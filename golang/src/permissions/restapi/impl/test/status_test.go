package test

import (
	"encoding/json"
	"permissions/restapi/operations/status"
	"reflect"
	"testing"

	impl "permissions/restapi/impl/status"
)

var testJson = json.RawMessage([]byte(`{
    "info": {
        "description": "The Description",
        "title":       "The Title",
        "version":     "The Version"
    }
}`))

func TestGetStatus(t *testing.T) {
	f := impl.BuildStatusHandler(testJson)
	r := f()

	// Verify that we got the expected return value.
	if reflect.TypeOf(r) != reflect.TypeOf((*status.GetOK)(nil)) {
		t.Errorf("unexpected return type from status handler: %s", reflect.TypeOf(r))
	}

	// Convert the return value for convenience.
	s := r.(*status.GetOK)

	// Verify that we got the expected payload.
	if *s.Payload.Description != "The Description" {
		t.Errorf("unexpected description: %s", *s.Payload.Description)
	}
	if *s.Payload.Service != "The Title" {
		t.Errorf("unexpected service name: %s", *s.Payload.Service)
	}
	if *s.Payload.Version != "The Version" {
		t.Errorf("unexpected version: %s", *s.Payload.Version)
	}
}
