package main

import (
	"net/http"
	"net/http/httptest"
	"testing"
)

type MockDB struct {
	storage map[string]map[string]interface{}
}

func NewMockDB() *MockDB {
	return &MockDB{
		storage: make(map[string]map[string]interface{}),
	}
}

func (m *MockDB) hasSHA1(sha1 string) (bool, error) {
	var ok bool
	_, ok = m.storage[sha1]
	return ok, nil

}

func (m *MockDB) getSavedSearches(username string) ([]string, error) {
	return []string{m.storage[username]["saved_searches"].(string)}, nil
}

func (m *MockDB) deleteSavedSearches(username string) error {
	delete(m.storage, username)
	return nil
}

func (m *MockDB) insertSavedSearches(username, savedSearches string) error {
	if _, ok := m.storage[username]["tree_urls"]; !ok {
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
