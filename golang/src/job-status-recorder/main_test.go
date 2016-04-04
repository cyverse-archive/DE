package main

import (
	"database/sql"
	"encoding/json"
	"messaging"
	"model"
	"net"
	"os"
	"strconv"
	"testing"
	"time"

	"github.com/streadway/amqp"
)

func shouldrun() bool {
	if os.Getenv("RUN_INTEGRATION_TESTS") != "" {
		return true
	}
	return false
}

func rabbituri() string {
	return "amqp://guest:guest@rabbit:5672/"
}

func dburi() string {
	return "postgres://de:notprod@dedb:5432/de?sslmode=disable"
}

func initdb(t *testing.T) *sql.DB {
	db, err := sql.Open("postgres", dburi())
	if err != nil {
		t.Error(err)
	}
	err = db.Ping()
	if err != nil {
		t.Error(err)
	}
	return db
}

func TestInsert(t *testing.T) {
	if !shouldrun() {
		return
	}
	db = initdb(t)
	defer db.Close()
	n := time.Now().UnixNano() / int64(time.Millisecond)
	actual, err := insert("RUNNING", "test-invocation-id", "test", "localhost", "127.0.0.1", n)
	if err != nil {
		t.Error(err)
	}
	rowCount, err := actual.RowsAffected()
	if err != nil {
		t.Error(err)
	}
	rows, err := db.Query("select status, message, sent_from, sent_from_hostname, sent_on from job_status_updates where external_id = 'test-invocation-id'")
	if err != nil {
		t.Error(err)
	}
	defer rows.Close()
	var (
		status, message, sentFromHostname string
		sentOn                            int64
		sentFrom                          string
	)
	for rows.Next() {
		err = rows.Scan(&status, &message, &sentFrom, &sentFromHostname, &sentOn)
		if err != nil {
			t.Error(err)
		}
		if status != "RUNNING" {
			t.Errorf("status was %s instead of RUNNING", status)
		}
		if message != "test" {
			t.Errorf("message was %s instead of 'test'", message)
		}
		if sentFrom != "127.0.0.1" {
			t.Errorf("sentFrom was %s instead of '127.0.0.1'", sentFrom)
		}
		if sentFromHostname != "localhost" {
			t.Errorf("sentFromHostname was %s instead of 'localhost'", sentFromHostname)
		}
		if n != sentOn {
			t.Errorf("sentOn was %d instead of %d", sentOn, n)
		}
	}
	err = rows.Err()
	if err != nil {
		t.Error(err)
	}
	if rowCount != 1 {
		t.Errorf("RowsAffected() should have returned 1: %d", rowCount)
	}
	_, err = db.Exec("DELETE FROM job_status_updates")
	if err != nil {
		t.Error(err)
	}
}

func TestMsg(t *testing.T) {
	if !shouldrun() {
		return
	}
	db = initdb(t)
	defer db.Close()
	me, err := os.Hostname()
	if err != nil {
		t.Error(err)
	}
	j := &model.Job{InvocationID: "test-invocation-id"}
	expected := &messaging.UpdateMessage{
		Job:     j,
		State:   "RUNNING",
		Message: "this is a test",
		SentOn:  strconv.FormatInt(time.Now().UnixNano()/int64(time.Millisecond), 10),
		Sender:  me,
	}
	m, err := json.Marshal(expected)
	if err != nil {
		t.Error(err)
	}
	d := amqp.Delivery{
		Body:      m,
		Timestamp: time.Now(),
	}
	msg(d)
	rows, err := db.Query("select status, message, sent_from, sent_from_hostname, sent_on from job_status_updates where external_id = 'test-invocation-id'")
	if err != nil {
		t.Error(err)
	}
	defer rows.Close()
	var (
		status, message, sentFromHostname string
		sentOn                            int64
		sentFrom                          string
	)
	for rows.Next() {
		err = rows.Scan(&status, &message, &sentFrom, &sentFromHostname, &sentOn)
		if err != nil {
			t.Error(err)
		}
		if status != string(expected.State) {
			t.Errorf("status was %s instead of %s", status, expected.State)
		}
		if message != expected.Message {
			t.Errorf("message was %s instead of %s", message, expected.Message)
		}
		ips, err := net.LookupIP(expected.Sender)
		if err != nil {
			t.Error(err)
		}
		var expectedSentFrom string
		if len(ips) > 0 {
			expectedSentFrom = ips[0].String()
		} else {
			t.Error("Couldn't get ip address")
		}
		if sentFrom != expectedSentFrom {
			t.Errorf("sentFrom was %s instead of %s", sentFrom, expectedSentFrom)
		}
		if sentFromHostname != me {
			t.Errorf("sentFromHostname was %s instead of %s", sentFromHostname, me)
		}
		actual := strconv.FormatInt(sentOn, 10)
		if expected.SentOn != actual {
			t.Errorf("sentOn was %s instead of %s", actual, expected.SentOn)
		}
	}
	err = rows.Err()
	if err != nil {
		t.Error(err)
	}
	_, err = db.Exec("DELETE FROM job_status_updates")
	if err != nil {
		t.Error(err)
	}
}
