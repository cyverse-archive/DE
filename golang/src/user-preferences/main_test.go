package main

import "testing"

func TestConvertBlankPreferences(t *testing.T) {
	record := &UserPreferencesRecord{
		ID:          "test_id",
		Preferences: "",
		UserID:      "test_user_id",
	}
	actual, err := convert(record, false)
	if err != nil {
		t.Error(err)
	}
	if len(actual) > 0 {
		t.Fail()
	}
}

func TestConvertUnparseablePreferences(t *testing.T) {
	record := &UserPreferencesRecord{
		ID:          "test_id",
		Preferences: "------------",
		UserID:      "test_user_id",
	}
	actual, err := convert(record, false)
	if err == nil {
		t.Fail()
	}
	if actual != nil {
		t.Fail()
	}
}

func TestConvertEmbeddedPreferences(t *testing.T) {
	record := &UserPreferencesRecord{
		ID:          "test_id",
		Preferences: `{"preferences":{"foo":"bar"}}`,
		UserID:      "test_user_id",
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

func TestConvertNormalPreferences(t *testing.T) {
	record := &UserPreferencesRecord{
		ID:          "test_id",
		Preferences: `{"foo":"bar"}`,
		UserID:      "test_user_id",
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
