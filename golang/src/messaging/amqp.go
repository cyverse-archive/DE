// Package messaging provides the logic and data structures that the services
// will need to communicate with each other over AMQP (as implemented
// by RabbitMQ).
package messaging

import (
	"encoding/json"
	"fmt"
	"logcabin"
	"math/rand"
	"model"
	"os"
	"strconv"
	"time"

	"github.com/streadway/amqp"
)

//Command is tells the receiver of a JobRequest which action to perform
type Command int

// JobState defines a valid state for a job.
type JobState string

// StatusCode defines a valid exit code for a job.
type StatusCode int

var (
	//LaunchCommand is the string used in LaunchCo
	LaunchCommand = "LAUNCH"

	//JobsExchange is the name of the exchange that job related info is passed around.
	JobsExchange = "jobs"

	//ReindexExchange is the name of the exchange that full-reindex info is passed around on.
	ReindexExchange = "de"

	//IncrementalExchange is the name of the exchange that incremental update info is passed around on.
	IncrementalExchange = "de"

	//ReindexAllKey is the routing/binding key for full reindex messages.
	ReindexAllKey = "index.all"

	//ReindexTemplatesKey is the routing/binding key for templates reindex messages.
	ReindexTemplatesKey = "index.templates"

	//IncrementalKey is the routing/binding key for incremental updates
	IncrementalKey = "metadata.update"

	//LaunchesKey is the routing/binding key for job launch request messages.
	LaunchesKey = "jobs.launches"

	//UpdatesKey is the routing/binding key for job update messages.
	UpdatesKey = "jobs.updates"

	//StopsKey is the routing/binding key for job stop request messages.
	StopsKey = "jobs.stops"

	//CommandsKey is the routing/binding key for job command messages.
	CommandsKey = "jobs.commands"

	// TimeLimitRequestsKey is the routing/binding key for the job time limit messages.
	TimeLimitRequestsKey = "jobs.timelimits.requests"

	//TimeLimitDeltaKey is the routing/binding key for the job time limit delta messages.
	TimeLimitDeltaKey = "jobs.timelimits.deltas"

	//TimeLimitResponseKey is the routing/binding key for the job time limit
	//response messages.
	TimeLimitResponseKey = "jobs.timelimits.responses"

	//QueuedState is when a job is queued.
	QueuedState JobState = "Queued"

	//SubmittedState is when a job has been submitted.
	SubmittedState JobState = "Submitted"

	//RunningState is when a job is running.
	RunningState JobState = "Running"

	//SucceededState is when a job has successfully completed the required steps.
	SucceededState JobState = "Completed"

	//FailedState is when a job has failed. Duh.
	FailedState JobState = "Failed"
)

const (
	//Launch tells the receiver of a JobRequest to launch the job
	Launch Command = iota

	//Stop tells the receiver of a JobRequest to stop a job
	Stop
)

const (
	// Success is the exit code used when the required commands execute correctly.
	Success StatusCode = iota

	// StatusDockerPullFailed is the exit code when a 'docker pull' fails.
	StatusDockerPullFailed

	// StatusDockerCreateFailed is the exit code when a 'docker create' fails.
	StatusDockerCreateFailed

	// StatusInputFailed is the exit code when an input download fails.
	StatusInputFailed

	// StatusStepFailed is the exit code when a step in the job fails.
	StatusStepFailed

	// StatusOutputFailed is the exit code when the output upload fails.
	StatusOutputFailed

	// StatusKilled is the exit code when the job is killed.
	StatusKilled

	// StatusTimeLimit is the exit code when the job is killed due to the time
	// limit being reached.
	StatusTimeLimit

	// StatusBadDuration is the exit code when the job is killed because an
	// unparseable job duration was sent to it.
	StatusBadDuration
)

// JobRequest is a generic request type for job related requests.
type JobRequest struct {
	Job     *model.Job
	Command Command
	Message string
	Version int
}

// StopRequest contains the information needed to stop a job
type StopRequest struct {
	Reason       string
	Username     string
	Version      int
	InvocationID string
}

// UpdateMessage contains the information needed to broadcast a change in state
// for a job.
type UpdateMessage struct {
	Job     *model.Job
	Version int
	State   JobState
	Message string
	SentOn  string // Should be the milliseconds since the epoch
	Sender  string // Should be the hostname of the box sending the message.
}

// TimeLimitRequest is the message that is sent to road-runner to get it to
// broadcast its current time limit.
type TimeLimitRequest struct {
	InvocationID string
}

// TimeLimitRequestKey returns the formatted binding key based on the passed in
// job InvocationID.
func TimeLimitRequestKey(invID string) string {
	return fmt.Sprintf("%s.%s", TimeLimitRequestsKey, invID)
}

