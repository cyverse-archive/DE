package permissions

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/permissions"
)

func BuildListPermissionsHandler(db *sql.DB) func() middleware.Responder {

	// Return the handler function.
	return func() middleware.Responder {

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return permissions.NewListPermissionsInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		defer tx.Commit()

		// List all permissions.
		result, err := permsdb.ListPermissions(tx)
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return permissions.NewListPermissionsInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Return the results.
		return permissions.NewListPermissionsOK().WithPayload(&models.PermissionList{Permissions: result})
	}
}
