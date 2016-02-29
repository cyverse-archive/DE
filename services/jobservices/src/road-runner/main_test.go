package main

import (
	"fmt"
	"messaging"
	"testing"
	"time"
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
