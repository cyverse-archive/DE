package db

import (
	"database/sql"
	"fmt"
	"permissions/models"
)

func ListPermissions(tx *sql.Tx) ([]*models.Permission, error) {

	// Query the database.
	query := `SELECT p.id AS id,
                   s.id AS internal_subject_id,
                   s.subject_id AS subject_id,
                   s.subject_type AS subject_type,
                   r.id AS resource_id,
                   r.name AS resource_name,
                   rt.name AS resource_type,
                   pl.name AS permission_level
            FROM permissions p
            JOIN permission_levels pl ON p.permission_level_id = pl.id
            JOIN subjects s ON p.subject_id = s.id
            JOIN resources r ON p.resource_id = r.id
            JOIN resource_types rt ON r.resource_type_id = rt.id
            ORDER BY s.subject_id, r.name, pl.precedence`
	rows, err := tx.Query(query)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of permissions.
	permissions := make([]*models.Permission, 0)
	for rows.Next() {
		var dto PermissionDto
		err = rows.Scan(
			&dto.ID, &dto.InternalSubjectID, &dto.SubjectID, &dto.SubjectType, &dto.ResourceID,
			&dto.ResourceName, &dto.ResourceType, &dto.PermissionLevel,
		)
		if err != nil {
			return nil, err
		}
		permissions = append(permissions, dto.ToPermission())
	}

	return permissions, nil
}

func GetPermissionById(tx *sql.Tx, permissionId string) (*models.Permission, error) {

	// Query the database.
	query := `SELECT p.id AS id,
                   s.id AS internal_subject_id,
                   s.subject_id AS subject_id,
                   s.subject_type AS subject_type,
                   r.id AS resource_id,
                   r.name AS resource_name,
                   rt.name AS resource_type,
                   pl.name AS permission_level
            FROM permissions p
            JOIN permission_levels pl ON p.permission_level_id = pl.id
            JOIN subjects s ON p.subject_id = s.id
            JOIN resources r ON p.resource_id = r.id
            JOIN resource_types rt ON r.resource_type_id = rt.id
            WHERE p.id = $1`
	rows, err := tx.Query(query, &permissionId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of permissions.
	permissions := make([]*models.Permission, 0)
	for rows.Next() {
		var dto PermissionDto
		err = rows.Scan(
			&dto.ID, &dto.InternalSubjectID, &dto.SubjectID, &dto.SubjectType, &dto.ResourceID,
			&dto.ResourceName, &dto.ResourceType, &dto.PermissionLevel,
		)
		if err != nil {
			return nil, err
		}
		permissions = append(permissions, dto.ToPermission())
	}

	// Check for duplicates. This shouldn't happen because of the primary key constraint.
	if len(permissions) > 1 {
		return nil, fmt.Errorf("multiple permissions found for ID: %s", permissionId)
	}

	// Return the result.
	if len(permissions) < 1 {
		return nil, nil
	}
	return permissions[0], nil
}

func GetPermissionLevelIdByName(tx *sql.Tx, level models.PermissionLevel) (*string, error) {

	// Query the database.
	query := "SELECT id FROM permission_levels WHERE name = $1"
	rows, err := tx.Query(query, string(level))
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of permission levels.
	ids := make([]*string, 0)
	for rows.Next() {
		var id string
		err = rows.Scan(&id)
		if err != nil {
			return nil, err
		}
		ids = append(ids, &id)
	}

	// Check for duplicate results. This shouldn't happen because there's a uniqueness constraint.
	if len(ids) > 1 {
		return nil, fmt.Errorf("duplicate permission levels found: %s", string(level))
	}

	// Return the result.
	if len(ids) < 1 {
		return nil, nil
	}
	return ids[0], nil
}

func UpsertPermission(
	tx *sql.Tx,
	subjectId models.InternalSubjectID,
	resourceId string,
	permissionLevelId string,
) (*models.Permission, error) {

	// Update the database.
	stmt := `INSERT INTO permissions (subject_id, resource_id, permission_level_id) VALUES ($1, $2, $3)
           ON CONFLICT (subject_id, resource_id) DO UPDATE
           SET permission_level_id = EXCLUDED.permission_level_id
           RETURNING id`
	row := tx.QueryRow(stmt, string(subjectId), resourceId, permissionLevelId)

	// Extract the permission ID.
	var permissionId string
	if err := row.Scan(&permissionId); err != nil {
		return nil, err
	}

	// Look up the permission.
	permission, err := GetPermissionById(tx, permissionId)
	if err != nil {
		return nil, err
	} else if permission == nil {
		return nil, fmt.Errorf("unable to look up permission after upsert: %s", permissionId)
	} else {
		return permission, nil
	}
}
