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
```

## Generating the Code

```
$ swagger generate server -A permissions -f swagger.yml
2016/04/15 17:38:38 building a plan for generation
2016/04/15 17:38:38 planning definitions
2016/04/15 17:38:39 planning operations
2016/04/15 17:38:39 grouping operations into packages
2016/04/15 17:38:39 planning meta data and facades
2016/04/15 17:38:39 rendering 3 models
2016/04/15 17:38:39 rendered model template: resource_type_out
2016/04/15 17:38:39 rendered model template: resource_types_out
2016/04/15 17:38:39 rendered model template: service_info
2016/04/15 17:38:39 rendered handler template: resource_types.GetResourceTypes
2016/04/15 17:38:40 generated handler resource_types.GetResourceTypes
2016/04/15 17:38:40 rendered responses template: resource_types.GetResourceTypesResponses
2016/04/15 17:38:40 generated responses resource_types.GetResourceTypesResponses
2016/04/15 17:38:40 no parameters for operation resource_types.GetResourceTypes
2016/04/15 17:38:40 rendered handler template: status.Get
2016/04/15 17:38:40 generated handler status.Get
2016/04/15 17:38:40 rendered responses template: status.GetResponses
2016/04/15 17:38:40 generated responses status.GetResponses
2016/04/15 17:38:40 no parameters for operation status.Get
2016/04/15 17:38:40 rendered embedded Swagger JSON template: restapi.Permissions
2016/04/15 17:38:41 rendered builder template: operations.Permissions
2016/04/15 17:39:01 rendered server template: restapi.Server
2016/04/15 17:39:01 skipped (already exists) configure api template: operations.ConfigurePermissions
2016/04/15 17:39:01 rendered doc template: operations.Permissions
2016/04/15 17:39:01 rendered main template: server.Permissions
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
  api.ResourceTypesGetResourceTypesHandler = resourceTypes.GetHandlerFunc(impl.BuildResourceTypesGetHandler(db))
```

## Endpoint Implementation File
