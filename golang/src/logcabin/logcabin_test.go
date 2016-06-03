package logcabin

import (
	"encoding/json"
	"io/ioutil"
	"log"
	"os"
	"regexp"
	"testing"
)

func TestAllLoggerOutput(t *testing.T) {
	svc, artifact, msg := "test_service", "test_artifact", "msg"
	Init(svc, artifact)
	testLoggerOutput(svc, artifact, traceLevel, msg, Trace, t)
	testLoggerOutput(svc, artifact, infoLevel, msg, Info, t)
	testLoggerOutput(svc, artifact, warnLevel, msg, Warning, t)
	testLoggerOutput(svc, artifact, errorLevel, msg, Error, t)
}

func testLoggerOutput(expectedSvc string, expectedArtifact string, expectedLvl string, expectedMsg string, logger *log.Logger, t *testing.T) {
	original := os.Stdout
	restore := func() {
		os.Stdout = original
	}
	defer restore()

	r, w, err := os.Pipe()
	if err != nil {
		t.Error(err)
	}
	os.Stdout = w

	logger.Println(expectedMsg)

	w.Close()
	actualBytes, err := ioutil.ReadAll(r)
	if err != nil {
		t.Error(err)
	}
	var msg logMessage
	err = json.Unmarshal(actualBytes, &msg)
	if err != nil {
		t.Error(err)
	}
	actual := msg.Service
	expected := expectedSvc
	if expected != actual {
		t.Errorf("msg.Service was %s instead of %s", actual, expected)
	}

	actual = msg.Artifact
	expected = expectedArtifact
	if actual != expected {
		t.Errorf("msg.Artifact was %s instead of %s", actual, expected)
	}

	actual = msg.Group
	expected = "org.iplantc"
	if actual != expected {
		t.Errorf("msg.Group was %s instead of %s", actual, expected)
	}

	actual = msg.Level
	expected = expectedLvl
	if actual != expected {
		t.Errorf("msg.Level was %s instead of %s", actual, expected)
	}

	actual = msg.Message
	expected = expectedMsg
	if match, _ := regexp.MatchString(expectedMsg, actual); !match {
		t.Errorf("msg.Message was \"%s\", and did not contain %s", actual, expected)
	}
}

func BenchmarkTrace(b *testing.B) {
	svc, artifact, msg := "test_service", "test_artifact", "msg"
	Init(svc, artifact)
	Trace.SetOutput(ioutil.Discard)
	for i := 0; i < b.N; i++ {
		Trace.Println(msg)
	}
}

func BenchmarkInfo(b *testing.B) {
	svc, artifact, msg := "test_service", "test_artifact", "msg"
	Init(svc, artifact)
	Info.SetOutput(ioutil.Discard)
	for i := 0; i < b.N; i++ {
		Info.Println(msg)
	}
}

func BenchmarkWarn(b *testing.B) {
	svc, artifact, msg := "test_service", "test_artifact", "msg"
	Init(svc, artifact)
	Warning.SetOutput(ioutil.Discard)
	for i := 0; i < b.N; i++ {
		Warning.Println(msg)
	}
}

func BenchmarkErr(b *testing.B) {
	svc, artifact, msg := "test_service", "test_artifact", "msg"
	Init(svc, artifact)
	Error.SetOutput(ioutil.Discard)
	for i := 0; i < b.N; i++ {
		Error.Println(msg)
	}
}
