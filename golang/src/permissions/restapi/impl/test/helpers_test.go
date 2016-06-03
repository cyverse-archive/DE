package test

import (
	"database/sql"
	"permissions/models"
	"testing"

	permsdb "permissions/restapi/impl/db"
)

func addDefaultResourceType(tx *sql.Tx, name, description string, t *testing.T) {
	rt := &models.ResourceTypeIn{Name: &name, Description: description}
	if _, err := permsdb.AddNewResourceType(tx, rt); err != nil {
		t.Fatalf("unable to add default resource types: %s", err)
	}
}

func addDefaultResourceTypes(db *sql.DB, t *testing.T) {

	// Start a transaction.
	tx, err := db.Begin()
	if err != nil {
		t.Fatalf("unable to add default resource types: %s", err)
	}

	// Add the default resource types.
	addDefaultResourceType(tx, "app", "app", t)
	addDefaultResourceType(tx, "analysis", "analysis", t)

	// Commit the transaction.
	if err := tx.Commit(); err != nil {
		tx.Rollback()
		t.Fatalf("unable to add default resource types: %s", err)
	}
}

func newSubjectIn(subjectId, subjectType string) *models.SubjectIn {
	return &models.SubjectIn{
		SubjectID:   models.ExternalSubjectID(subjectId),
		SubjectType: models.SubjectType(subjectType),
	}
}

func newResourceIn(name, resourceType string) *models.ResourceIn {
	return &models.ResourceIn{
		Name:         &name,
		ResourceType: &resourceType,
	}
}
