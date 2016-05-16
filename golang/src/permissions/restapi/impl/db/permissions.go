package db

import (
	"database/sql"
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
            JOIN resources r ON s.resource_id = r.id
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
