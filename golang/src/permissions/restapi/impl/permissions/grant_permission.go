package permissions

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permissions"
)

func grantPermissionInternalServerError(reason string) middleware.Responder {
	return permissions.NewGrantPermissionInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func grantPermissionBadRequest(reason string) middleware.Responder {
	return permissions.NewGrantPermissionBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func grantPermissionGetOrAddSubject(
	tx *sql.Tx,
	subjectIn *models.SubjectIn,
) (*models.SubjectOut, middleware.Responder) {

	// Attempt to look up the subject.
	subject, err := permsdb.GetSubject(tx, subjectIn.SubjectID, subjectIn.SubjectType)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError(err.Error())
	}
	if subject != nil {
		return subject, nil
	}

	// Make sure that another subject with the same ID doesn't exist already.
	exists, err := permsdb.SubjectIdExists(tx, subjectIn.SubjectID)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError((err.Error()))
	}
	if exists {
		reason := fmt.Sprintf("another subject with ID, %s, already exists", string(subjectIn.SubjectID))
		return nil, grantPermissionBadRequest(reason)
	}

	// Attempt to add the subject.
	subject, err = permsdb.AddSubject(tx, subjectIn.SubjectID, subjectIn.SubjectType)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError(err.Error())
	}
	return subject, nil
}

func grantPermissionGetOrAddResource(
	tx *sql.Tx,
	resourceIn *models.ResourceIn,
) (*models.ResourceOut, middleware.Responder) {

	// Look up the resource type.
	resourceType, err := permsdb.GetResourceTypeByName(tx, resourceIn.ResourceType)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError(err.Error())
	}
	if resourceType == nil {
		reason := fmt.Sprintf("no resource type named, %s, found", resourceIn.ResourceType)
		return nil, grantPermissionBadRequest(reason)
	}

	// Attempt to look up the resource.
	resource, err := permsdb.GetResourceByName(tx, resourceIn.Name, resourceType.ID)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError(err.Error())
	}
	if resource != nil {
		return resource, nil
	}

	// Attempt to add the resource.
	resource, err = permsdb.AddResource(tx, resourceIn.Name, resourceType.ID)
	if err != nil {
		logcabin.Error.Print(err)
		return nil, grantPermissionInternalServerError(err.Error())
	}
	return resource, nil
}

func BuildGrantPermissionHandler(db *sql.DB) func(permissions.GrantPermissionParams) middleware.Responder {

	// Return the hnadler function.
	return func(params permissions.GrantPermissionParams) middleware.Responder {
		req := params.PermissionGrantRequest

		// Create a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return grantPermissionInternalServerError(err.Error())
		}

		// Either get or add the subject.
		subject, errorResponder := grantPermissionGetOrAddSubject(tx, req.Subject)
		if errorResponder != nil {
			tx.Rollback()
			return errorResponder
		}

		// Either get or add the resource.
		resource, errorResponder := grantPermissionGetOrAddResource(tx, req.Resource)
		if errorResponder != nil {
			tx.Rollback()
			return errorResponder
		}

		// Look up the permission level.
		permissionLevelId, err := permsdb.GetPermissionLevelIdByName(tx, req.PermissionLevel)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return grantPermissionInternalServerError(err.Error())
		}
		if permissionLevelId == nil {
			reason := fmt.Sprintf("no permission level named, %s, found", string(req.PermissionLevel))
			return grantPermissionBadRequest(reason)
		}

		// Either update or add the permission.
		permission, err := permsdb.UpsertPermission(tx, subject.ID, *resource.ID, *permissionLevelId)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return grantPermissionInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return grantPermissionInternalServerError(err.Error())
		}

		return permissions.NewGrantPermissionOK().WithPayload(permission)
	}
}
