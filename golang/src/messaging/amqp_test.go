package messaging

import (
	"encoding/json"
	"fmt"
	"model"
	"os"
	"reflect"
	"testing"

	"github.com/streadway/amqp"
)

var client *Client

func GetClient(t *testing.T) *Client {
	var err error
	if client != nil {
		return client
	}
	client, err = NewClient(uri(), false)
	if err != nil {
		t.Error(err)
	}
	client.SetupPublishing(JobsExchange)
	go client.Listen()
	return client
}

func shouldrun() bool {
	if os.Getenv("RUN_INTEGRATION_TESTS") != "" {
		return true
	}
	return false
}

func uri() string {
	return "amqp://guest:guest@rabbit:5672/"
}

func TestConstants(t *testing.T) {
	expected := 0
	actual := int(Launch)
	if actual != expected {
		t.Errorf("Launch was %d instead of %d", actual, expected)
	}
	expected = 1
	actual = int(Stop)
	if actual != expected {
		t.Errorf("Stop was %d instead of %d", actual, expected)
	}
	expected = 0
	actual = int(Success)
	if actual != expected {
		t.Errorf("Success was %d instead of %d", actual, expected)
	}
}

func TestNewStopRequest(t *testing.T) {
	actual := NewStopRequest()
	expected := &StopRequest{Version: 0}
	if !reflect.DeepEqual(actual, expected) {
		t.Errorf("NewStopRequest returned:\n%#v\n\tinstead of:\n%#v", actual, expected)
	}
}

func TestNewLaunchRequest(t *testing.T) {
	job := &model.Job{}
	actual := NewLaunchRequest(job)
	expected := &JobRequest{
		Version: 0,
		Job:     job,
		Command: Launch,
	}
	if !reflect.DeepEqual(actual, expected) {
		t.Errorf("NewLaunchRequest returned:\n%#v\n\tinstead of:\n%#v", actual, expected)
	}
}

func TestNewClient(t *testing.T) {
	if !shouldrun() {
		return
	}
	actual, err := NewClient(uri(), false)
	if err != nil {
		t.Error(err)
	}
	defer actual.Close()
	expected := uri()
	if actual.uri != expected {
		t.Errorf("Client's uri was %s instead of %s", actual.uri, expected)
	}
}

func TestClient(t *testing.T) {
	if !shouldrun() {
		return
	}

	client := GetClient(t)

	//defer client.Close()
	key := "tests"
	actual := ""
	expected := "this is a test"
	coord := make(chan int)

	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = string(d.Body)
		coord <- 1
	}
	client.AddConsumer(JobsExchange, "topic", "test_queue", key, handler)
	client.Publish(key, []byte(expected))
	<-coord
	if actual != expected {
		t.Errorf("Handler received %s instead of %s", actual, expected)
	}

}

func TestSendTimeLimitRequest(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	var actual []byte
	coord := make(chan int)
	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = d.Body
		coord <- 1
	}
	key := TimeLimitRequestKey("test")
	client.AddConsumer(JobsExchange, "topic", "test_queue1", key, handler)
	client.SendTimeLimitRequest("test")
	<-coord
	req := &TimeLimitRequest{}
	err := json.Unmarshal(actual, req)
	if err != nil {
		t.Error(err)
	}
	if req.InvocationID != "test" {
		t.Errorf("TimeLimitRequest's InvocationID was %s instead of test", req.InvocationID)
	}
}

func TestSendTimeLimitResponse(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	var actual []byte
	coord := make(chan int)
	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = d.Body
		coord <- 1
	}
	key := TimeLimitResponsesKey("test")
	client.AddConsumer(JobsExchange, "topic", "test_queue2", key, handler)
	client.SendTimeLimitResponse("test", 0)
	<-coord
	resp := &TimeLimitResponse{}
	err := json.Unmarshal(actual, resp)
	if err != nil {
		t.Error(err)
	}
	if resp.InvocationID != "test" {
		t.Errorf("TimeLimitRequest's InvocationID was %s instead of test", resp.InvocationID)
	}
}

func TestSendTimeLimitDelta(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	var actual []byte
	coord := make(chan int)
	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = d.Body
		coord <- 1
	}
	key := TimeLimitDeltaRequestKey("test")
	client.AddConsumer(JobsExchange, "topic", "test_queue3", key, handler)
	client.SendTimeLimitDelta("test", "10s")
	<-coord
	delta := &TimeLimitDelta{}
	err := json.Unmarshal(actual, delta)
	if err != nil {
		t.Error(err)
	}
	if delta.InvocationID != "test" {
		t.Errorf("TimeLimitDelta's InvocationID was %s instead of test", delta.InvocationID)
	}
	if delta.Delta != "10s" {
		t.Errorf("TimeLimitDelta's Delta was %s instead of 10s", delta.Delta)
	}
}

func TestSendStopRequest(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	var actual []byte
	var err error
	coord := make(chan int)
	invID := "test"
	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = d.Body
		coord <- 1
	}
	key := StopRequestKey(invID)
	client.AddConsumer(JobsExchange, "topic", "test_queue4", key, handler)
	client.SendStopRequest(invID, "test_user", "this is a test")
	<-coord
	req := &StopRequest{}
	if err = json.Unmarshal(actual, req); err != nil {
		t.Error(err)
	}
	if req.Reason != "this is a test" {
		t.Errorf("Reason was '%s' instead of '%s'", req.Reason, "this is a test")
	}
	if req.InvocationID != invID {
		t.Errorf("InvocationID was %s instead of %s", req.InvocationID, invID)
	}
	if req.Username != "test_user" {
		t.Errorf("Username was %s instead of %s", req.Username, "test_user")
	}
}

