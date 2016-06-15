package resources

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resources"
)

func BuildListResourcesHandler(db *sql.DB) func(resources.ListResourcesParams) middleware.Responder {

	// Return the handler function.
	return func(params resources.ListResourcesParams) middleware.Responder {

		// Start a transaction for this request.
		tx, err := db.Begin()
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewListResourcesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		defer tx.Commit()

		// List all resources.
		result, err := permsdb.ListResources(tx, params.ResourceTypeName, params.ResourceName)
		if err != nil {
			logcabin.Error.Print(err)
			reason := err.Error()
			return resources.NewListResourcesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}

		// Return the results.
		return resources.NewListResourcesOK().WithPayload(&models.ResourcesOut{Resources: result})
	}
}
