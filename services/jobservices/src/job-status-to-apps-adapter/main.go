package main

import (
	"bytes"
	"configurate"
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"logcabin"
	"messaging"
	"net/http"
	"os"
	"time"

	"github.com/streadway/amqp"
)

var (
	logger  = logcabin.New()
	cfgPath = flag.String("config", "", "Path to the config file. Required.")
	version = flag.Bool("version", false, "Print the version information")
	gitref  string
	appver  string
	builtby string
)

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

func init() {
	flag.Parse()
}

// JobStatusUpdate contains the data POSTed to the apps service.
type JobStatusUpdate struct {
	Status         messaging.JobState `json:"status"`
	CompletionDate time.Time          `json:"completion_date,omitempty"`
	UUID           string             `json:"uuid"`
}

// JobStatusUpdateWrapper wraps a JobStatusUpdate
type JobStatusUpdateWrapper struct {
	State JobStatusUpdate `json:"state"`
}

func main() {
	if *version {
		AppVersion()
		os.Exit(0)
	}
	if *cfgPath == "" {
		fmt.Println("Error: --config must be set.")
		flag.PrintDefaults()
		os.Exit(-1)
	}
	err := configurate.Init(*cfgPath)
	if err != nil {
		logger.Print(err)
		os.Exit(-1)
	}
	logger.Println("Done reading config.")

	uri, err := configurate.C.String("amqp.uri")
	if err != nil {
		log.Fatal(err)
	}
	appsURI, err := configurate.C.String("apps.callback_uri")
	if err != nil {
		log.Fatal(err)
	}
	client, err := messaging.NewClient(uri, true)
	if err != nil {
		logger.Fatal(err)
	}
	defer client.Close()

	client.AddConsumer(messaging.JobsExchange, "job_status_to_apps_adapter", messaging.UpdatesKey, func(d amqp.Delivery) {
		d.Ack(false) // It is acked, the false means it doesn't batch ack messages
		logger.Println("Message received")
		update := &messaging.UpdateMessage{}
		err := json.Unmarshal(d.Body, update)
		if err != nil {
			logger.Print(err)
			return
		}
		if update.State == "" {
			logger.Println("State was unset, dropping update")
			return
		}
		logger.Printf("State is %s\n", update.State)
		if update.Job.InvocationID == "" {
			logger.Println("InvocationID was unset, dropping update")
			return
		}
		jsu := JobStatusUpdate{
			Status: update.State,
			UUID:   update.Job.InvocationID,
		}
		if jsu.Status == messaging.SucceededState || jsu.Status == messaging.FailedState {
			jsu.CompletionDate = time.Now()
		}
		jsuw := JobStatusUpdateWrapper{
			State: jsu,
		}
		msg, err := json.Marshal(jsuw)
		if err != nil {
			logger.Print(err)
			return
		}
		buf := bytes.NewBuffer(msg)
		if err != nil {
			logger.Print(err)
			return
		}
		resp, err := http.Post(appsURI, "application/json", buf)
		if err != nil {
			logger.Print(err)
			return
		}
		defer resp.Body.Close()
	})
	client.Listen()
}
