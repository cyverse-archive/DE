package subjects

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/subjects"
)

func listSubjectsInternalServerError(reason string) middleware.Responder {
	return subjects.NewListSubjectsInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func BuildListSubjectsHandler(db *sql.DB) func(subjects.ListSubjectsParams) middleware.Responder {

	// Return the handler function.
	return func(params subjects.ListSubjectsParams) middleware.Responder {

		// Start a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return listSubjectsInternalServerError(err.Error())
		}

		// Obtain the list of subjects.
		result, err := permsdb.ListSubjects(tx, params.SubjectType, params.SubjectID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return listSubjectsInternalServerError(err.Error())
		}

		// Commit the transaction for the request.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return listSubjectsInternalServerError(err.Error())
		}

		// Return the result.
		return subjects.NewListSubjectsOK().WithPayload(&models.SubjectsOut{result})
	}
}
