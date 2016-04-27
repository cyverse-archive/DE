// road-runner
//
// Executes jobs based on a JSON blob serialized to a file.
// Each step of the job runs inside a Docker container. Job results are
// transferred back into iRODS with the porklock tool. Job status updates are
// posted to the **jobs.updates** topic in the **jobs** exchange.
package main

import (
	"configurate"
	"dockerops"
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"logcabin"
	"messaging"
	"model"
	"os"
	"os/signal"
	"path"
	"syscall"
	"time"

	"github.com/streadway/amqp"
)

var (
	version   = flag.Bool("version", false, "Print the version information")
	jobFile   = flag.String("job", "", "The path to the job description file")
	cfgPath   = flag.String("config", "", "The path to the config file")
	writeTo   = flag.String("write-to", "/opt/image-janitor", "The directory to copy job files to.")
	dockerURI = flag.String("docker", "unix:///var/run/docker.sock", "The URI for connecting to docker.")
	gitref    string
	appver    string
	builtby   string
	job       *model.Job
	dckr      *dockerops.Docker
	client    *messaging.Client
)

func signals() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt, os.Kill, syscall.SIGTERM, syscall.SIGSTOP, syscall.SIGQUIT)
	go func() {
		sig := <-c
		logcabin.Info.Println("Received signal:", sig)
		if dckr == nil {
			logcabin.Warning.Println("Docker client is nil, can't clean up. Probably don't need to.")
		}
		if job == nil {
			logcabin.Warning.Println("Info didn't get parsed from the job file, can't clean up. Probably don't need to.")
		}
		if dckr != nil && job != nil {
			cleanup(job)
		}
		if client != nil && job != nil {
			fail(client, job, fmt.Sprintf("Received signal %s", sig))
		}
		os.Exit(-1)
	}()
}

func init() {
	flag.Parse()
	signals()
	logcabin.Init("road-runner", "road-runner")
}

// AppVersion prints version information to stdout
func AppVersion() {
	if appver != "" {
		fmt.Printf("App-Version: %s\n", appver)
	}
	if gitref != "" {
		fmt.Printf("Git-Ref: %s\n", gitref)
	}

	if builtby != "" {
		fmt.Printf("Built-By: %s\n", builtby)
	}
}

func hostname() string {
	h, err := os.Hostname()
	if err != nil {
		logcabin.Error.Printf("Couldn't get the hostname: %s", err.Error())
		return ""
	}
	return h
}

func fail(client *messaging.Client, job *model.Job, msg string) error {
	logcabin.Error.Print(msg)
	return client.PublishJobUpdate(&messaging.UpdateMessage{
		Job:     job,
		State:   messaging.FailedState,
		Message: msg,
		Sender:  hostname(),
	})
}

func success(client *messaging.Client, job *model.Job) error {
	logcabin.Info.Print("Job success")
	return client.PublishJobUpdate(&messaging.UpdateMessage{
		Job:    job,
		State:  messaging.SucceededState,
		Sender: hostname(),
	})
}

func running(client *messaging.Client, job *model.Job, msg string) {
	err := client.PublishJobUpdate(&messaging.UpdateMessage{
		Job:     job,
		State:   messaging.RunningState,
		Message: msg,
		Sender:  hostname(),
	})
	if err != nil {
		logcabin.Error.Print(err)
	}
	logcabin.Info.Print(msg)
}

// TimeTracker tracks when road-runner should exit.
type TimeTracker struct {
	Timer   *time.Timer
	EndDate time.Time
}

// NewTimeTracker returns a new *TimeTracker.
func NewTimeTracker(d time.Duration, exitFunc func()) *TimeTracker {
	endDate := time.Now().Add(d)
	exitTimer := time.AfterFunc(d, exitFunc)
	return &TimeTracker{
		EndDate: endDate,
		Timer:   exitTimer,
	}
}

// ApplyDelta generates a new end date and modifies the time with the passed-in
// duration.
func (t *TimeTracker) ApplyDelta(deltaDuration time.Duration) error {
	//apply the new duration to the current end date.
	newEndDate := t.EndDate.Add(deltaDuration)

	//create a new duration that is the difference between the new end date and now.
	newDuration := t.EndDate.Sub(time.Now())

	//modify the Timer to use the new duration.
	wasActive := t.Timer.Reset(newDuration)

	//set the new enddate
	t.EndDate = newEndDate

	if !wasActive {
		return errors.New("Timer was not active")
	}
	return nil
}

// RegisterTimeLimitDeltaListener sets a function that listens for TimeLimitDelta
// messages on the given client.
func RegisterTimeLimitDeltaListener(client *messaging.Client, timeTracker *TimeTracker, invID string) {
	client.AddDeletableConsumer(messaging.JobsExchange, "topic", messaging.TimeLimitDeltaQueueName(invID), messaging.TimeLimitDeltaRequestKey(invID), func(d amqp.Delivery) {
		d.Ack(false)
		running(client, job, "Received delta request")
		deltaMsg := &messaging.TimeLimitDelta{}
		err := json.Unmarshal(d.Body, deltaMsg)
		if err != nil {
			running(client, job, fmt.Sprintf("Failed to unmarshal time limit delta: %s", err.Error()))
			return
		}
		newDuration, err := time.ParseDuration(deltaMsg.Delta)
		if err != nil {
			running(client, job, fmt.Sprintf("Failed to parse duration string from message: %s", err.Error()))
			return
		}
		err = timeTracker.ApplyDelta(newDuration)
		if err != nil {
			running(client, job, fmt.Sprintf("Failed to apply time limit delta: %s", err.Error()))
			return
		}
		running(client, job, fmt.Sprintf("Applied time delta of %s. New end date is %s", deltaMsg.Delta, timeTracker.EndDate.UTC().String()))
	})
}

