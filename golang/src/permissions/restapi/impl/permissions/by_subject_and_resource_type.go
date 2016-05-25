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

func bySubjectAndResourceTypeOk(perms []*models.Permission) middleware.Responder {
	return permissions.NewBySubjectAndResourceTypeOK().WithPayload(&models.PermissionList{perms})
}

func bySubjectAndResourceTypeInternalServerError(reason string) middleware.Responder {
	return permissions.NewBySubjectAndResourceTypeInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func bySubjectAndResourceTypeBadRequest(reason string) middleware.Responder {
	return permissions.NewBySubjectAndResourceTypeBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func BuildBySubjectAndResourceTypeHandler(
	db *sql.DB, grouperClient grouper.Grouper,
) func(permissions.BySubjectAndResourceTypeParams) middleware.Responder {

	// Return the handler function.
	return func(params permissions.BySubjectAndResourceTypeParams) middleware.Responder {
		subjectType := params.SubjectType
		subjectId := params.SubjectID
		resourceTypeName := params.ResourceType
		lookup := extractLookupFlag(params.Lookup)

		// Create a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}

		// Verify that the subject type is correct.
		subject, err := permsdb.GetSubjectByExternalId(tx, models.ExternalSubjectID(subjectId))
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}
		if subject != nil && string(subject.SubjectType) != subjectType {
			tx.Rollback()
			reason := fmt.Sprintf("incorrect type for subject, %s: %s", subjectId, subjectType)
			return bySubjectAndResourceTypeBadRequest(reason)
		}

		// Verify that the resource type exists.
		resourceType, err := permsdb.GetResourceTypeByName(tx, &resourceTypeName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}
		if resourceType == nil {
			tx.Rollback()
			return bySubjectAndResourceTypeOk(make([]*models.Permission, 0))
		}

		// Get the list of subject IDs to use for the query.
		subjectIds, err := buildSubjectIdList(grouperClient, subjectType, subjectId, lookup)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectInternalServerError(err.Error())
		}

		// Perform the lookup.
		permissions, err := permsdb.PermissionsForSubjectsAndResourceType(tx, subjectIds, resourceTypeName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}

		return bySubjectAndResourceTypeOk(permissions)
	}
}