func TestCreateQueue(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	actual, err := client.CreateQueue("test_queue5", JobsExchange, "test_key5", true, false)
	if err != nil {
		t.Error(err)
	}
	if actual == nil {
		t.Error("channel is nil")
	}
	if _, err = actual.QueueInspect("test_queue5"); err != nil {
		t.Error(err)
	}
	if err = actual.Close(); err != nil {
		t.Error(err)
	}
}

func TestQueueExists(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	actual, err := client.CreateQueue("test_queue5", JobsExchange, "test_key5", true, false)
	if err != nil {
		t.Error(err)
	}
	if actual == nil {
		t.Error("channel is nil")
	}
	exists, err := client.QueueExists("test_queue5")
	if err != nil {
		t.Error(err)
	}
	if !exists {
		t.Error("Queue 'test_queue5' was not found")
	}
	if err = actual.Close(); err != nil {
		t.Error(err)
	}
}

func TestDeleteQueue(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	actual, err := client.CreateQueue("test_queue6", JobsExchange, "test_key5", true, false)
	if err != nil {
		t.Error(err)
	}
	if actual == nil {
		t.Error("channel is nil")
	}
	exists, err := client.QueueExists("test_queue6")
	if err != nil {
		t.Error(err)
	}
	if !exists {
		t.Error("Queue 'test_queue6' was not found")
	}

	actual, err = client.CreateQueue("test_queue7", JobsExchange, "test_key6", true, false)
	if err != nil {
		t.Error(err)
	}
	if actual == nil {
		t.Error("channel is nil")
	}
	exists, err = client.QueueExists("test_queue7")
	if err != nil {
		t.Error(err)
	}
	if !exists {
		t.Error("Queue 'test_queue7' was not found")
	}

	actual, err = client.CreateQueue("test_queue8", JobsExchange, "test_key7", true, false)
	if err != nil {
		t.Error(err)
	}
	if actual == nil {
		t.Error("channel is nil")
	}
	exists, err = client.QueueExists("test_queue8")
	if err != nil {
		t.Error(err)
	}
	if !exists {
		t.Error("Queue 'test_queue8' was not found")
	}

	if err = client.DeleteQueue("test_queue6"); err != nil {
		t.Error(err)
	}
	exists, err = client.QueueExists("test_queue6")
	if exists {
		t.Error("Queue 'test_queue6' was found")
	}

	if err = client.DeleteQueue("test_queue7"); err != nil {
		t.Error(err)
	}
	exists, err = client.QueueExists("test_queue7")
	if exists {
		t.Error("Queue 'test_queue7' was found")
	}

	if err = client.DeleteQueue("test_queue8"); err != nil {
		t.Error(err)
	}
	exists, err = client.QueueExists("test_queue8")
	if exists {
		t.Error("Queue 'test_queue8' was found")
	}

	if err = actual.Close(); err != nil {
		t.Error(err)
	}
}

func TestTimeLimitRequestKey(t *testing.T) {
	invID := "test"
	actual := TimeLimitRequestKey(invID)
	expected := fmt.Sprintf("%s.%s", TimeLimitRequestsKey, invID)
	if actual != expected {
		t.Errorf("TimeLimitRequestKey returned %s instead of %s", actual, expected)
	}
}

func TestTimeLimitRequestQueueName(t *testing.T) {
	invID := "test"
	actual := TimeLimitRequestQueueName(invID)
	expected := fmt.Sprintf("road-runner-%s-tl-request", invID)
	if actual != expected {
		t.Errorf("TimeLimitRequestQueueName returned %s instead of %s", actual, expected)
	}
}

func TestTimeLimitResponsesKey(t *testing.T) {
	invID := "test"
	actual := TimeLimitResponsesKey(invID)
	expected := fmt.Sprintf("%s.%s", TimeLimitResponseKey, invID)
	if actual != expected {
		t.Errorf("TimeLimitResponsesKey returned %s instead of %s", actual, expected)
	}
}

func TestTimeLimitResponsesQueueName(t *testing.T) {
	invID := "test"
	actual := TimeLimitResponsesQueueName(invID)
	expected := fmt.Sprintf("road-runner-%s-tl-response", invID)
	if actual != expected {
		t.Errorf("TimeLimitResponsesQueueName returned %s instead of %s", actual, expected)
	}
}

func TestTimeLimitDeltaRequestKey(t *testing.T) {
	invID := "test"
	actual := TimeLimitDeltaRequestKey(invID)
	expected := fmt.Sprintf("%s.%s", TimeLimitDeltaKey, invID)
	if actual != expected {
		t.Errorf("TimeLimitDeltaRequestKey returned %s instead of %s", actual, expected)
	}
}

func TestStopRequestKey(t *testing.T) {
	invID := "test"
	actual := StopRequestKey(invID)
	expected := fmt.Sprintf("%s.%s", StopsKey, invID)
	if actual != expected {
		t.Errorf("StopRequestKey returned %s instead of %s", actual, expected)
	}
}

func TestTimeLimitDeltaQueueName(t *testing.T) {
	invID := "test"
	actual := TimeLimitDeltaQueueName(invID)
	expected := fmt.Sprintf("road-runner-%s-tl-delta", invID)
	if actual != expected {
		t.Errorf("TimeLimitDeltaQueueName returned %s instead of %s", actual, expected)
	}
}

func TestStopQueueName(t *testing.T) {
	invID := "test"
	actual := StopQueueName(invID)
	expected := fmt.Sprintf("road-runner-%s-stops-request", invID)
	if actual != expected {
		t.Errorf("StopQueueName returneed %s instead of %s", actual, expected)
	}
}
