package main

import (
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/http/httptest"
	"reflect"
	"strings"
	"testing"
)

type MockDB struct {
	storage map[string]map[string]interface{}
	users   map[string]bool
}

func NewMockDB() *MockDB {
	return &MockDB{
		users:   make(map[string]bool),
		storage: make(map[string]map[string]interface{}),
	}
}

func (m *MockDB) isUser(username string) (bool, error) {
	_, ok := m.users[username]
	return ok, nil
}

func (m *MockDB) hasSavedSearches(username string) (bool, error) {
	stored, ok := m.storage[username]
	if !ok {
		return false, nil
	}
	if stored == nil {
		return false, nil
	}
	searches, ok := m.storage[username]["saved_searches"].(string)
	if !ok {
		return false, nil
	}
	return len(searches) > 0, nil

}

func (m *MockDB) getSavedSearches(username string) ([]string, error) {
	return []string{m.storage[username]["saved_searches"].(string)}, nil
}

func (m *MockDB) deleteSavedSearches(username string) error {
	delete(m.storage, username)
	return nil
}

func (m *MockDB) insertSavedSearches(username, savedSearches string) error {
	if _, ok := m.storage[username]["saved_searches"]; !ok {
		m.storage[username] = make(map[string]interface{})
	}
	m.storage[username]["saved_searches"] = savedSearches
	return nil
}

func (m *MockDB) updateSavedSearches(username, savedSearches string) error {
	return m.insertSavedSearches(username, savedSearches)
}

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

//////////////
func TestNew(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)
	if n == nil {
		t.Error("New returned nil instead of a *TreeURLs")
	}
}

func TestGreeting(t *testing.T) {
	mock := NewMockDB()
	n := New(mock)

	server := httptest.NewServer(n.router)
	defer server.Close()

	res, err := http.Get(server.URL)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	actualBody := string(bodyBytes)
	expectedBody := "Hello from saved-searches."

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualBody != expectedBody {
		t.Errorf("Body of the response was '%s' instead of '%s'", actualBody, expectedBody)
	}

	if actualStatus != expectedStatus {
		t.Errorf("Status of the response was %d instead of %d", actualStatus, expectedStatus)
	}
}

func TestGet(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true
	if err := mock.insertSavedSearches(username, expectedBody); err != nil {
		t.Error(err)
	}

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	res, err := http.Get(userURL)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	actualBody := string(bodyBytes)

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualBody != expectedBody {
		t.Errorf("Body of the response was '%s' instead of '%s'", actualBody, expectedBody)
	}

	if actualStatus != expectedStatus {
		t.Errorf("Status of the response was %d instead of %d", actualStatus, expectedStatus)
	}
}

func TestPutInsert(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodPut, userURL, strings.NewReader(expectedBody))
	if err != nil {
		t.Error(err)
	}
	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	var parsed map[string]map[string]string
	err = json.Unmarshal(bodyBytes, &parsed)
	if err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	err = json.Unmarshal([]byte(expectedBody), &expectedParsed)
	if err != nil {
		t.Error(err)
	}

	if _, ok := parsed["saved_searches"]; !ok {
		t.Error("Parsed response did not have a top-level 'saved_searches' key")
	}

	if !reflect.DeepEqual(parsed["saved_searches"], expectedParsed) {
		t.Errorf("Put returned '%#v' as the saved search instead of '%#v'", parsed["saved_searches"], expectedBody)
	}
}

func TestPutUpdate(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true
	if err := mock.insertSavedSearches(username, expectedBody); err != nil {
		t.Error(err)
	}

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodPut, userURL, strings.NewReader(expectedBody))
	if err != nil {
		t.Error(err)
	}
	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	var parsed map[string]map[string]string
	err = json.Unmarshal(bodyBytes, &parsed)
	if err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	err = json.Unmarshal([]byte(expectedBody), &expectedParsed)
	if err != nil {
		t.Error(err)
	}

	if _, ok := parsed["saved_searches"]; !ok {
		t.Error("Parsed response did not have a top-level 'saved_searches' key")
	}

	if !reflect.DeepEqual(parsed["saved_searches"], expectedParsed) {
		t.Errorf("Put returned '%#v' as the saved search instead of '%#v'", parsed["saved_searches"], expectedBody)
	}
}

func TestPostInsert(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true
	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	res, err := http.Post(userURL, "", strings.NewReader(expectedBody))
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	var parsed map[string]map[string]string
	err = json.Unmarshal(bodyBytes, &parsed)
	if err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	err = json.Unmarshal([]byte(expectedBody), &expectedParsed)
	if err != nil {
		t.Error(err)
	}

	if _, ok := parsed["saved_searches"]; !ok {
		t.Error("Parsed response did not have a top-level 'saved_searches' key")
	}

	if !reflect.DeepEqual(parsed["saved_searches"], expectedParsed) {
		t.Errorf("Post returned '%#v' as the saved search instead of '%#v'", parsed["saved_searches"], expectedBody)
	}
}

func TestPostUpdate(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true
	if err := mock.insertSavedSearches(username, expectedBody); err != nil {
		t.Error(err)
	}

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	res, err := http.Post(userURL, "", strings.NewReader(expectedBody))
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	var parsed map[string]map[string]string
	err = json.Unmarshal(bodyBytes, &parsed)
	if err != nil {
		t.Error(err)
	}

	var expectedParsed map[string]string
	err = json.Unmarshal([]byte(expectedBody), &expectedParsed)
	if err != nil {
		t.Error(err)
	}

	if _, ok := parsed["saved_searches"]; !ok {
		t.Error("Parsed response did not have a top-level 'saved_searches' key")
	}

	if !reflect.DeepEqual(parsed["saved_searches"], expectedParsed) {
		t.Errorf("Post returned '%#v' as the saved search instead of '%#v'", parsed["saved_searches"], expectedBody)
	}
}

func TestDelete(t *testing.T) {
	username := "test_user@test-domain.org"
	expectedBody := `{"search":"fake"}`

	mock := NewMockDB()
	mock.users[username] = true
	if err := mock.insertSavedSearches(username, expectedBody); err != nil {
		t.Error(err)
	}

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodDelete, userURL, nil)
	if err != nil {
		t.Error(err)
	}
	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	if len(bodyBytes) > 0 {
		t.Errorf("Delete returned a body when it should not have: %s", string(bodyBytes))
	}

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualStatus != expectedStatus {
		t.Errorf("StatusCode was %d instead of %d", actualStatus, expectedStatus)
	}
}

func TestDeleteUnstored(t *testing.T) {
	username := "test_user@test-domain.org"

	mock := NewMockDB()
	mock.users[username] = true

	n := New(mock)
	server := httptest.NewServer(n.router)
	defer server.Close()

	userURL := fmt.Sprintf("%s/%s", server.URL, username)
	httpClient := &http.Client{}
	req, err := http.NewRequest(http.MethodDelete, userURL, nil)
	if err != nil {
		t.Error(err)
	}
	res, err := httpClient.Do(req)
	if err != nil {
		t.Error(err)
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	res.Body.Close()
	if err != nil {
		t.Error(err)
	}

	if len(bodyBytes) > 0 {
		t.Errorf("Delete returned a body when it should not have: %s", string(bodyBytes))
	}

	expectedStatus := http.StatusOK
	actualStatus := res.StatusCode

	if actualStatus != expectedStatus {
		t.Errorf("StatusCode was %d instead of %d", actualStatus, expectedStatus)
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
