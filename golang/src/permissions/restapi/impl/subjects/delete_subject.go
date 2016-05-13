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

func BuildDeleteSubjectHandler(db *sql.DB) func(subjects.DeleteSubjectParams) middleware.Responder {

	// Return the handler function.
	return func(params subjects.DeleteSubjectParams) middleware.Responder {
		id := models.InternalSubjectID(params.ID)

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewDeleteSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the subject exists.
		exists, err := permsdb.SubjectExists(tx, id)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewDeleteSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			tx.Rollback()
			reason := fmt.Sprintf("subject, %s, not found", string(id))
			return subjects.NewDeleteSubjectNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Delete the subject.
		if err := permsdb.DeleteSubject(tx, id); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewDeleteSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewDeleteSubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return subjects.NewDeleteSubjectOK()
	}
}
