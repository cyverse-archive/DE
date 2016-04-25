package impl

import (
	"permissions/models"
	"permissions/restapi/operations/resource_types"
	"testing"
)

func TestAddResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Build the request handler.
	handler := BuildResourceTypesPutHandler(db)

	// Define the incoming resource type
	name := "resource_type"
	description := "The resource type."
	resourceTypeIn := &models.ResourceTypeIn{Name: &name, Description: description}

	// Add a resource type to the database.
	params := resource_types.PutResourceTypesParams{ResourceTypeIn: resourceTypeIn}
	responder := handler(params).(*resource_types.PutResourceTypesCreated)
	resourceTypeOut := responder.Payload

	// Verify that the name and description.
	if *resourceTypeOut.Name != name {
		t.Errorf("unexpected resource type name returned from database: %s", resourceTypeOut.Name)
	}
	if resourceTypeOut.Description != description {
		t.Errorf("unexpected resource type description returned from database: %s", resourceTypeOut.Description)
	}
}
