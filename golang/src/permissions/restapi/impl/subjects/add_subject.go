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

func BuildAddSubjectHandler(db *sql.DB) func(subjects.AddSubjectParams) middleware.Responder {

	// Return the handler function.
	return func(params subjects.AddSubjectParams) middleware.Responder {
		subjectIn := params.SubjectIn

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewAddSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Make sure that a subject with the same ID doesn't exist already.
		exists, err := permsdb.SubjectIdExists(tx, subjectIn.SubjectID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewAddSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if exists {
			tx.Rollback()
			reason := fmt.Sprintf("subject, %s, already exists", string(subjectIn.SubjectID))
			return subjects.NewAddSubjectBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Add the subject.
		subjectOut, err := permsdb.AddSubject(tx, subjectIn.SubjectID, subjectIn.SubjectType)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewAddSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewAddSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return subjects.NewAddSubjectCreated().WithPayload(subjectOut)
	}
}
