package permissions

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permissions"
)

func listResourcePermissionsOk(perms []*models.Permission) middleware.Responder {
	return permissions.NewListResourcePermissionsOK().WithPayload(&models.PermissionList{perms})
}

func listResourcePermissionsInternalServerError(reason string) middleware.Responder {
	return permissions.NewListResourcePermissionsInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func BuildListResourcePermissionsHandler(
	db *sql.DB,
) func(permissions.ListResourcePermissionsParams) middleware.Responder {

	// Return the handler function.
	return func(params permissions.ListResourcePermissionsParams) middleware.Responder {
		resourceTypeName := params.ResourceType
		resourceName := params.ResourceName

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return listResourcePermissionsInternalServerError(err.Error())
		}

		// List the permissions for the resource.
		perms, err := permsdb.ListResourcePermissions(tx, resourceTypeName, resourceName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return listResourcePermissionsInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return listResourcePermissionsInternalServerError(err.Error())
		}

		// Return the results.
		return listResourcePermissionsOk(perms)
	}
}
