package db

import (
	"permissions/models"
)

type PermissionDto struct {
	ID                string
	InternalSubjectID string
	SubjectID         string
	SubjectType       string
	ResourceID        string
	ResourceName      string
	ResourceType      string
	PermissionLevel   string
}

func (p *PermissionDto) ToPermission() *models.Permission {

	// Extract the subject.
	subject := &models.SubjectOut{
		ID:          models.InternalSubjectID(p.InternalSubjectID),
		SubjectID:   models.ExternalSubjectID(p.SubjectID),
		SubjectType: models.SubjectType(p.SubjectType),
	}

	// Extract the resource.
	resource := &models.ResourceOut{
		ID:           &p.ResourceID,
		Name:         &p.ResourceName,
		ResourceType: &p.ResourceType,
	}

	// Extract the permission itself.
	permission := &models.Permission{
		ID:              models.PermissionID(p.ID),
		PermissionLevel: models.PermissionLevel(p.PermissionLevel),
		Resource:        resource,
		Subject:         subject,
	}

	return permission
}
