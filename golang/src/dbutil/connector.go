package dbutil

import (
	"database/sql"
	"fmt"
	"time"
)

// Connector is used to establish and verify database connections within a set timeout period.
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

func (c Connector) connect(endTime time.Time, driverName, uri string) (*sql.DB, error) {
	delay := time.Duration(0)

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

func (c Connector) ping(endTime time.Time, db *sql.DB) error {
	delay := time.Duration(0)

	// Try to ping the database until we succeed or time out.
	err := error(nil)
	for {

		// Sleep for the current delay before trying to ping the database.
		time.Sleep(delay)

		// Attempt to ping the database.
		err = db.Ping()
		if err == nil {
			return nil
		}

		// Return the error if the timeout period has expired.
		if !endTime.After(time.Now()) {
			return err
		}
	}
}

// Connect attempts to establish and verify a database connection within a set timeout period. If the connection
// can't be established or the database can't be successfully pinged within the timeout period then an error is
// returned.
func (c Connector) Connect(driverName, uri string) (*sql.DB, error) {

	// Determine when the timeout period expires.
	endTime := time.Now().Add(c.Timeout)

	// Attempt to establish the database connection.
	db, err := c.connect(endTime, driverName, uri)
	if err != nil {
		return nil, err
	}

	// Attempt to ping the database.
	if err := c.ping(endTime, db); err != nil {
		db.Close()
		return nil, err
	}
	return db, nil
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
