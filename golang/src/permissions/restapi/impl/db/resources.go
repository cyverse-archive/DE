package db

import (
	"database/sql"
	"fmt"
	"permissions/models"
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

func GetResourceByName(tx *sql.Tx, name *string, resourceTypeId *string) (*models.ResourceOut, error) {

	// Query the database.
	query := `SELECT r.id, r.name, t.name AS resource_type
            FROM resources r JOIN resource_types t ON r.resource_type_id = t.id
            WHERE t.id = $1 and r.name = $2`
	rows, err := tx.Query(query, resourceTypeId, name)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the resources.
	var resources []*models.ResourceOut
	for rows.Next() {
		var resource models.ResourceOut
		if err := rows.Scan(&resource.ID, &resource.Name, &resource.ResourceType); err != nil {
			return nil, err
		}
		resources = append(resources, &resource)
	}

	// Check for duplicates. There's a uniqueness constraint on the database, so this shouldn't happen.
	if len(resources) > 1 {
		return nil, fmt.Errorf("found multiple resources of the same type named, '%s'", *name)
	}

	// Return the result.
	if len(resources) < 1 {
		return nil, nil
	} else {
		return resources[0], nil
	}
}

func AddResource(tx *sql.Tx, name *string, resourceTypeId *string) (*models.ResourceOut, error) {

	// Update the database.
	query := `INSERT INTO resources (name, resource_type_id) VALUES ($1, $2)
            RETURNING id, name, (SELECT name FROM resource_types t WHERE t.id = resource_type_id)`
	row := tx.QueryRow(query, name, resourceTypeId)

	// Return the result.
	var resource models.ResourceOut
	if err := row.Scan(&resource.ID, &resource.Name, &resource.ResourceType); err != nil {
		return nil, err
	}
	return &resource, nil
}

func ListResources(tx *sql.Tx) ([]*models.ResourceOut, error) {

	// Query the database.
	query := `SELECT r.id, r.name, t.name AS resource_type
            FROM resources r JOIN resource_types t ON r.resource_type_id = t.id`
	rows, err := tx.Query(query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of resources.
	var resources []*models.ResourceOut
	for rows.Next() {
		var resource models.ResourceOut
		if err := rows.Scan(&resource.ID, &resource.Name, &resource.ResourceType); err != nil {
			return nil, err
		}
		resources = append(resources, &resource)
	}

	return resources, nil
}
