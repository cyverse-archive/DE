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
	resourceTypes := make([]*models.ResourceTypeOut, 0)
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
	query := `SELECT id, name, description FROM resource_types
	          WHERE lower(trim(regexp_replace(name, '\s+', ' ', 'g')))
	              = lower(trim(regexp_replace($1, '\s+', ' ', 'g')))`
	rows, err := tx.Query(query, name)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the resource type.
	resourceTypes := make([]*models.ResourceTypeOut, 0)
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

func GetDuplicateResourceTypeByName(tx *sql.Tx, id *string, name *string) (*models.ResourceTypeOut, error) {

	// Query the database.
	query := `SELECT id, name, description FROM resource_types
	          WHERE id != $1
	          AND lower(trim(regexp_replace(name, '\s+', ' ', 'g')))
	            = lower(trim(regexp_replace($2, '\s+', ' ', 'g')))`
	rows, err := tx.Query(query, id, name)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Get the resource type.
	resourceTypes := make([]*models.ResourceTypeOut, 0)
	for rows.Next() {
		var resourceType models.ResourceTypeOut
		if err := rows.Scan(&resourceType.ID, &resourceType.Name, &resourceType.Description); err != nil {
			return nil, err
		}
		resourceTypes = append(resourceTypes, &resourceType)
	}

	// Check for duplicates. There's a uniqueness constraint on the database so this shouldn't happen.
	if len(resourceTypes) > 1 {
		return nil, fmt.Errorf("found multiple duplicate resource types with the name: %s", *name)
	}

	// Return the result.
	if len(resourceTypes) < 1 {
		return nil, nil
	} else {
		return resourceTypes[0], nil
	}
}

func ResourceTypeExists(tx *sql.Tx, id *string) (bool, error) {

	// Query the database.
	query := "SELECT count(*) FROM resource_types WHERE id = $1"
	row := tx.QueryRow(query, id)

	// Get the result.
	var count uint32
	if err := row.Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

func AddNewResourceType(tx *sql.Tx, resourceTypeIn *models.ResourceTypeIn) (*models.ResourceTypeOut, error) {

	// Insert the resource type.
	query := `INSERT INTO resource_types (name, description)
	          VALUES (trim(regexp_replace($1, '\s+', ' ', 'g')), $2)
	          RETURNING id, name, description`
	row := tx.QueryRow(query, resourceTypeIn.Name, resourceTypeIn.Description)

	// Get the newly created resource type.
	var resourceTypeOut models.ResourceTypeOut
	if err := row.Scan(&resourceTypeOut.ID, &resourceTypeOut.Name, &resourceTypeOut.Description); err != nil {
		return nil, err
	}
	return &resourceTypeOut, nil
}

func UpdateResourceType(
	tx *sql.Tx,
	id *string,
	resourceTypeIn *models.ResourceTypeIn,
) (*models.ResourceTypeOut, error) {

	// Update the databse.
	statement := `UPDATE resource_types
	              SET name = trim(regexp_replace($1, '\s+', ' ', 'g')),
	                  description = $2
	              WHERE id = $3
	              RETURNING id, name, description`
	row := tx.QueryRow(statement, resourceTypeIn.Name, resourceTypeIn.Description, id)

	// Get the newly updated resource type.
	var resourceTypeOut models.ResourceTypeOut
	if err := row.Scan(&resourceTypeOut.ID, &resourceTypeOut.Name, &resourceTypeOut.Description); err != nil {
		return nil, err
	}

	return &resourceTypeOut, nil
}

func DeleteResourceType(tx *sql.Tx, id *string) error {

	// Update the database.
	statement := "DELETE FROM resource_types WHERE id = $1"
	result, err := tx.Exec(statement, id)
	if err != nil {
		return err
	}

	// Verify that a row was deleted.
	count, err := result.RowsAffected()
	if err != nil {
		return err
	}
	if count == 0 {
		return fmt.Errorf("no resource types deleted for id %s", *id)
	}
	if count > 1 {
		return fmt.Errorf("multiple resource types deleted for id %s", *id)
	}

	return nil
}
