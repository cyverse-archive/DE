package main

import (
	"configurate"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"messaging"
	"model"
	"os"
	"path"
	"testing"
	"time"

	"github.com/streadway/amqp"
)

var (
	s *model.Job
)

func shouldrun() bool {
	if os.Getenv("RUN_INTEGRATION_TESTS") != "" {
		return true
	}
	return false
}

func uri() string {
	return "http://dind:2375"
}

func JSONData() ([]byte, error) {
	f, err := os.Open("../test/test_runner.json")
	if err != nil {
		return nil, err
	}
	c, err := ioutil.ReadAll(f)
	if err != nil {
		return nil, err
	}
	return c, err
}

func _inittests(t *testing.T, memoize bool) *model.Job {
	if s == nil || !memoize {
		configurate.Init("../test/test_config.json")
		configurate.C.Set("irods.base", "/path/to/irodsbase")
		configurate.C.Set("irods.host", "hostname")
		configurate.C.Set("irods.port", "1247")
		configurate.C.Set("irods.user", "user")
		configurate.C.Set("irods.pass", "pass")
		configurate.C.Set("irods.zone", "test")
		configurate.C.Set("irods.resc", "")
		configurate.C.Set("condor.log_path", "/path/to/logs")
		configurate.C.Set("condor.porklock_tag", "test")
		configurate.C.Set("condor.filter_files", "foo,bar,baz,blippy")
		configurate.C.Set("condor.request_disk", "0")
		data, err := JSONData()
		if err != nil {
			t.Error(err)
		}
		s, err = model.NewFromData(data)
		if err != nil {
			t.Error(err)
		}
		err = configurate.Init("../test/test_config.yaml")
		if err != nil {
			t.Fatal(err)
		}
	}
	return s
}

func inittests(t *testing.T) *model.Job {
	return _inittests(t, true)
}

func GetClient(t *testing.T) *messaging.Client {
	var err error
	if client != nil {
		return client
	}
	client, err = messaging.NewClient(messagingURI(), false)
	if err != nil {
		t.Error(err)
	}
	client.SetupPublishing(messaging.JobsExchange)
	go client.Listen()
	return client
}

func messagingURI() string {
	return "amqp://guest:guest@rabbit:5672/"
}

func TestRegisterTimeLimitDeltaListener(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	defaultDuration, err := time.ParseDuration("48h")
	if err != nil {
		t.Error(err)
	}
	exitFunc := func() {
		fmt.Println("exitFunc called")
	}
	timeTracker := NewTimeTracker(defaultDuration, exitFunc)
	unwanted := timeTracker.EndDate
	invID := "test_inv"
	RegisterTimeLimitDeltaListener(client, timeTracker, invID)
	client.SendTimeLimitDelta(invID, "9h")
	time.Sleep(1000 * time.Millisecond)
	if timeTracker.EndDate == unwanted {
		t.Errorf("EndDate was still set to the default after sending a delta")
	}
}

func TestRegisterTimeLimitRequestListener(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	defaultDuration, err := time.ParseDuration("48h")
	if err != nil {
		t.Error(err)
	}
	exitFunc := func() {
		fmt.Println("exitFunc called")
	}
	timeTracker := NewTimeTracker(defaultDuration, exitFunc)
	invID := "test"
	var actual []byte
	coord := make(chan int)
	handler := func(d amqp.Delivery) {
		d.Ack(false)
		actual = d.Body
		coord <- 1
	}

	key := messaging.TimeLimitResponsesKey(invID)

	// This will listen for the messages sent out as a response by RegisterTimeLimitRequestListener
	client.AddConsumer(messaging.JobsExchange, "topic", "yay", key, handler)

	// Listen for time limit requests
	RegisterTimeLimitRequestListener(client, timeTracker, invID)

	// Send a time limit request
	err = client.SendTimeLimitRequest(invID)
	if err != nil {
		t.Error(err)
	}
	time.Sleep(1000 * time.Millisecond)
	<-coord
	parsedResponse := &messaging.TimeLimitResponse{}
	if err = json.Unmarshal(actual, parsedResponse); err != nil {
		t.Error(err)
	}
	if parsedResponse.InvocationID != invID {
		t.Errorf("InvocationID was %s instead of %s", parsedResponse.InvocationID, invID)
	}
	if parsedResponse.MillisecondsRemaining <= 0 {
		t.Errorf("MillisecondsRemaining was %d instead of >0", parsedResponse.MillisecondsRemaining)
	}
}

func TestRegisterStopRequestListener(t *testing.T) {
	if !shouldrun() {
		return
	}
	client := GetClient(t)
	invID := "test"
	exit := make(chan messaging.StatusCode)
	RegisterStopRequestListener(client, exit, invID)
	err := client.SendStopRequest(invID, "test", "this is a test")
	if err != nil {
		t.Error(err)
	}
	actual := <-exit
	if actual != messaging.StatusKilled {
		t.Errorf("StatusCode was %d instead of %d", int64(actual), int64(messaging.StatusKilled))
	}
}

func TestNewTimeTracker(t *testing.T) {
	actual := 0
	expected := 1
	duration, err := time.ParseDuration("1s")
	if err != nil {
		t.Error(err)
	}
	coord := make(chan int)
	handler := func() {
		actual = 1
		coord <- 1
	}
	tt := NewTimeTracker(duration, handler)
	if tt == nil {
		t.Error("NewTimeTracker returned nil")
	}
	<-coord
	if actual != expected {
		t.Errorf("actual was %d instead of %d", actual, expected)
	}
}

func TestApplyDelta(t *testing.T) {
	defaultDuration, err := time.ParseDuration("10s")
	if err != nil {
		t.Error(err)
	}
	resetDuration, err := time.ParseDuration("20s")
	if err != nil {
		t.Error(err)
	}
	handler := func() {}
	tt := NewTimeTracker(defaultDuration, handler)
	firstDate := tt.EndDate
	if err = tt.ApplyDelta(resetDuration); err != nil {
		t.Error(err)
	}
	secondDate := tt.EndDate
	if !secondDate.After(firstDate) {
		t.Errorf("The date after ApplyDelta() was %s, which isn't later than %s", secondDate.String(), firstDate.String())
	}
}

func TestCopyJobFile(t *testing.T) {
	uuid := "00000000-0000-0000-0000-000000000000"
	from := path.Join("../test", fmt.Sprintf("%s.json", uuid))
	to := "/tmp"
	err := copyJobFile(uuid, from, to)
	if err != nil {
		t.Error(err)
	}
	tmpPath := path.Join(to, fmt.Sprintf("%s.json", uuid))
	if _, err := os.Open(tmpPath); err != nil {
		t.Error(err)
	} else {
		if err = os.Remove(tmpPath); err != nil {
			t.Error(err)
		}
	}
}

func TestDeleteJobFile(t *testing.T) {
	uuid := "00000000-0000-0000-0000-000000000000"
	from := path.Join("../test", fmt.Sprintf("%s.json", uuid))
	to := "/tmp"
	err := copyJobFile(uuid, from, to)
	if err != nil {
		t.Error(err)
	}
	deleteJobFile(uuid, to)
	tmpPath := path.Join(to, fmt.Sprintf("%s.json", uuid))
	if _, err := os.Open(tmpPath); err == nil {
		t.Errorf("tmpPath %s existed after deleteJobFile() was called", tmpPath)
	}
}
