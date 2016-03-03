package main

import (
	"encoding/json"
	"fmt"
	"messaging"
	"testing"
	"time"

	"github.com/streadway/amqp"
)

var client *messaging.Client

func GetClient(t *testing.T) *messaging.Client {
	var err error
	if client != nil {
		return client
	}
	client, err = messaging.NewClient(messagingURI(), false)
	if err != nil {
		t.Error(err)
		t.Fail()
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
		t.Fail()
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
		t.Fail()
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
	key := fmt.Sprintf("%s.%s", messaging.TimeLimitResponseKey, invID)

	// This will listen for the messages sent out as a response by RegisterTimeLimitRequestListener
	client.AddConsumer(messaging.JobsExchange, "yay", key, handler)

	// Listen for time limit requests
	RegisterTimeLimitRequestListener(client, timeTracker, invID)

	// Send a time limit request
	err = client.SendTimeLimitRequest(invID)
	if err != nil {
		t.Error(err)
		t.Fail()
	}
	time.Sleep(1000 * time.Millisecond)
	<-coord
	parsedResponse := &messaging.TimeLimitResponse{}
	if err = json.Unmarshal(actual, parsedResponse); err != nil {
		t.Error(err)
		t.Fail()
	}
	if parsedResponse.InvocationID != invID {
		t.Errorf("InvocationID was %s instead of %s", parsedResponse.InvocationID, invID)
	}
	if parsedResponse.MillisecondsRemaining <= 0 {
		t.Errorf("MillisecondsRemaining was %d instead of >0", parsedResponse.MillisecondsRemaining)
	}
}

func TestNewTimeTracker(t *testing.T) {
	actual := 0
	expected := 1
	duration, err := time.ParseDuration("1s")
	if err != nil {
		t.Error(err)
		t.Fail()
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
		t.Fail()
	}
	resetDuration, err := time.ParseDuration("20s")
	if err != nil {
		t.Error(err)
		t.Fail()
	}
	handler := func() {}
	tt := NewTimeTracker(defaultDuration, handler)
	firstDate := tt.EndDate
	if err = tt.ApplyDelta(resetDuration); err != nil {
		t.Error(err)
		t.Fail()
	}
	secondDate := tt.EndDate
	if !secondDate.After(firstDate) {
		t.Errorf("The date after ApplyDelta() was %s, which isn't later than %s", secondDate.String(), firstDate.String())
	}
}
