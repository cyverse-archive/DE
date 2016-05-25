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

func bySubjectAndResourceOk(permissions []*models.Permission) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceOK().WithPayload(&models.PermissionList{permissions})
}

func bySubjectAndResourceInternalServerError(reason string) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func bySubjectAndResourceBadRequest(reason string) middleware.Responder {
	return permission_lookup.NewBySubjectAndResourceBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func BuildBySubjectAndResourceHandler(
	db *sql.DB, grouperClient grouper.Grouper,
) func(permission_lookup.BySubjectAndResourceParams) middleware.Responder {

	// Return the handler function.
	return func(params permission_lookup.BySubjectAndResourceParams) middleware.Responder {
		subjectType := params.SubjectType
		subjectId := params.SubjectID
		resourceTypeName := params.ResourceType
		resourceName := params.ResourceName

		// Start a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}

		// Verify that the subject type is correct.
		subject, err := permsdb.GetSubjectByExternalId(tx, models.ExternalSubjectID(subjectId))
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}
		if subject != nil && string(subject.SubjectType) != subjectType {
			tx.Rollback()
			reason := fmt.Sprintf("incorrect type for subject, %s: %s", subjectId, subjectType)
			return bySubjectAndResourceBadRequest(reason)
		}

		// Verify that the resource type exists.
		resourceType, err := permsdb.GetResourceTypeByName(tx, &resourceTypeName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}
		if resourceType == nil {
			tx.Rollback()
			return bySubjectAndResourceOk(make([]*models.Permission, 0))
		}

		// Verify that the resource exists.
		resource, err := permsdb.GetResourceByName(tx, &resourceName, resourceType.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}
		if resource == nil {
			tx.Rollback()
			return bySubjectAndResourceOk(make([]*models.Permission, 0))
		}

		// Get the list of group IDs.
		groupIds, err := groupIdsForSubject(grouperClient, subjectType, subjectId)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}

		// The list of subject IDs is just the current subject ID plus the list of group IDs.
		subjectIds := append(groupIds, subjectId)

		// Perform the lookup.
		permissions, err := permsdb.PermissionsForSubjectsAndResource(tx, subjectIds, resourceTypeName, resourceName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}

		// Commit the transaction.
		err = tx.Commit()
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return bySubjectAndResourceInternalServerError(err.Error())
		}

		return bySubjectAndResourceOk(permissions)
	}
}
