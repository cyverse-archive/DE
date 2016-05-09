package test

import (
	"database/sql"
	"fmt"
	"permissions/models"
	"permissions/restapi/operations/subjects"
	"testing"

	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	impl "permissions/restapi/impl/subjects"
)

func addSubjectAttempt(
	db *sql.DB,
	subjectId models.ExternalSubjectID,
	subjectType models.SubjectType,
) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildAddSubjectHandler(db)

	// Attempt to add the subject to the database.
	subjectIn := &models.SubjectIn{SubjectID: subjectId, SubjectType: subjectType}
	params := subjects.AddSubjectParams{SubjectIn: subjectIn}
	return handler(params)
}

func addSubject(db *sql.DB, subjectId models.ExternalSubjectID, subjectType models.SubjectType) *models.SubjectOut {
	responder := addSubjectAttempt(db, subjectId, subjectType)
	return responder.(*subjects.AddSubjectCreated).Payload
}

func TestAddSubject(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add a subject.
	subjectId := models.ExternalSubjectID("nobody")
	subjectType := models.SubjectType("user")
	subject := addSubject(db, subjectId, subjectType)

	// Verify that we got the expected response.
	if subject.SubjectID != subjectId {
		t.Errorf("unexpected subject ID: %s", subject.SubjectID)
	}
	if subject.SubjectType != subjectType {
		t.Errorf("unexpected subject type: %s", subject.SubjectType)
	}
}

func TestAddDuplicateSubject(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add a subject.
	subjectId := models.ExternalSubjectID("nobody")
	subjectType := models.SubjectType("user")
	addSubject(db, subjectId, subjectType)

	// Attempt to add a subject with the same ID.
	responder := addSubjectAttempt(db, subjectId, subjectType)
	errorOut := responder.(*subjects.AddSubjectBadRequest).Payload

	// Verify that we got the expected result.
	expected := fmt.Sprintf("subject, %s, already exists", string(subjectId))
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}
