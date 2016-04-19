package impl

import (
	"database/sql"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/models"
	permsdb "permissions/restapi/impl/db"
	"permissions/restapi/operations/resource_types"
)

func buildResponse(db *sql.DB) (*models.ResourceTypesOut, error) {

	// Get the list of resource types.
	resourceTypes, err := permsdb.ListResourceTypes(db)
	if err != nil {
		return nil, err
	}

	return &models.ResourceTypesOut{resourceTypes}, nil
}

func BuildResourceTypesGetHandler(db *sql.DB) func() middleware.Responder {

	// Return the handler function.
	return func() middleware.Responder {
		response, err := buildResponse(db)
		if err != nil {
			reason := err.Error()
			return resource_types.NewGetResourceTypesInternalServerError().WithPayload(&models.ErrorOut{&reason})
		}
		return resource_types.NewGetResourceTypesOK().WithPayload(response)
	}
}
