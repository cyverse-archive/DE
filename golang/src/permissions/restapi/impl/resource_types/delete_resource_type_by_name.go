package resource_types

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resource_types"
)

func deleteResourceTypeByNameOK() middleware.Responder {
	return resource_types.NewDeleteResourceTypeByNameOK()
}

func deleteResourceTypeByNameBadRequest(reason string) middleware.Responder {
	return resource_types.NewDeleteResourceTypeByNameBadRequest().WithPayload(&models.ErrorOut{&reason})
}

func deleteResourceTypeByNameInternalServerError(reason string) middleware.Responder {
	return resource_types.NewDeleteResourceTypeByNameInternalServerError().WithPayload(&models.ErrorOut{&reason})
}

func deleteResourceTypeByNameNotFound(reason string) middleware.Responder {
	return resource_types.NewDeleteResourceTypeByNameNotFound().WithPayload(&models.ErrorOut{&reason})
}

func BuildDeleteResourceTypeByNameHandler(
	db *sql.DB,
) func(resource_types.DeleteResourceTypeByNameParams) middleware.Responder {

	// Return the handler function.
	return func(params resource_types.DeleteResourceTypeByNameParams) middleware.Responder {

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			return deleteResourceTypeByNameInternalServerError(err.Error())
		}

		// Verify that the resource type exists.
		resourceType, err := permsdb.GetResourceTypeByName(tx, &params.ResourceTypeName)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceTypeByNameInternalServerError(err.Error())
		}
		if resourceType == nil {
			tx.Rollback()
			reason := fmt.Sprintf("resource type name not found: %s", params.ResourceTypeName)
			return deleteResourceTypeByNameNotFound(reason)
		}

		// Verify that the resource type has no resources associated with it.
		numResources, err := permsdb.CountResourcesOfType(tx, resourceType.ID)
		if err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceTypeByNameInternalServerError(err.Error())
		}
		if numResources != 0 {
			tx.Rollback()
			reason := fmt.Sprintf("resource type has resources associated with it: %s", params.ResourceTypeName)
			return deleteResourceTypeByNameBadRequest(reason)
		}

		// Delete the resource type.
		if err := permsdb.DeleteResourceType(tx, resourceType.ID); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceTypeByNameInternalServerError(err.Error())
		}

		// Commit the transaction.
		if err := tx.Commit(); err != nil {
			tx.Rollback()
			logcabin.Error.Print(err)
			return deleteResourceTypeByNameInternalServerError(err.Error())
		}

		return deleteResourceTypeByNameOK()
	}
}
