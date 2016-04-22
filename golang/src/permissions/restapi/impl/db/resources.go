package db

import (
	"database/sql"
)

func CountResourcesOfType(tx *sql.Tx, resourceTypeId *string) (int64, error) {

	// Query the database.
	query := "SELECT count(*) FROM resources WHERE resource_type_id = $1"
	row := tx.QueryRow(query, resourceTypeId)

	// Return the result.
	var count int64
	if err := row.Scan(&count); err != nil {
		return 0, err
	}
	return count, nil
}
