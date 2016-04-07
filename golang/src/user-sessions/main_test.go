package main

import "testing"

func TestConvertBlankSession(t *testing.T) {
	record := &UserSessionRecord{
		ID:      "test_id",
		Session: "",
		UserID:  "test_user_id",
	}
	actual, err := convert(record, false)
	if err != nil {
		t.Error(err)
	}
	if len(actual) > 0 {
		t.Fail()
	}
}

func TestConvertUnparseableSession(t *testing.T) {
	record := &UserSessionRecord{
		ID:      "test_id",
		Session: "------------",
		UserID:  "test_user_id",
	}
	actual, err := convert(record, false)
	if err == nil {
		t.Fail()
	}
	if actual != nil {
		t.Fail()
	}
}

func TestConvertEmbeddedSession(t *testing.T) {
	record := &UserSessionRecord{
		ID:      "test_id",
		Session: `{"session":{"foo":"bar"}}`,
		UserID:  "test_user_id",
	}
	actual, err := convert(record, false)
	if err != nil {
		t.Fail()
	}
	if _, ok := actual["foo"]; !ok {
		t.Fail()
	}
	if actual["foo"].(string) != "bar" {
		t.Fail()
	}
}

func TestConvertNormalSession(t *testing.T) {
	record := &UserSessionRecord{
		ID:      "test_id",
		Session: `{"foo":"bar"}`,
		UserID:  "test_user_id",
	}
	actual, err := convert(record, false)
	if err != nil {
		t.Fail()
	}
	if _, ok := actual["foo"]; !ok {
		t.Fail()
	}
	if actual["foo"].(string) != "bar" {
		t.Fail()
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