// TimeLimitRequestQueueName returns the formatted queue name for time limit
// requests. It is based on the passed in job InvocationID.
func TimeLimitRequestQueueName(invID string) string {
	return fmt.Sprintf("road-runner-%s-tl-request", invID)
}

// TimeLimitResponse is the message that is sent by road-runner in response
// to a TimeLimitRequest. It contains the current time limit from road-runner.
type TimeLimitResponse struct {
	InvocationID          string
	MillisecondsRemaining int64
}

// TimeLimitResponsesKey returns the formatted binding key based on the passed in
// job InvocationID.
func TimeLimitResponsesKey(invID string) string {
	return fmt.Sprintf("%s.%s", TimeLimitResponseKey, invID)
}

// TimeLimitResponsesQueueName returns the formatted queue name for time limit
// responses. It is based on the passed in job InvocationID.
func TimeLimitResponsesQueueName(invID string) string {
	return fmt.Sprintf("road-runner-%s-tl-response", invID)
}

// TimeLimitDelta is the message that is sent to get road-runner to change its
// time limit. The 'Delta' field contains a string in Go's Duration string
// format. More info on the format is available here:
// https://golang.org/pkg/time/#ParseDuration
type TimeLimitDelta struct {
	InvocationID string
	Delta        string
}

// TimeLimitDeltaRequestKey returns the binding key formatted correctly for the
// jobs exchange based on the InvocationID passed in.
func TimeLimitDeltaRequestKey(invID string) string {
	return fmt.Sprintf("%s.%s", TimeLimitDeltaKey, invID)
}

// TimeLimitDeltaQueueName returns the correctly formatted queue name for time
// limit delta requests. It's based on the passed in string, which is assumed to
// be the InvocationID for a job, but there's no reason that is required to be
// the case.
func TimeLimitDeltaQueueName(invID string) string {
	return fmt.Sprintf("road-runner-%s-tl-delta", invID)
}

// NewStopRequest returns a *JobRequest that has been constructed to be a
// stop request for a running job.
func NewStopRequest() *StopRequest {
	return &StopRequest{
		Version: 0,
	}
}

// StopRequestKey returns the binding key formatted correctly for the jobs
// exchange based on the InvocationID passed in.
func StopRequestKey(invID string) string {
	return fmt.Sprintf("%s.%s", StopsKey, invID)
}

// StopQueueName returns the formatted queue name for job stop requests. It's
// based on the passed in string, which is assumed to be the InvocationID for a
// job, but there's no reason that is required to the case.
func StopQueueName(invID string) string {
	return fmt.Sprintf("road-runner-%s-stops-request", invID)
}

// NewLaunchRequest returns a *JobRequest that has been constructed to be a
// launch request for the provided job.
func NewLaunchRequest(j *model.Job) *JobRequest {
	return &JobRequest{
		Job:     j,
		Command: Launch,
		Version: 0,
	}
}

// MessageHandler defines a type for amqp.Delivery handlers.
type MessageHandler func(amqp.Delivery)

type aggregationMessage struct {
	handler  MessageHandler
	delivery amqp.Delivery
}

type consumer struct {
	exchange        string
	exchangeType    string
	queue           string
	key             string
	handler         MessageHandler
	queueDurable    bool
	queueAutoDelete bool
}

type consumeradder struct {
	consumer consumer
	latch    chan int
}

type publisher struct {
	exchange string
	channel  *amqp.Channel
}

// Client encapsulates the information needed to interact via AMQP.
type Client struct {
	uri             string
	connection      *amqp.Connection
	aggregationChan chan aggregationMessage
	errors          chan *amqp.Error
	consumers       []*consumer
	consumersChan   chan consumeradder
	publisher       *publisher
	Reconnect       bool
}

// NewClient returns a new *Client. It will block until the connection succeeds.
func NewClient(uri string, reconnect bool) (*Client, error) {
	c := &Client{}
	randomizer := rand.New(rand.NewSource(time.Now().UnixNano()))
	c.uri = uri
	c.Reconnect = reconnect
	logcabin.Info.Println("Attempting AMQP connection...")
	var connection *amqp.Connection
	var err error
	if c.Reconnect {
		for {
			connection, err = amqp.Dial(c.uri)
			if err != nil {
				logcabin.Error.Print(err)
				waitFor := randomizer.Intn(10)
				logcabin.Info.Printf("Re-attempting connection in %d seconds", waitFor)
				time.Sleep(time.Duration(waitFor) * time.Second)
			} else {
				logcabin.Info.Println("Successfully connected to the AMQP broker")
				break
			}
		}
	} else {
		connection, err = amqp.Dial(c.uri)
		if err != nil {
			return nil, err
		}
		logcabin.Info.Println("Successfully connected to the AMQP broker")
	}
	c.connection = connection
	c.consumersChan = make(chan consumeradder)
	c.aggregationChan = make(chan aggregationMessage)
	c.errors = c.connection.NotifyClose(make(chan *amqp.Error))
	return c, nil
}

