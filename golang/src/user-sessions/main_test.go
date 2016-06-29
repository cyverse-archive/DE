package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/http/httptest"
	"reflect"
	"testing"
)

type MockDB struct {
	storage map[string]map[string]interface{}
	users   map[string]bool
}

func NewMockDB() *MockDB {
	return &MockDB{
		storage: make(map[string]map[string]interface{}),
		users:   make(map[string]bool),
	}
}

func (m *MockDB) isUser(username string) (bool, error) {
	_, ok := m.users[username]
	return ok, nil
}

func (m *MockDB) hasSessions(username string) (bool, error) {
	stored, ok := m.storage[username]
	if !ok {
		return false, nil
	}
	if stored == nil {
		return false, nil
	}
	prefs, ok := m.storage[username]["user-sessions"].(string)
	if !ok {
		return false, nil
	}
	if prefs == "" {
		return false, nil
	}
	return true, nil
}

func (m *MockDB) getSessions(username string) ([]UserSessionRecord, error) {
	return []UserSessionRecord{
		UserSessionRecord{
			ID:      "id",
			Session: m.storage[username]["user-sessions"].(string),
			UserID:  "user-id",
		},
	}, nil
}

func (m *MockDB) insertSession(username, session string) error {
	if _, ok := m.storage[username]["user-sessions"]; !ok {
		m.storage[username] = make(map[string]interface{})
	}
	m.storage[username]["user-sessions"] = session
	return nil
}

func (m *MockDB) updateSession(username, prefs string) error {
	return m.insertSession(username, prefs)
}

func (m *MockDB) deleteSession(username string) error {
	delete(m.storage, username)
	return nil
}

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

func TestBadRequest(t *testing.T) {
	var (
		expectedMsg    = "test message\n"
		expectedStatus = http.StatusBadRequest
	)

	recorder := httptest.NewRecorder()
	badRequest(recorder, "test message")
	actualMsg := recorder.Body.String()
	actualStatus := recorder.Code

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d but should have been %d", actualStatus, expectedStatus)
	}

	if actualMsg != expectedMsg {
		t.Errorf("Message was '%s' but should have been '%s'", actualMsg, expectedMsg)
	}
}

func TestErrored(t *testing.T) {
	var (
		expectedMsg    = "test message\n"
		expectedStatus = http.StatusInternalServerError
	)

	recorder := httptest.NewRecorder()
	errored(recorder, "test message")
	actualMsg := recorder.Body.String()
	actualStatus := recorder.Code

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d but should have been %d", actualStatus, expectedStatus)
	}

	if actualMsg != expectedMsg {
		t.Errorf("Message was '%s' but should have been '%s'", actualMsg, expectedMsg)
	}
}

func TestGetUserSessionForRequest(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)

	expected := []byte("{\"one\":\"two\"}")
	expectedWrapped := []byte("{\"session\":{\"one\":\"two\"}}")
	mock.users["test-user"] = true
	if err := mock.insertSession("test-user", string(expected)); err != nil {
		t.Error(err)
	}

	actualWrapped, err := n.getUserSessionForRequest("test-user", true)
	if err != nil {
		t.Error(err)
	}

	if !bytes.Equal(actualWrapped, expectedWrapped) {
		t.Errorf("The return value was '%s' instead of '%s'", actualWrapped, expectedWrapped)
	}

	actual, err := n.getUserSessionForRequest("test-user", false)
	if err != nil {
		t.Error(err)
	}

	if !bytes.Equal(actual, expected) {
		t.Errorf("The return value was '%s' instead of '%s'", actual, expected)
	}
}

func TestGetRequest(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)

	expected := []byte("{\"one\":\"two\"}")
	mock.users["test-user"] = true
	if err := mock.insertSession("test-user", string(expected)); err != nil {
		t.Error(err)
	}

	server := httptest.NewServer(n.router)
	defer server.Close()

	url := fmt.Sprintf("%s/%s", server.URL, "test-user")
	res, err := http.Get(url)
	if err != nil {
		t.Error(err)
	}

	actualBody, err := ioutil.ReadAll(res.Body)
	if err != nil {
		t.Error(err)
	}
	res.Body.Close()

	if !bytes.Equal(actualBody, expected) {
		t.Errorf("Message was '%s' but should have been '%s'", actualBody, expected)
	}

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualStatus != expectedStatus {
		t.Errorf("Status code was %d but should have been %d", actualStatus, expectedStatus)
	}
}

func TestPutRequest(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)

	username := "test-user"
	expected := []byte(`{"one":"two"}`)

	mock.users[username] = true

	server := httptest.NewServer(n.router)
	defer server.Close()

	url := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodPut, url, bytes.NewReader(expected))
	if err != nil {
		t.Error(err)
	}

	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		t.Error(err)
	}
	res.Body.Close()

	var parsed map[string]map[string]string
	if err = json.Unmarshal(body, &parsed); err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	if err = json.Unmarshal(expected, &expectedParsed); err != nil {
		t.Error(err)
	}

	if _, ok := parsed["session"]; !ok {
		t.Error("JSON did not contain a 'preferences' key")
	}

	if !reflect.DeepEqual(parsed["session"], expectedParsed) {
		t.Errorf("Put returned %#v instead of %#v", parsed["session"], expectedParsed)
	}
}

func TestPostRequest(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)

	username := "test-user"
	expected := []byte(`{"one":"two"}`)

	mock.users[username] = true
	if err := mock.insertSession(username, string(expected)); err != nil {
		t.Error(err)
	}

	server := httptest.NewServer(n.router)
	defer server.Close()

	url := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewReader(expected))
	if err != nil {
		t.Error(err)
	}

	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		t.Error(err)
	}
	res.Body.Close()

	var parsed map[string]map[string]string
	if err = json.Unmarshal(body, &parsed); err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	if err = json.Unmarshal(expected, &expectedParsed); err != nil {
		t.Error(err)
	}

	if _, ok := parsed["session"]; !ok {
		t.Error("JSON did not contain a 'preferences' key")
	}

	if !reflect.DeepEqual(parsed["session"], expectedParsed) {
		t.Errorf("POST requeted %#v instead of %#v", parsed["session"], expectedParsed)
	}
}

func TestDelete(t *testing.T) {
	username := "test-user"
	expected := []byte(`{"one":"two"}`)

	mock := NewMockDB()
	mock.users[username] = true
	n := New(mock)

	if err := mock.insertSession(username, string(expected)); err != nil {
		t.Error(err)
	}

	server := httptest.NewServer(n.router)
	defer server.Close()

	url := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodDelete, url, nil)
	if err != nil {
		t.Error(err)
	}

	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		t.Error(err)
	}
	res.Body.Close()

	if len(body) > 0 {
		t.Errorf("DELETE returned a body: %s", body)
	}

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualStatus != expectedStatus {
		t.Errorf("DELETE status code was %d instead of %d", actualStatus, expectedStatus)
	}
}

func TestDeleteUnstored(t *testing.T) {
	username := "test-user"
	mock := NewMockDB()
	mock.users[username] = true
	n := New(mock)

	server := httptest.NewServer(n.router)
	defer server.Close()

	url := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodDelete, url, nil)
	if err != nil {
		t.Error(err)
	}

	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		t.Error(err)
	}
	res.Body.Close()

	if len(body) > 0 {
		t.Errorf("DELETE returned a body: %s", body)
	}

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualStatus != expectedStatus {
		t.Errorf("DELETE status code was %d instead of %d", actualStatus, expectedStatus)
	}
}
