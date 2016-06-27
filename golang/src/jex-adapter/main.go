// jex-adapter
//
// jex-adapter allows the apps service to submit job requests through an AMQP
// broker by implementing the portion of the old JEX API that apps interacted
// with. Instead of writing the files out to disk and calling condor_submit like
// the JEX service did, it serializes the request as JSON and pushes it out
// as a message on the "jobs" exchange with a routing key of "jobs.launches".
//
package main

import (
	"configurate"
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"logcabin"
	"messaging"
	"model"
	"net/http"
	"os"
	"version"

	"github.com/gorilla/mux"
	"github.com/olebedev/config"
	"github.com/streadway/amqp"
)

// JEXAdapter contains the application state for jex-adapter.
type JEXAdapter struct {
	cfg    *config.Config
	client *messaging.Client
}

// New returns a *JEXAdapter
func New(cfg *config.Config) *JEXAdapter {
	return &JEXAdapter{
		cfg: cfg,
	}
}

func amqpError(err error) {
	if err.(*amqp.Error).Code != 0 {
		logcabin.Error.Fatal(err)
	}
}

func (j *JEXAdapter) home(writer http.ResponseWriter, request *http.Request) {
	fmt.Fprintf(writer, "Welcome to the JEX.\n")
}

func (j *JEXAdapter) stop(writer http.ResponseWriter, request *http.Request) {
	var (
		invID string
		ok    bool
		err   error
		v     = mux.Vars(request)
	)

	logcabin.Info.Printf("Request received:\n%#v\n", request)

	logcabin.Info.Println("Getting invocation ID out of the Vars")
	if invID, ok = v["invocation_id"]; !ok {
		http.Error(writer, "Missing job id in URL", http.StatusBadRequest)
		logcabin.Error.Print("Missing job id in URL")
		return
	}
	logcabin.Info.Printf("Invocation ID is %s\n", invID)

	logcabin.Info.Println("Sending stop request")
	err = j.client.SendStopRequest(invID, "root", "because I said to")
	if err != nil {
		http.Error(
			writer,
			fmt.Sprintf("Error sending stop request %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
		return
	}
	logcabin.Info.Println("Done sending stop request")
}

func (j *JEXAdapter) launch(writer http.ResponseWriter, request *http.Request) {
	bodyBytes, err := ioutil.ReadAll(request.Body)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(writer, "Request had no body", http.StatusBadRequest)
		return
	}

	job, err := model.NewFromData(j.cfg, bodyBytes)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Failed to create job from json: %s", err.Error()),
			http.StatusBadRequest,
		)
		return
	}

	// Create the time limit delta channel
	timeLimitDeltaChannel, err := j.client.CreateQueue(
		messaging.TimeLimitDeltaQueueName(job.InvocationID),
		messaging.JobsExchange,
		messaging.TimeLimitDeltaRequestKey(job.InvocationID),
		false,
		true,
	)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating time limit delta request queue: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
	}
	defer timeLimitDeltaChannel.Close()

	// Create the time limit request channel
	timeLimitRequestChannel, err := j.client.CreateQueue(
		messaging.TimeLimitRequestQueueName(job.InvocationID),
		messaging.JobsExchange,
		messaging.TimeLimitRequestKey(job.InvocationID),
		false,
		true,
	)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating time limit request queue: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
	}
	defer timeLimitRequestChannel.Close()

	// Create the time limit response channel
	timeLimitResponseChannel, err := j.client.CreateQueue(
		messaging.TimeLimitResponsesQueueName(job.InvocationID),
		messaging.JobsExchange,
		messaging.TimeLimitResponsesKey(job.InvocationID),
		false,
		true,
	)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating time limit response queue: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
	}
	defer timeLimitResponseChannel.Close()

	// Create the stop request channel
	stopRequestChannel, err := j.client.CreateQueue(
		messaging.StopQueueName(job.InvocationID),
		messaging.JobsExchange,
		messaging.StopRequestKey(job.InvocationID),
		false,
		true,
	)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating stop request queue: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
	}
	defer stopRequestChannel.Close()

	launchRequest := messaging.NewLaunchRequest(job)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating launch request: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
		return
	}

	launchJSON, err := json.Marshal(launchRequest)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating launch request JSON: %s", err.Error()),
			http.StatusInternalServerError,
		)
		return
	}

	err = j.client.Publish(messaging.LaunchesKey, launchJSON)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error publishing launch request: %s", err.Error()),
			http.StatusInternalServerError,
		)
		amqpError(err)
		return
	}
}

//Previewer contains a list of params that need to be constructed into a
//command-line preview.
type Previewer struct {
	Params model.PreviewableStepParam `json:"params"`
}

// Preview returns the command-line preview as a string.
func (p *Previewer) Preview() string {
	return p.Params.String()
}

//PreviewerReturn is what the arg-preview endpoint returns.
type PreviewerReturn struct {
	Params string `json:"params"`
}

func (j *JEXAdapter) preview(writer http.ResponseWriter, request *http.Request) {
	bodyBytes, err := ioutil.ReadAll(request.Body)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(writer, "Request had no body", http.StatusBadRequest)
		return
	}

	previewer := &Previewer{}
	err = json.Unmarshal(bodyBytes, previewer)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error parsing preview JSON: %s", err.Error()),
			http.StatusBadRequest,
		)
		return
	}

	var paramMap PreviewerReturn
	paramMap.Params = previewer.Params.String()
	outgoingJSON, err := json.Marshal(paramMap)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error creating response JSON: %s", err.Error()),
			http.StatusInternalServerError,
		)
		return
	}

	_, err = writer.Write(outgoingJSON)
	if err != nil {
		logcabin.Error.Print(err)
		http.Error(
			writer,
			fmt.Sprintf("Error writing response: %s", err.Error()),
			http.StatusInternalServerError,
		)
		return
	}
}

// NewRouter returns a newly configured *mux.Router.
func (j *JEXAdapter) NewRouter() *mux.Router {
	router := mux.NewRouter()
	router.HandleFunc("/", j.home).Methods("GET")
	router.HandleFunc("/", j.launch).Methods("POST")
	router.HandleFunc("/stop/{invocation_id}", j.stop).Methods("DELETE")
	router.HandleFunc("/arg-preview", j.preview).Methods("POST")
	return router
}

func main() {
	var (
		showVersion = flag.Bool("version", false, "Print version information")
		cfgPath     = flag.String("config", "", "Path to the configuration file")
		addr        = flag.String("addr", ":60000", "The port to listen on for HTTP requests")
		amqpURI     string
	)

	flag.Parse()

	logcabin.Init("jex-adapter", "jex-adapter")

	if *showVersion {
		version.AppVersion()
		os.Exit(0)
	}

	if *cfgPath == "" {
		fmt.Println("--config is required")
		flag.PrintDefaults()
		os.Exit(-1)
	}

	cfg, err := configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	amqpURI, err = cfg.String("amqp.uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	app := New(cfg)

	app.client, err = messaging.NewClient(amqpURI, false)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer app.client.Close()

	app.client.SetupPublishing(messaging.JobsExchange)

	router := app.NewRouter()
	logcabin.Error.Fatal(http.ListenAndServe(*addr, router))
}
