package impl

import (
	"database/sql"
	"fmt"
	"permissions/restapi/operations/resource_types"
)

func BuildResourceTypesGetHandler(db *sql.DB) func() middleware.Responder {

	// Return the handler function.
	return func() middleware.Responder {
		return resource_types.NewGetResourceTypes().WithPayload(info)
	}
}
