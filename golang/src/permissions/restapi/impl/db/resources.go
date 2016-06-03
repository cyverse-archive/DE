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

func ResourceExists(tx *sql.Tx, id *string) (bool, error) {

	// Query the database.
	query := "SELECT count(*) FROM resources WHERE id = $1"
	row := tx.QueryRow(query, id)

	// Get the result.
	var count uint32
	if err := row.Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
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
	resources := make([]*models.ResourceOut, 0)
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
	}
	return resources[0], nil
}

func GetDuplicateResourceByName(tx *sql.Tx, id *string, name *string) (*models.ResourceOut, error) {

	// Query the database.
	query := `SELECT r.id, r.name, t.name AS resource_type
            FROM resources r JOIN resource_types t ON r.resource_type_id = t.id
            WHERE r.id != $1
            AND r.name = $2
            AND r.resource_type_id = (SELECT resource_type_id FROM resources WHERE id = $1)`
	rows, err := tx.Query(query, id, name)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the resources.
	resources := make([]*models.ResourceOut, 0)
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
	}
	return resources[0], nil
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

func UpdateResource(tx *sql.Tx, id *string, name *string) (*models.ResourceOut, error) {

	// Update the database.
	query := `UPDATE resources SET name = $1 WHERE id = $2
            RETURNING id, name, (SELECT name FROM resource_types t WHERE t.id = resource_type_id)`
	row := tx.QueryRow(query, name, id)

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
	resources := make([]*models.ResourceOut, 0)
	for rows.Next() {
		var resource models.ResourceOut
		if err := rows.Scan(&resource.ID, &resource.Name, &resource.ResourceType); err != nil {
			return nil, err
		}
		resources = append(resources, &resource)
	}

	return resources, nil
}

func DeleteResource(tx *sql.Tx, id *string) error {

	// Update the database.
	stmt := "DELETE FROM resources WHERE id = $1"
	result, err := tx.Exec(stmt, id)
	if err != nil {
		return err
	}

	// Verify that a row was deleted.
	count, err := result.RowsAffected()
	if err != nil {
		return err
	}
	if count == 0 {
		return fmt.Errorf("no resources deleted for id %s", *id)
	}
	if count > 1 {
		return fmt.Errorf("multiple resources deleted for id %s", *id)
	}

	return nil
}
