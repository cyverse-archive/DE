package db

import (
	"database/sql"
	"fmt"
	"permissions/models"
)

func rowsToPermissionList(rows *sql.Rows) ([]*models.Permission, error) {

	// Build the list of permissions.
	permissions := make([]*models.Permission, 0)
	for rows.Next() {
		var dto PermissionDto
		err := rows.Scan(
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

	return rowsToPermissionList(rows)
}

func ListResourcePermissions(tx *sql.Tx, resourceTypeName, resourceName string) ([]*models.Permission, error) {

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
            WHERE rt.name = $1 AND r.name = $2
	          ORDER BY s.subject_id`
	rows, err := tx.Query(query, resourceTypeName, resourceName)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjects(tx *sql.Tx, subjectIds []string) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjectsMinLevel(tx *sql.Tx, subjectIds []string, minLevel string) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
            AND pl.precedence <= (SELECT precedence FROM permission_levels WHERE name = $2)
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa, minLevel)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjectsAndResourceType(
	tx *sql.Tx, subjectIds []string, resourceTypeName string,
) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
            AND rt.Name = $2
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa, resourceTypeName)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjectsAndResourceTypeMinLevel(
	tx *sql.Tx, subjectIds []string, resourceTypeName, minLevel string,
) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
            AND rt.Name = $2
            AND pl.precedence <= (SELECT precedence FROM permission_levels WHERE name = $3)
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa, resourceTypeName, minLevel)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjectsAndResource(
	tx *sql.Tx, subjectIds []string, resourceTypeName, resourceName string,
) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
            AND rt.name = $2
	          AND r.name = $3
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa, resourceTypeName, resourceName)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
}

func PermissionsForSubjectsAndResourceMinLevel(
	tx *sql.Tx, subjectIds []string, resourceTypeName, resourceName, minLevel string,
) ([]*models.Permission, error) {
	sa := StringArray(subjectIds)

	// Query the database.
	query := `SELECT DISTINCT ON (r.id)
	              first_value(p.id) OVER w AS id,
	              first_value(s.id) OVER w AS internal_subject_id,
	              first_value(s.subject_id) OVER w AS subject_id,
	              first_value(s.subject_type) OVER w AS subject_type,
	              r.id AS resource_id,
	              first_value(r.name) OVER w AS resource_name,
	              first_value(rt.name) OVER w AS resource_type,
	              first_value(pl.name) OVER w AS permission_level
	          FROM permissions p
	          JOIN permission_levels pl ON p.permission_level_id = pl.id
	          JOIN subjects s ON p.subject_id = s.id
	          JOIN resources r ON p.resource_id = r.id
	          JOIN resource_types rt ON r.resource_type_id = rt.id
	          WHERE s.subject_id = any($1)
            AND rt.name = $2
	          AND r.name = $3
            AND pl.precedence <= (SELECT precedence FROM permission_levels WHERE name = $4)
	          WINDOW w AS (PARTITION BY r.id ORDER BY pl.precedence)
            ORDER BY r.id`
	rows, err := tx.Query(query, &sa, resourceTypeName, resourceName, minLevel)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	return rowsToPermissionList(rows)
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
	permissions, err := rowsToPermissionList(rows)
	if err != nil {
		return nil, err
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
	}
	return permission, nil
}

func GetPermission(
	tx *sql.Tx,
	subjectId models.InternalSubjectID,
	resourceId string,
) (*models.Permission, error) {

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
	          WHERE p.subject_id = $1
	          AND p.resource_id = $2`
	rows, err := tx.Query(query, string(subjectId), resourceId)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Build the list of permissions.
	permissions, err := rowsToPermissionList(rows)
	if err != nil {
		return nil, err
	}

	// Check for duplicates. This shouldn't happen because of the uniqueness constraint.
	if len(permissions) > 1 {
		return nil, fmt.Errorf("multiple permissions found for subject/resource: %s/%s", subjectId, resourceId)
	}

	// Return the result.
	if len(permissions) < 1 {
		return nil, nil
	}
	return permissions[0], nil
}

func DeletePermission(tx *sql.Tx, id models.PermissionID) error {

	// Update the database.
	stmt := "DELETE FROM permissions WHERE id = $1"
	result, err := tx.Exec(stmt, string(id))
	if err != nil {
		return err
	}

	// Verify that a row was deleted.
	count, err := result.RowsAffected()
	if err != nil {
		return err
	}
	if count == 0 {
		return fmt.Errorf("no permissions deleted for id %s", id)
	}
	if count > 1 {
		return fmt.Errorf("multiple permissions deleted for id %s", id)
	}

	return nil
}
