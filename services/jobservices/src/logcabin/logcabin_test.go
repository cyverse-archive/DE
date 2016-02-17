package logcabin

import (
	"encoding/json"
	"io/ioutil"
	"os"
	"testing"
)

func TestNewLogMessage(t *testing.T) {
	logger := New("jex-events", "jex-events")
	m := logger.NewLogMessage("foo")
	expected := "jex-events"
	if m.Service != expected {
		t.Errorf("LogMessage.Service was %s instead of %s", m.Service, expected)
	}
	if m.Artifact != expected {
		t.Errorf("LogMessage.Artifact was %s instead of %s", m.Artifact, expected)
	}
	expected = "org.iplantc"
	if m.Group != expected {
		t.Errorf("LogMessage.Group was %s instead of %s", m.Group, expected)
	}
	expected = "INFO"
	if m.Level != expected {
		t.Errorf("LogMessage.Level was %s instead of %s", m.Level, expected)
	}
	expected = "foo"
	if m.Message != expected {
		t.Errorf("LogMessage.Message was %s instead of %s", m.Message, expected)
	}
}

func TestConfigurableNew(t *testing.T) {
	l := New("test_service", "test_artifact")
	if l == nil {
		t.Error("logger was nil when it shouldn't have been")
		t.Fail()
	}
	expected := "test_service"
	if l.service != expected {
		t.Errorf("logger.service was %s instead of %s", l.service, expected)
	}
	expected = "test_artifact"
	if l.artifact != expected {
		t.Errorf("logger.artifact was %s instead of %s", l.artifact, expected)
	}
}

func TestLogWriter(t *testing.T) {
	original := os.Stdout
	r, w, err := os.Pipe()
	if err != nil {
		t.Error(err)
		t.Fail()
	}
	os.Stdout = w
	restore := func() {
		os.Stdout = original
	}
	defer restore()
	expected := "this is a test"
	logger := New("test_service", "test_artifact")
	_, err = logger.Write([]byte(expected))
	if err != nil {
		t.Error(err)
		os.Stdout = original
		t.Fail()
	}
	w.Close()
	var msg LogMessage
	actualBytes, err := ioutil.ReadAll(r)
	if err != nil {
		t.Error(err)
		t.Fail()
	}
	err = json.Unmarshal(actualBytes, &msg)
	if err != nil {
		t.Error(err)
		t.Fail()
	}
	actual := msg.Message
	if actual != expected {
		t.Errorf("logger returned %s instead of %s", actual, expected)
	}
	expected = "test_service"
	actual = msg.Service
	if actual != expected {
		t.Errorf("msg.Service was %s instead of %s", actual, expected)
	}
	expected = "test_artifact"
	actual = msg.Artifact
	if actual != expected {
		t.Errorf("msg.Artifact was %s instead of %s", actual, expected)
	}
	expected = "org.iplantc"
	actual = msg.Group
	if actual != expected {
		t.Errorf("msg.Group was %s instead of %s", actual, expected)
	}
}
