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

func BuildDeleteResourceHandler(db *sql.DB) func(resources.DeleteResourceParams) middleware.Responder {

	// Return the handler function.
	return func(params resources.DeleteResourceParams) middleware.Responder {

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewDeleteResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the resource exists.
		exists, err := permsdb.ResourceExists(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewDeleteResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			tx.Rollback()
			reason := fmt.Sprintf("resource, %s, not found", params.ID)
			return resources.NewDeleteResourceNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Delete the resource.
		err = permsdb.DeleteResource(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewDeleteResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewDeleteResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return resources.NewDeleteResourceOK()
	}
}
