package permission_lookup

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/clients/grouper"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permission_lookup"
)

func bySubjectAndResourceTypeOk(permissions []*models.Permission) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceTypeOK().WithPayload(&models.PermissionList{permissions})
}

func bySubjectAndResourceTypeInternalServerError(reason string) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceTypeInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func bySubjectAndResourceTypeBadRequest(reason string) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceTypeBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func BuildBySubjectAndResourceTypeHandler(
	db *sql.DB, grouperClient grouper.Grouper,
) func(permission_lookup.BySubjectAndResourceTypeParams) middleware.Responder {

	// Return the handler function.
	return func(params permission_lookup.BySubjectAndResourceTypeParams) middleware.Responder {
		subjectType := params.SubjectType
		subjectId := params.SubjectID
		resourceTypeName := params.ResourceType

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

		// Get the list of group IDs.
		groupIds, err := groupIdsForSubject(grouperClient, subjectType, subjectId)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceTypeInternalServerError(err.Error())
		}

		// The list of subject IDs is just the current subject ID plus the list of group IDs.
		subjectIds := append(groupIds, subjectId)

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
