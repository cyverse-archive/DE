package resources

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resources"
)

func deleteResourceByNameInternalServerError(reason string) middleware.Responder {
	return resources.NewDeleteResourceByNameInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func deleteResourceByNameOK() middleware.Responder {
	return resources.NewDeleteResourceByNameOK()
}

func deleteResourceByNameNotFound(reason string) middleware.Responder {
	return resources.NewDeleteResourceByNameNotFound().WithPayload(&models.ErrorOut{&reason})
}

func BuildDeleteResourceByNameHandler(db *sql.DB) func(resources.DeleteResourceByNameParams) middleware.Responder {

	// Return the handler function.
	return func(params resources.DeleteResourceByNameParams) middleware.Responder {

		// Start a transaction for the request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return deleteResourceByNameInternalServerError(err.Error())
		}

		// Look up the resource.
		resource, err := permsdb.GetResourceByNameAndType(tx, params.ResourceName, params.ResourceTypeName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceByNameInternalServerError(err.Error())
		}
		if resource == nil {
			tx.Rollback()
			reason := fmt.Sprintf("resource not found: %s:%s", params.ResourceTypeName, params.ResourceName)
			return deleteResourceByNameNotFound(reason)
		}

		// Delete the resource.
		if err := permsdb.DeleteResource(tx, resource.ID); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceByNameInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceByNameInternalServerError(err.Error())
		}

		return deleteResourceByNameOK()
	}
}
