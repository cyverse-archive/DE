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

func BuildAddResourceHandler(db *sql.DB) func(resources.AddResourceParams) middleware.Responder {

	// Return the handler function.
	return func(params resources.AddResourceParams) middleware.Responder {
		resourceIn := params.ResourceIn

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewAddResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Load the resource type.
		resourceType, err := permsdb.GetResourceTypeByName(tx, resourceIn.ResourceType)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewAddResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if resourceType == nil {
			reason := fmt.Sprintf("no resource type named, '%s', found", *resourceIn.ResourceType)
			return resources.NewAddResourceBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that another resource with the same name doesn't already exist.
		duplicate, err := permsdb.GetResourceByName(tx, resourceIn.Name, resourceType.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewAddResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if duplicate != nil {
			tx.Rollback()
			reason := fmt.Sprintf(
				"a resource named, '%s', with type, '%s', already exists", *resourceIn.Name, *resourceType.Name,
			)
			return resources.NewAddResourceBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Add the resource to the database.
		resourceOut, err := permsdb.AddResource(tx, resourceIn.Name, resourceType.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewAddResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewAddResourceInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return resources.NewAddResourceCreated().WithPayload(resourceOut)
	}
}