// Listen will wait for messages and pass them off to handlers, which run in
// their own goroutine.
func (c *Client) Listen() {
	var consumers []*consumer
	// init := func() {
	// 	for _, cs := range c.consumers {
	// 		c.initconsumer(cs)
	// 	}
	// }
	// init()
	// for _, cs := range c.consumers {
	// 	consumers = append(consumers, cs)
	// }
	for {
		select {
		case cs := <-c.consumersChan:
			logcabin.Info.Println("A new consumer is being added")
			c.initconsumer(&cs.consumer)
			consumers = append(consumers, &cs.consumer)
			logcabin.Info.Println("Done adding a new consumer")
			cs.latch <- 1
		case err := <-c.errors:
			logcabin.Error.Printf("An error in the connection to the AMQP broker occurred:\n%s", err)
			if c.Reconnect {
				c, _ = NewClient(c.uri, c.Reconnect)
				c.consumers = consumers
				for _, cs := range c.consumers {
					c.initconsumer(cs)
				}
				// init()
			} else {
				os.Exit(-1)
			}
		case msg := <-c.aggregationChan:
			go func(deliveryMsg aggregationMessage) {
				deliveryMsg.handler(deliveryMsg.delivery)
			}(msg)
		}
	}
}

// Close closes the connection to the AMQP broker.
func (c *Client) Close() {
	c.connection.Close()
}

// AddConsumer adds a consumer to the list of consumers that need to be created
// each time the client is set up. Note that this just adds the consumers to a
// list, it doesn't actually start handling messages yet. You need to call
// Listen() for that.
func (c *Client) AddConsumer(exchange, exchangeType, queue, key string, handler MessageHandler) {
	cs := consumer{
		exchange:        exchange,
		exchangeType:    exchangeType,
		queue:           queue,
		key:             key,
		handler:         handler,
		queueDurable:    true,
		queueAutoDelete: false,
	}
	adder := consumeradder{
		consumer: cs,
		latch:    make(chan int),
	}
	c.consumersChan <- adder
	<-adder.latch
}

// AddDeletableConsumer adds a consumer to the list of consumers that need to be
// created each time the client is set up. Unlike AddConsumer(), the new
// consumer will have auto-delete set to true and durable set to false. Make
// sure that Listen() has been called before calling this function.
func (c *Client) AddDeletableConsumer(exchange, exchangeType, queue, key string, handler MessageHandler) {
	cs := consumer{
		exchange:        exchange,
		exchangeType:    exchangeType,
		queue:           queue,
		key:             key,
		handler:         handler,
		queueDurable:    false,
		queueAutoDelete: true,
	}
	adder := consumeradder{
		consumer: cs,
		latch:    make(chan int),
	}
	c.consumersChan <- adder
	<-adder.latch
}

// CreateQueue creates a queue with the given name, durability, and auto-delete
// settings. It then binds it to the given exchange with the provided key. This
// function does not declare the exchange.
func (c *Client) CreateQueue(name, exchange, key string, durable, autoDelete bool) (*amqp.Channel, error) {
	channel, err := c.connection.Channel()
	if err != nil {
		return nil, err
	}

	if _, err = channel.QueueDeclare(
		name,
		durable,
		autoDelete,
		false, //internal
		false, //no wait
		nil,   //args
	); err != nil {
		return nil, err
	}

	if err = channel.QueueBind(
		name,
		key,
		exchange,
		false, //no wait
		nil,   //args
	); err != nil {
		return nil, err
	}
	return channel, nil
}

// QueueExists returns true if the given queue name exists, false or an error
// otherwise.
func (c *Client) QueueExists(name string) (bool, error) {
	channel, err := c.connection.Channel()
	if err != nil {
		return false, err
	}
	defer channel.Close()
	if _, err = channel.QueueInspect(name); err != nil {
		return false, err
	}
	return true, nil
}

// DeleteQueue deletes the queue with the given name without regards to safety.
func (c *Client) DeleteQueue(name string) error {
	channel, err := c.connection.Channel()
	if err != nil {
		return err
	}
	defer channel.Close()
	_, err = channel.QueueDelete(name, false, false, false)
	return err
}

