package resource_types

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resource_types"
)

func BuildResourceTypesIDPostHandler(db *sql.DB) func(resource_types.PostResourceTypesIDParams) middleware.Responder {

	// Return the handler function.
	return func(params resource_types.PostResourceTypesIDParams) middleware.Responder {
		resourceTypeIn := params.ResourceTypeIn

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			reason := err.Error()
			return resource_types.NewPostResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the resource type exists.
		exists, err := permsdb.ResourceTypeExists(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPostResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			tx.Rollback()
			reason := fmt.Sprintf("resource type %s not found", params.ID)
			return resource_types.NewPostResourceTypesIDNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Check for a duplicate name.
		duplicate, err := permsdb.GetDuplicateResourceTypeByName(tx, &params.ID, resourceTypeIn.Name)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPostResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if duplicate != nil {
			tx.Rollback()
			reason := fmt.Sprintf("another resource type named %s already exists", *resourceTypeIn.Name)
			return resource_types.NewPostResourceTypesIDBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Update the resource type.
		resourceTypeOut, err := permsdb.UpdateResourceType(tx, &params.ID, resourceTypeIn)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPostResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPostResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return resource_types.NewPostResourceTypesIDOK().WithPayload(resourceTypeOut)
	}
}
