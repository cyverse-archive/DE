package test

import (
	"database/sql"
	"fmt"
	"os"
	"testing"

	_ "github.com/lib/pq"
)

const FAKE_ID = "1A960034-969A-46A7-B6B5-3F1866258CAB"

func shouldrun() bool {
	return os.Getenv("RUN_INTEGRATION_TESTS") != ""
}

func dburi() string {
	uri := os.Getenv("DBURI")
	if uri != "" {
		return uri
	} else {
		return "postgres://de:notprod@dedb:5432/permissions?sslmode=disable"
	}
}

func truncateTables(db *sql.DB) error {

	// Truncate all tables.
	tables := []string{"permissions", "permission_levels", "subjects", "resources", "resource_types"}
	for _, table := range tables {
		_, err := db.Exec(fmt.Sprintf("DELETE FROM %s", table))
		if err != nil {
			return err
		}
	}

	return nil
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

	if err := truncateTables(db); err != nil {
		t.Error(err)
	}

	return db
}
