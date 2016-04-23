package impl

import (
	"database/sql"
	"os"
	"testing"

	_ "github.com/lib/pq"
)

func shouldrun() bool {
	return os.Getenv("RUN_INTEGRATION_TESTS") != ""
}

func dburi() string {
	return "postgres://de:notprod@dedb:5432/permissions?sslmode=disable"
}

func truncateTables(db *sql.DB) error {

	// Truncate all tables.
	stmt := "TRUNCATE permissions, permission_levels, subjects, resources, resource_types"
	_, err := db.Exec(stmt)
	if err != nil {
		return err
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
