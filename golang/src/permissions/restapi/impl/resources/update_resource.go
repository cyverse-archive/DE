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

func BuildUpdateResourceHandler(db *sql.DB) func(resources.UpdateResourceParams) middleware.Responder {

	// Return the handler function.
	return func(params resources.UpdateResourceParams) middleware.Responder {
		resourceUpdate := params.ResourceUpdate

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewUpdateResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the resource exists.
		exists, err := permsdb.ResourceExists(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewUpdateResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			tx.Rollback()
			reason := fmt.Sprintf("resource, %s, not found", params.ID)
			return resources.NewUpdateResourceNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that another resource with the same name doesn't already exist.
		duplicate, err := permsdb.GetDuplicateResourceByName(tx, &params.ID, resourceUpdate.Name)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewUpdateResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if duplicate != nil {
			tx.Rollback()
			reason := fmt.Sprintf("a resource of the same type named, '%s', already exists", *resourceUpdate.Name)
			return resources.NewUpdateResourceBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Update the resource.
		resourceOut, err := permsdb.UpdateResource(tx, &params.ID, resourceUpdate.Name)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewUpdateResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewUpdateResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return resources.NewUpdateResourceOK().WithPayload(resourceOut)
	}
}
