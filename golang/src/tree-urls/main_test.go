package main

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestBadRequest(t *testing.T) {
	var (
		expectedMsg    = "test message"
		expectedStatus = http.StatusBadRequest
	)

	recorder := httptest.NewRecorder()
	badRequest(recorder, "test message")
	actualMsg := recorder.Body.String()
	actualStatus := recorder.Code

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d and should have been %d", actualStatus, expectedStatus)
	}

	if actualMsg != expectedMsg {
		t.Errorf("Expected message was '%s', but should have been '%s'", actualMsg, expectedMsg)
	}
}

func TestErrored(t *testing.T) {
	var (
		expectedMsg    = "test message"
		expectedStatus = http.StatusInternalServerError
	)

	recorder := httptest.NewRecorder()
	errored(recorder, "test message")
	actualMsg := recorder.Body.String()
	actualStatus := recorder.Code

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d and should have been %d", actualStatus, expectedStatus)
	}

	if actualMsg != expectedMsg {
		t.Errorf("Expected message was '%s', but should have been '%s'", actualMsg, expectedMsg)
	}
}

func TestNotFound(t *testing.T) {
	var (
		expectedMsg    = "test message"
		expectedStatus = http.StatusNotFound
	)

	recorder := httptest.NewRecorder()
	notFound(recorder, "test message")
	actualMsg := recorder.Body.String()
	actualStatus := recorder.Code

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d and should have been %d", actualStatus, expectedStatus)
	}

	if actualMsg != expectedMsg {
		t.Errorf("Expected message was '%s', but should have been '%s'", actualMsg, expectedMsg)
	}
}

func TestValidSHA1(t *testing.T) {
	var (
		err      error
		valid    bool
		goodSHA1 = "60e3da2efd886074e28e44d48cc642f84c25b140"
		badSHA1  = "60e3da2efd886074e28e44d48cc64"
	)

	if valid, err = validSHA1(goodSHA1); err != nil {
		t.Error(err)
	}
	if !valid {
		t.Errorf("SHA1 '%s' was reported as invalid when it is valid", goodSHA1)
	}

	if valid, err = validSHA1(badSHA1); err != nil {
		t.Error(err)
	}
	if valid {
		t.Errorf("SHA1 '%s' was reported as valid when it is invalid", badSHA1)
	}
}

func TestFixAddrNoPrefix(t *testing.T) {
	expected := ":70000"
	actual := fixAddr("70000")
	if actual != expected {
		t.Fail()
	}
}

func TestFixAddrWithPrefix(t *testing.T) {
	expected := ":70000"
	actual := fixAddr(":70000")
	if actual != expected {
		t.Fail()
	}
}
