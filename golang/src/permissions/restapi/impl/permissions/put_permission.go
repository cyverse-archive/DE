package permissions

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permissions"
)

func putPermissionInternalServerError(reason string) middleware.Responder {
	return permissions.NewPutPermissionInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func putPermissionBadRequest(reason string) middleware.Responder {
	return permissions.NewPutPermissionBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func BuildPutPermissionHandler(db *sql.DB) func(permissions.PutPermissionParams) middleware.Responder {

	erf := &ErrorResponseFns{
		InternalServerError: putPermissionInternalServerError,
		BadRequest:          putPermissionBadRequest,
	}

	// Return the handler function.
	return func(params permissions.PutPermissionParams) middleware.Responder {
		req := params.Permission

		// Create a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return putPermissionInternalServerError(err.Error())
		}

		// Either get or add the subject.
		subjectIn := &models.SubjectIn{
			SubjectID:   models.ExternalSubjectID(params.SubjectID),
			SubjectType: models.SubjectType(params.SubjectType),
		}
		subject, errorResponder := getOrAddSubject(tx, subjectIn, erf)
		if errorResponder != nil {
			tx.Rollback()
			return errorResponder
		}

		// Either get or add the resource.
		resourceIn := &models.ResourceIn{
			Name:         &params.ResourceName,
			ResourceType: &params.ResourceType,
		}
		resource, errorResponder := getOrAddResource(tx, resourceIn, erf)
		if errorResponder != nil {
			tx.Rollback()
			return errorResponder
		}

		// Look up the permission level.
		permissionLevelId, errorResponder := getPermissionLevel(tx, req.PermissionLevel, erf)
		if errorResponder != nil {
			tx.Rollback()
			return errorResponder
		}

		// Either update or add the permission.
		permission, err := permsdb.UpsertPermission(tx, subject.ID, *resource.ID, *permissionLevelId)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return putPermissionInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return putPermissionInternalServerError(err.Error())
		}

		return permissions.NewPutPermissionOK().WithPayload(permission)
	}
}
