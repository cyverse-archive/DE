package subjects

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/subjects"
)

func BuildListSubjectsHandler(db *sql.DB) func() middleware.Responder {

	// Return the handler function.
	return func() middleware.Responder {

		// Start a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewListSubjectsInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Obtain the list of subjects.
		result, err := permsdb.ListSubjects(tx)
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewListSubjectsInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction for the request.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return subjects.NewListSubjectsInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Return the result.
		return subjects.NewListSubjectsOK().WithPayload(&models.SubjectsOut{result})
	}
}
