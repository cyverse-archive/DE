# Permissions Service

This service manages permissions for the Discovery environment.

# Implementation Details

This service is generated using [go-swagger](https://github.com/go-swagger/go-swagger).

## Adding a New Endpoint

1. Update `swagger.yml`
1. Run `swagger generate server -A permissions -f swagger.yml`
1. Update `restapi/configure_permissions.go` to add the service implementation

# Example Endpoint Implementation

## swagger.yml

### definitions

``` yaml
  resource_type_out:
    type: object
    required:
      - id
      - name
    properties:
      id:
        type: string
        minLength: 36
        maxLength: 36
      name:
        type: string
        minLength: 1
      description:
        type: string
  resource_types_out:
    type: object
    required:
      - resource_types
    properties:
      resource_types:
        type: array
        items:
          $ref: "#/definitions/resource_type_out"
```

### paths

``` yaml
  /resource_types:
    get:
      tags:
        - resource_types
      responses:
        200:
          description: resource type listing
          schema:
            $ref: "#/definitions/resource_types_out"
        500:
          description: resource type listing
          schema:
            $ref: "#/definitions/error_out"
```

## Generating the Code

```
$ swagger generate server -A permissions -f swagger.yml
2016/04/18 13:52:14 building a plan for generation
2016/04/18 13:52:14 planning definitions
2016/04/18 13:52:15 planning operations
2016/04/18 13:52:15 grouping operations into packages
2016/04/18 13:52:15 planning meta data and facades
2016/04/18 13:52:15 rendering 4 models
2016/04/18 13:52:15 rendered model template: resource_type_out
2016/04/18 13:52:15 rendered model template: resource_types_out
2016/04/18 13:52:15 rendered model template: service_info
2016/04/18 13:52:15 rendered model template: error_out
2016/04/18 13:52:15 rendered handler template: resource_types.GetResourceTypes
2016/04/18 13:52:16 generated handler resource_types.GetResourceTypes
2016/04/18 13:52:16 rendered responses template: resource_types.GetResourceTypesResponses
2016/04/18 13:52:16 generated responses resource_types.GetResourceTypesResponses
2016/04/18 13:52:16 no parameters for operation resource_types.GetResourceTypes
2016/04/18 13:52:16 rendered handler template: status.Get
2016/04/18 13:52:16 generated handler status.Get
2016/04/18 13:52:16 rendered responses template: status.GetResponses
2016/04/18 13:52:16 generated responses status.GetResponses
2016/04/18 13:52:16 no parameters for operation status.Get
2016/04/18 13:52:16 rendered embedded Swagger JSON template: restapi.Permissions
2016/04/18 13:52:17 rendered builder template: operations.Permissions
2016/04/18 13:52:32 rendered server template: restapi.Server
2016/04/18 13:52:32 skipped (already exists) configure api template: operations.ConfigurePermissions
2016/04/18 13:52:32 rendered doc template: operations.Permissions
2016/04/18 13:52:32 rendered main template: server.Permissions
```

## Updates to restapi/configure_permissions.go

Update the imports to include the new operation:

``` go
import (
  // ...
  "permissions/restapi/operations/resource_types"
  // ...
)
```

Add the handler definition to `configureAPI`:

``` go
  api.ResourceTypesGetResourceTypesHandler =
    resource_types.GetResourceTypesHandlerFunc(impl.BuildResourceTypesGetHandler(db))
```

## Endpoint Implementation Files

`restapi/impl/get_resource_types.go`:

``` go
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
```

`restapi/impl/db/resource_types.go`:

``` go
package db

import (
  "database/sql"
  "permissions/models"
)

func ListResourceTypes(db *sql.DB) ([]*models.ResourceTypeOut, error) {

  // Query the database.
  query := "SELECT id, name, description FROM resource_types"
  rows, err := db.Query(query)
  if err != nil {
    return nil, err
  }
  defer rows.Close()

  // Build the list of resource types.
  var resourceTypes []*models.ResourceTypeOut
  for rows.Next() {
    var resourceType models.ResourceTypeOut
    if err := rows.Scan(&resourceType.ID, &resourceType.Name, &resourceType.Description); err != nil {
      return nil, err
    }
    resourceTypes = append(resourceTypes, &resourceType)
  }

  // Check for any uncaught errors.
  if err := rows.Err(); err != nil {
    return resourceTypes, err
  }

  return resourceTypes, nil
}
```
