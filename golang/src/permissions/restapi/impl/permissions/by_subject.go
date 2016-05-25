package permissions

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/clients/grouper"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permissions"
)

func bySubjectOk(perms []*models.Permission) middleware.Responder {
	return permissions.NewBySubjectOK().WithPayload(&models.PermissionList{perms})
}

func bySubjectInternalServerError(reason string) middleware.Responder {
	return permissions.NewBySubjectInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func bySubjectBadRequest(reason string) middleware.Responder {
	return permissions.NewBySubjectBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func BuildBySubjectHandler(
	db *sql.DB, grouperClient grouper.Grouper,
) func(permissions.BySubjectParams) middleware.Responder {

	// Return the handler function.
	return func(params permissions.BySubjectParams) middleware.Responder {
		subjectType := params.SubjectType
		subjectId := params.SubjectID

		// Extract the lookup flag.
		lookup := false
		if params.Lookup != nil {
			lookup = *params.Lookup
		}

		// Create a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		// Verify that the subject type is correct.
		subject, err := permsdb.GetSubjectByExternalId(tx, models.ExternalSubjectID(subjectId))
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}
		if subject != nil && string(subject.SubjectType) != subjectType {
			tx.Rollback()
			reason := fmt.Sprintf("incorrect type for subject, %s: %s", subjectId, subjectType)
			return bySubjectBadRequest(reason)
		}

		// Get the list of subject IDs to use for the query.
		subjectIds, err := buildSubjectIdList(grouperClient, subjectType, subjectId, lookup)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

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

		return bySubjectOk(permissions)
	}
}
