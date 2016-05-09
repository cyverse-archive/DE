package resource_types

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resource_types"
)

func BuildResourceTypesIDDeleteHandler(
	db *sql.DB,
) func(resource_types.DeleteResourceTypesIDParams) middleware.Responder {

	// Return the handler function.
	return func(params resource_types.DeleteResourceTypesIDParams) middleware.Responder {

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			reason := err.Error()
			return resource_types.NewDeleteResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the resource type exists.
		exists, err := permsdb.ResourceTypeExists(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewDeleteResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if !exists {
			tx.Rollback()
			reason := fmt.Sprintf("resource type %s not found", params.ID)
			return resource_types.NewDeleteResourceTypesIDNotFound().WithPayload(&models.ErrorOut{&reason})
		}

		// Verify that the resource type has no resources associated with it.
		numResources, err := permsdb.CountResourcesOfType(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewDeleteResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if numResources != 0 {
			tx.Rollback()
			reason := fmt.Sprintf("resource type %s has resources associated with it", params.ID)
			return resource_types.NewDeleteResourceTypesIDBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Delete the resource type.
		err = permsdb.DeleteResourceType(tx, &params.ID)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewDeleteResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewDeleteResourceTypesIDInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		return resource_types.NewDeleteResourceTypesIDOK()
	}
}