// RegisterTimeLimitRequestListener sets a function that listens for
// TimeLimitRequest messages on the given client.
func RegisterTimeLimitRequestListener(client *messaging.Client, timeTracker *TimeTracker, invID string) {
	client.AddDeletableConsumer(messaging.JobsExchange, "topic", messaging.TimeLimitRequestQueueName(invID), messaging.TimeLimitRequestKey(invID), func(d amqp.Delivery) {
		d.Ack(false)
		running(client, job, "Received time limit request")
		timeLeft := int64(timeTracker.EndDate.Sub(time.Now())) / int64(time.Millisecond)
		err := client.SendTimeLimitResponse(invID, timeLeft)
		if err != nil {
			running(client, job, fmt.Sprintf("Failed to send time limit response: %s", err.Error()))
			return
		}
		running(client, job, fmt.Sprintf("Sent message saying that time left is %dms", timeLeft))
	})
}

// RegisterTimeLimitResponseListener sets a function that handles messages that
// are sent on the jobs exchange with the key for time limit responses. This
// service doesn't need these messages, this is just here to force the queue
// to get cleaned up when road-runner exits.
func RegisterTimeLimitResponseListener(client *messaging.Client, invID string) {
	client.AddDeletableConsumer(messaging.JobsExchange, "topic", messaging.TimeLimitResponsesQueueName(invID), messaging.TimeLimitResponsesKey(invID), func(d amqp.Delivery) {
		d.Ack(false)
		logcabin.Info.Print(string(d.Body))
	})
}

// RegisterStopRequestListener sets a function that responses to StopRequest
// messages.
func RegisterStopRequestListener(client *messaging.Client, exit chan messaging.StatusCode, invID string) {
	client.AddDeletableConsumer(messaging.JobsExchange, "topic", messaging.StopQueueName(invID), messaging.StopRequestKey(invID), func(d amqp.Delivery) {
		d.Ack(false)
		running(client, job, "Received stop request")
		exit <- messaging.StatusKilled
	})
}

func copyJobFile(uuid, from, toDir string) error {
	inputReader, err := os.Open(from)
	if err != nil {
		return err
	}
	outputFilePath := path.Join(toDir, fmt.Sprintf("%s.json", uuid))
	outputWriter, err := os.Create(outputFilePath)
	if err != nil {
		return err
	}
	if _, err := io.Copy(outputWriter, inputReader); err != nil {
		return err
	}
	return nil
}

func deleteJobFile(uuid, toDir string) {
	filePath := path.Join(toDir, fmt.Sprintf("%s.json", uuid))
	if err := os.Remove(filePath); err != nil {
		logcabin.Error.Print(err)
	}
}

func main() {
	var err error
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if *cfgPath == "" {
		logcabin.Error.Fatal("--config must be set.")
	}
	logcabin.Info.Printf("Reading config from %s", *cfgPath)
	if _, err = os.Open(*cfgPath); err != nil {
		logcabin.Error.Fatal(*cfgPath)
	}
	err = configurate.Init(*cfgPath)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Printf("Done reading config from %s", *cfgPath)
	if *jobFile == "" {
		logcabin.Error.Fatal("--job must be set.")
	}
	data, err := ioutil.ReadFile(*jobFile)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	job, err = model.NewFromData(data)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	if _, err = os.Open(*writeTo); err != nil {
		logcabin.Error.Fatal(err)
	}
	if err = copyJobFile(job.InvocationID, *jobFile, *writeTo); err != nil {
		logcabin.Error.Fatal(err)
	}

	uri, err := configurate.C.String("amqp.uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	client, err = messaging.NewClient(uri, true)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer client.Close()

	client.SetupPublishing(messaging.JobsExchange)

	dckr, err = dockerops.NewDocker(*dockerURI)
	if err != nil {
		fail(client, job, "Failed to connect to local docker socket")
		logcabin.Error.Fatal(err)
	}

	// The channel that the exit code will be passed along on.
	exit := make(chan messaging.StatusCode)

	// Could probably reuse the exit channel, but that's less explicit.
	finalExit := make(chan messaging.StatusCode)

	// Launch the go routine that will handle job exits by signal or timer.
	go Exit(exit, finalExit)

	//The default time limt for the jobs.
	// defaultDuration, err := time.ParseDuration("48h")
	// if err != nil {
	// 	fail(client, job, "Failed to parse default duration")
	// 	logcabin.Error.Fatal(err)
	// }

	//Set up the self destruct timer. All this does is fire off a message on the
	//exit channel that is listened to in the goroutine running Exit().
	// timeTracker := NewTimeTracker(defaultDuration, func() {
	// 	exit <- messaging.StatusTimeLimit
	// })

	go client.Listen()

	// RegisterTimeLimitDeltaListener(client, timeTracker, job.InvocationID)
	// RegisterTimeLimitRequestListener(client, timeTracker, job.InvocationID)
	RegisterStopRequestListener(client, exit, job.InvocationID)
	// RegisterTimeLimitResponseListener(client, job.InvocationID)

	err = os.Mkdir("logs", 0755)
	if err != nil {
		logcabin.Error.Print(err)
	}
	if err = writeJobSummary("logs", job); err != nil {
		logcabin.Error.Print(err)
	}
	if err = writeJobParameters("logs", job); err != nil {
		logcabin.Error.Print(err)
	}

	go Run(client, dckr, exit)
	exitCode := <-finalExit
	deleteJobFile(job.InvocationID, *writeTo)
	os.Exit(int(exitCode))
}
