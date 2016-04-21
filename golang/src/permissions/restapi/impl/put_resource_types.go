package impl

import (
	"database/sql"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resource_types"
)

func BuildResourceTypesPutHandler(db *sql.DB) func(resource_types.PutResourceTypesParams) middleware.Responder {

	// Return the handler function.
	return func(params resource_types.PutResourceTypesParams) middleware.Responder {
		resourceTypeIn := params.ResourceTypeIn

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			reason := err.Error()
			return resource_types.NewPutResourceTypesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Check for a duplicate name.
		duplicate, err := permsdb.GetResourceTypeByName(tx, resourceTypeIn.Name)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPutResourceTypesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		if duplicate != nil {
			tx.Rollback()
			reason := fmt.Sprintf("a resource type named %s already exists", *resourceTypeIn.Name)
			return resource_types.NewPutResourceTypesBadRequest().WithPayload(&models.ErrorOut{&reason})
		}

		// Save the resource type.
		resourceTypeOut, err := permsdb.AddNewResourceType(tx, resourceTypeIn)
		if err != nil {
			tx.Rollback()
			reason := err.Error()
			return resource_types.NewPutResourceTypesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		tx.Commit()
		return resource_types.NewPutResourceTypesCreated().WithPayload(resourceTypeOut)
	}
}
