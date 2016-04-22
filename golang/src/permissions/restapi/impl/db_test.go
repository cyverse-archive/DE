package impl

import (
	"database/sql"
	"os"
	"testing"
)

func shouldrun() bool {
	return os.Getenv("RUN_INTEGRATION_TESTS") != ""
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
