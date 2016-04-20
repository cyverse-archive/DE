package db

import (
	"database/sql"
	"fmt"
	"permissions/models"
)

func ListResourceTypes(tx *sql.Tx) ([]*models.ResourceTypeOut, error) {

	// Query the database.
	query := "SELECT id, name, description FROM resource_types"
	rows, err := tx.Query(query)
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

func GetResourceTypeByName(tx *sql.Tx, name *string) (*models.ResourceTypeOut, error) {

	// Query the database.
	query := "SELECT id, name, description FROM resource_types WHERE name = $1"
	rows, err := tx.Query(query, name)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the resource type.
	var resourceTypes []*models.ResourceTypeOut
	for rows.Next() {
		var resourceType models.ResourceTypeOut
		if err := rows.Scan(&resourceType.ID, &resourceType.Name, &resourceType.Description); err != nil {
			return nil, err
		}
		resourceTypes = append(resourceTypes, &resourceType)
	}

	// Check for duplicates. There's a uniqueness constraint on the database so this shouldn't happen.
	if len(resourceTypes) > 1 {
		return nil, fmt.Errorf("found multiple resource types with the name: %s", *name)
	}

	// Return the result.
	if len(resourceTypes) < 1 {
		return nil, nil
	} else {
		return resourceTypes[0], nil
	}
}

func AddNewResourceType(tx *sql.Tx, resourceTypeIn *models.ResourceTypeIn) (*models.ResourceTypeOut, error) {

	// Insert the resource type.
	query := "INSERT INTO resource_types (name, description) VALUES ($1, $2) RETURNING id"
	row := tx.QueryRow(query, resourceTypeIn.Name, resourceTypeIn.Description)

	// Get the newly created resource type.
	resourceTypeOut := models.ResourceTypeOut{Name: resourceTypeIn.Name, Description: resourceTypeIn.Description}
	if err := row.Scan(&resourceTypeOut.ID); err != nil {
		return nil, err
	}
	return &resourceTypeOut, nil
}
