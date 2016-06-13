package util

import (
	"database/sql"
	"fmt"
	"time"
)

type Connector struct {
	DelayStrategy func(time.Duration) time.Duration
	Timeout       time.Duration
}

func defaultDelayStrategy(currentDelay time.Duration) time.Duration {
	if currentDelay <= 0 {
		return time.Duration(1) * time.Second
	}
	return currentDelay * 2
}

func (c Connector) Connect(driverName, uri string) (*sql.DB, error) {
	delay := time.Duration(0)
	endTime := time.Now().Add(c.Timeout)

	// Try to connect until we succeed or time out.
	err := fmt.Errorf("no connection attempts made; verify that the maximum wait time is correct")
	for endTime.After(time.Now()) {

		// Sleep for the current delay before trying to connect.
		time.Sleep(delay)

		// Attempt to establish the database connection.
		db, err := sql.Open(driverName, uri)
		if err == nil {
			return db, nil
		}

		// Update the delay for the next attempt.
		delay = (c.DelayStrategy)(delay)
	}

	return nil, err
}

// NewDefaultConnector returns a new connector using the default delay strategy and a timeout period corresponding
// to the given duration string.
func NewDefaultConnector(timeout string) (*Connector, error) {
	duration, err := time.ParseDuration(timeout)
	if err != nil {
		return nil, err
	}
	return &Connector{
		DelayStrategy: defaultDelayStrategy,
		Timeout:       duration,
	}, nil
}
