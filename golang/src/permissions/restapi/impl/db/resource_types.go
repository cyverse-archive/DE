package db

import (
	"database/sql"
	"permissions/models"
)

func ListResourceTypes(db *sql.DB) ([]*models.ResourceTypeOut, error) {

	// Query the database.
	query := "SELECT id, name, description FROM resource_types"
	rows, err := db.Query(query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of resource types.
	var resourceTypes []*models.ResourceTypeOut
	for rows.Next() {
		var resourceType models.ResourceTypeOut
		if err := rows.Scan(&resourceType.ID, &resourceType.Name, &resourceType.Description); err != nil {
			return nil, err
		}
		resourceTypes = append(resourceTypes, &resourceType)
	}

	// Check for any uncaught errors.
	if err := rows.Err(); err != nil {
		return resourceTypes, err
	}

	return resourceTypes, nil
}
