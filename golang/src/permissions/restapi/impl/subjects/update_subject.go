package subjects

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/subjects"
)

func BuildUpdateSubjectHandler(db *sql.DB) func(subjects.UpdateSubjectParams) middleware.Responder {

	// Return the handler function.
	return func(params subjects.UpdateSubjectParams) middleware.Responder {
		id := models.InternalSubjectID(params.ID)
		subjectIn := params.SubjectIn

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewUpdateSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the subject exists.
		exists, err := permsdb.SubjectExists(tx, id)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewUpdateSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			reason := fmt.Sprintf("subject, %s, not found", string(id))
			return subjects.NewUpdateSubjectNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that a subject with the same external subject ID doesn't exist.
		duplicateExists, err := permsdb.DuplicateSubjectExists(tx, id, subjectIn.SubjectID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewUpdateSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if duplicateExists {
			reason := fmt.Sprintf("another subject with the ID, %s, already exists", string(subjectIn.SubjectID))
			return subjects.NewUpdateSubjectBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Update the subject.
		subjectOut, err := permsdb.UpdateSubject(tx, id, subjectIn.SubjectID, subjectIn.SubjectType)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewUpdateSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewUpdateSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return subjects.NewUpdateSubjectOK().WithPayload(subjectOut)
	}
}
