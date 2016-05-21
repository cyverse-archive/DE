package permission_lookup

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/clients/grouper"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permission_lookup"
)

func bySubjectInternalServerError(reason string) middleware.Responder {
	return permission_lookup.NewBySubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func BuildBySubjectHandler(
	db *sql.DB, grouperClient grouper.Grouper,
) func(permission_lookup.BySubjectParams) middleware.Responder {

	// Return the handler function.
	return func(params permission_lookup.BySubjectParams) middleware.Responder {

		// Create a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		// Get the list of group IDs.
		groupIds, err := groupIdsForSubject(grouperClient, params.SubjectType, params.SubjectID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		// The list of subject IDs is just the current subject ID plus the list of group IDs.
		subjectIds := append(groupIds, params.SubjectID)

		// Perform the lookup.
		permissions, err := permsdb.PermissionsForSubjects(tx, subjectIds)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		return permission_lookup.NewBySubjectOK().WithPayload(&models.PermissionList{permissions})
	}
}