func (c *Client) initconsumer(cs *consumer) error {
	channel, err := c.connection.Channel()
	if err != nil {
		return err
	}
	err = channel.ExchangeDeclare(
		cs.exchange,     //name
		cs.exchangeType, //kind
		true,            //durable
		false,           //auto-delete
		false,           //internal
		false,           //no-wait
		nil,             //args
	)
	_, err = channel.QueueDeclare(
		cs.queue,
		cs.queueDurable,    //durable
		cs.queueAutoDelete, //auto-delete
		false,              //internal
		false,              //no-wait
		nil,                //args
	)
	err = channel.QueueBind(
		cs.queue,
		cs.key,
		cs.exchange,
		false, //no-wait
		nil,   //args
	)

	d, err := channel.Consume(
		cs.queue,
		"",    //consumer tag - auto-assigned in this case
		false, //auto-ack
		false, //exclusive
		false, //no-local
		false, //no-wait
		nil,   //args
	)
	if err != nil {
		return err
	}
	go func() {
		for msg := range d {
			c.aggregationChan <- aggregationMessage{
				handler:  cs.handler,
				delivery: msg,
			}
		}
	}()
	return err
}

// SetupPublishing initializes the publishing functionality of the client.
// Call this before calling Publish.
func (c *Client) SetupPublishing(exchange string) error {
	channel, err := c.connection.Channel()
	if err != nil {
		return err
	}
	err = channel.ExchangeDeclare(
		exchange, //name
		"topic",  //kind
		true,     //durable
		false,    //auto-delete
		false,    //internal
		false,    //no-wait
		nil,      //args
	)
	if err != nil {
		return err
	}
	p := &publisher{
		exchange: exchange,
		channel:  channel,
	}
	c.publisher = p
	return err
}

// Publish sends a message to the configured exchange with a routing key set to
// the value of 'key'.
func (c *Client) Publish(key string, body []byte) error {
	msg := amqp.Publishing{
		DeliveryMode: amqp.Persistent,
		Timestamp:    time.Now(),
		ContentType:  "text/plain",
		Body:         body,
	}
	err := c.publisher.channel.Publish(
		c.publisher.exchange,
		key,
		false, //mandatory
		false, //immediate
		msg,
	)
	return err
}

// PublishJobUpdate sends a mess to the configured exchange with a routing key of
// "jobs.updates"
func (c *Client) PublishJobUpdate(u *UpdateMessage) error {
	if u.SentOn == "" {
		u.SentOn = strconv.FormatInt(time.Now().UnixNano()/int64(time.Millisecond), 10)
	}
	msgJSON, err := json.Marshal(u)
	if err != nil {
		return err
	}
	return c.Publish(UpdatesKey, msgJSON)
}

// SendTimeLimitRequest sends out a message to the job on the
// "jobs.timelimits.requests.<invocationID>" topic. This should trigger the job
// to emit a TimeLimitResponse.
func (c *Client) SendTimeLimitRequest(invID string) error {
	req := &TimeLimitRequest{
		InvocationID: invID,
	}
	msg, err := json.Marshal(req)
	if err != nil {
		return err
	}
	return c.Publish(TimeLimitRequestKey(invID), msg)
}

// SendTimeLimitResponse sends out a message to the
// jobs.timelimits.responses.<invocationID> topic containing the remaining time
// for the job.
func (c *Client) SendTimeLimitResponse(invID string, timeRemaining int64) error {
	resp := &TimeLimitResponse{
		InvocationID:          invID,
		MillisecondsRemaining: timeRemaining,
	}
	msg, err := json.Marshal(resp)
	if err != nil {
		return err
	}
	return c.Publish(TimeLimitResponsesKey(invID), msg)
}

// SendTimeLimitDelta sends out a message to the
// jobs.timelimits.deltas.<invocationID> topic containing how the job should
// adjust its timelimit.
func (c *Client) SendTimeLimitDelta(invID, delta string) error {
	d := &TimeLimitDelta{
		InvocationID: invID,
		Delta:        delta,
	}
	msg, err := json.Marshal(d)
	if err != nil {
		return err
	}
	return c.Publish(TimeLimitDeltaRequestKey(invID), msg)
}

// SendStopRequest sends out a message to the jobs.stops.<invocation_id> topic
// telling listeners to stop their job.
func (c *Client) SendStopRequest(invID, user, reason string) error {
	s := NewStopRequest()
	s.Username = user
	s.Reason = reason
	s.InvocationID = invID
	msg, err := json.Marshal(s)
	if err != nil {
		return err
	}
	return c.Publish(StopRequestKey(invID), msg)
}
