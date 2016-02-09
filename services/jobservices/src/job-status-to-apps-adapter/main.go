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
	"sync"
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
	appsURI string
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
	CompletionDate string             `json:"completion_date,omitempty"`
	UUID           string             `json:"uuid"`
}

// JobStatusUpdateWrapper wraps a JobStatusUpdate
type JobStatusUpdateWrapper struct {
	State JobStatusUpdate `json:"state"`
}

/*
Each new job gets:
  * An influx goroutine
	* A message buffer
	* An exit handling goroutine
	* An input channel
	* An exit channel.

Each goroutine is entered into a map, which the invocation ID as the key and the channel as the value.

Each message's invocation ID is used to figure out which channel to send the message out on.

Access to the map is controlled with locks.
*/

// JobTracker maps InvocationIDs to channels and spins up JobMessageHandlers.
type JobTracker struct {
	JobMap map[string]chan messaging.UpdateMessage
	Locker *sync.Mutex
}

// NewJobTracker returns a new *JobTracker
func NewJobTracker() *JobTracker {
	return &JobTracker{
		JobMap: make(map[string]chan messaging.UpdateMessage),
		Locker: &sync.Mutex{},
	}
}

// HandleMessage is the function that handles a delivery and passes it off to a
// JobMessageHandler.
func (t *JobTracker) HandleMessage(d amqp.Delivery) {
	d.Ack(false)
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
	if _, ok := t.JobMap[update.Job.InvocationID]; !ok {
		logger.Printf("Job map did not have a match for %s, creating entry", update.Job.InvocationID)
		mh := NewJobMessageHandler(t, update.Job.InvocationID)
		logger.Printf("Locking job map for %s", update.Job.InvocationID)
		t.Locker.Lock()
		t.JobMap[update.Job.InvocationID] = mh.In
		t.Locker.Unlock()
		logger.Printf("Unlocking job map for %s", update.Job.InvocationID)
	}
	t.JobMap[update.Job.InvocationID] <- *update
	// Check if invocation ID is in the map.
	// If no:
	//   Create a New JobMessageHandler
	//   Lock the map
	//   Register the JobMessageHandler's channel in JobMap.
	//   Unlock the map.
	// Send the message to the channel
}

// JobMessageHandler maintains a buffer of job messages in the order that they're
// received and propagates each message up to the apps service.
type JobMessageHandler struct {
	tracker      *JobTracker
	InvocationID string
	In           chan messaging.UpdateMessage
	Exit         chan int
	queueLock    *sync.Mutex
	Queue        []messaging.UpdateMessage
}

// NewJobMessageHandler returns a new JobMessageHandler
func NewJobMessageHandler(jt *JobTracker, invID string) *JobMessageHandler {
	var q []messaging.UpdateMessage
	i := make(chan messaging.UpdateMessage)
	e := make(chan int)
	jmh := &JobMessageHandler{
		tracker:      jt,
		InvocationID: invID,
		Queue:        q,
		queueLock:    &sync.Mutex{},
		In:           i,
		Exit:         e,
	}
	go jmh.launch()
	return jmh
}

// launch sets up the message handling logic for a JobMessageHandler. This is
// where updates get propagated up to the apps service.
func (h *JobMessageHandler) launch() {
	notifications := make(chan int)
	quitListening := make(chan int)

	// Start up exit handler
	go func() {
		select {
		case <-h.Exit:
			logger.Printf("Received message in exit goroutine for job %s", h.InvocationID)
			h.Queue = nil
			logger.Printf("Locking the job map in the exit goroutine for job %s", h.InvocationID)
			h.tracker.Locker.Lock()
			logger.Printf("Deleting entry from job map in the exit goroutine for job %s", h.InvocationID)
			delete(h.tracker.JobMap, h.InvocationID)
			h.tracker.Locker.Unlock()
			logger.Printf("Unlocking the job map in the exit goroutine for job %s", h.InvocationID)
			quitListening <- 1 // tell the other goroutine to exit
			return
		}
	}()

	// Start goroutine that pushes jobs into the queue from the input channel
	go func() {
		logger.Printf("Starting goroutine that reads messages from the input channel for job %s", h.InvocationID)
		for {
			select {
			case msg := <-h.In:
				logger.Printf("Input goroutine got a message for job %s", h.InvocationID)
				m := msg
				logger.Printf("Locking the queue for the input goroutine for job %s", h.InvocationID)
				h.queueLock.Lock()
				h.Queue = append(h.Queue, m)
				h.queueLock.Unlock()
				logger.Printf("Unlocked the queue for the input goroutine for job %s", h.InvocationID)
				notifications <- 1
			case <-quitListening: // exit handler told this goroutine to exit
				logger.Printf("Received exit message in input goroutine for job %s", h.InvocationID)
				return
			}
		}
	}()

	// for-select on the notification channel.
	//   for each loop, pull a message off of the queue
	for {
		select {
		case <-notifications:
			logger.Printf("Received message in notification loop for job %s", h.InvocationID)
			var update messaging.UpdateMessage
			logger.Printf("Locking the queue in the notification loop for job %s", h.InvocationID)
			h.queueLock.Lock()
			logger.Printf("Length of queue in the notification loop for job %s: %d", h.InvocationID, len(h.Queue))
			if len(h.Queue) > 0 {
				update, h.Queue = h.Queue[0], h.Queue[1:]
			}
			h.queueLock.Unlock()
			logger.Printf("Unlocked queue in the notification loop for job %s", h.InvocationID)
			if update.State != "" && update.Job.InvocationID != "" {
				jsu := JobStatusUpdate{
					Status: update.State,
					UUID:   update.Job.InvocationID,
				}
				if jsu.Status == messaging.SucceededState || jsu.Status == messaging.FailedState {
					jsu.CompletionDate = fmt.Sprintf("%d", time.Now().UnixNano()/int64(time.Millisecond))
				}
				jsuw := JobStatusUpdateWrapper{
					State: jsu,
				}
				logger.Printf("Job status in the notification loop for job %s is: %#v", h.InvocationID, jsuw)
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
				logger.Printf("Sending job status to %s in the notification loop for job %s", appsURI, h.InvocationID)
				resp, err := http.Post(appsURI, "application/json", buf)
				if err != nil {
					logger.Printf("Error sending job status to %s in the notification loop for job %s: %#v", appsURI, h.InvocationID, err)
					return
				}
				defer resp.Body.Close()
				logger.Printf("Response from %s in the notification loop for job %s is: %#v", appsURI, h.InvocationID, err)
			} else {
				if update.State == "" {
					logger.Printf("The update's state was blank in the notification loop for job %s", h.InvocationID)
				}
				if update.Job.InvocationID == "" {
					logger.Printf("The update's invocation ID was blank in the notification loop for job %s", h.InvocationID)
				}
			}
		}
	}
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
	appsURI, err = configurate.C.String("apps.callbacks_uri")
	if err != nil {
		log.Fatal(err)
	}
	client, err := messaging.NewClient(uri, true)
	if err != nil {
		logger.Fatal(err)
	}
	defer client.Close()

	jt := NewJobTracker()
	client.AddConsumer(messaging.JobsExchange, "job_status_to_apps_adapter", messaging.UpdatesKey, jt.HandleMessage)
	client.Listen()
}
