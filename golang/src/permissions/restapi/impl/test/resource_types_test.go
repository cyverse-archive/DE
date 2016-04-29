package test

import (
	"database/sql"
	"fmt"
	"permissions/models"
	"permissions/restapi/impl"
	"permissions/restapi/operations/resource_types"
	"testing"

	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
)

const FAKE_ID = "1A960034-969A-46A7-B6B5-3F1866258CAB"

func addResourceTypeAttempt(db *sql.DB, name, description string) middleware.Responder {

	// build the request handler.
	handler := impl.BuildResourceTypesPutHandler(db)

	// Attempt to add the resource type to the database.
	resourceTypeIn := &models.ResourceTypeIn{Name: &name, Description: description}
	params := resource_types.PutResourceTypesParams{ResourceTypeIn: resourceTypeIn}
	return handler(params)
}

func addResourceType(db *sql.DB, name string, description string) *models.ResourceTypeOut {
	responder := addResourceTypeAttempt(db, name, description)
	return responder.(*resource_types.PutResourceTypesCreated).Payload
}

func listResourceTypes(db *sql.DB) *models.ResourceTypesOut {

	// Build the request handler.
	handler := impl.BuildResourceTypesGetHandler(db)

	// Get the resource types from the database.
	responder := handler().(*resource_types.GetResourceTypesOK)

	return responder.Payload
}

func modifyResourceTypeAttempt(db *sql.DB, id, name, description string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildResourceTypesIDPostHandler(db)

	// Update the resource type in the database.
	resourceTypeIn := &models.ResourceTypeIn{Name: &name, Description: description}
	params := resource_types.PostResourceTypesIDParams{ID: id, ResourceTypeIn: resourceTypeIn}
	return handler(params)
}

func modifyResourceType(db *sql.DB, id string, name string, description string) *models.ResourceTypeOut {
	responder := modifyResourceTypeAttempt(db, id, name, description)
	return responder.(*resource_types.PostResourceTypesIDOK).Payload
}

func deleteResourceTypeAttempt(db *sql.DB, id string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildResourceTypesIDDeleteHandler(db)

	// Attempt to remove the resource type from the database.
	params := resource_types.DeleteResourceTypesIDParams{ID: id}
	return handler(params)
}

func deleteResourceType(db *sql.DB, id string) {
	responder := deleteResourceTypeAttempt(db, id)
	_ = responder.(*resource_types.DeleteResourceTypesIDOK)
}

func TestAddResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add the resource type.
	name := "resource_type"
	description := "The resource type."
	resourceTypeOut := addResourceType(db, name, description)

	// Verify that the name and description.
	if *resourceTypeOut.Name != name {
		t.Errorf("unexpected resource type name returned from database: %s", resourceTypeOut.Name)
	}
	if resourceTypeOut.Description != description {
		t.Errorf("unexpected resource type description returned from database: %s", resourceTypeOut.Description)
	}
}

func TestAddDuplicateResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add a resource type.
	name := "duplicate_resource_type"
	addResourceType(db, name, "The original!")

	// Attempt to add another resource type with the same name.
	responder := addResourceTypeAttempt(db, name, "The impostor!")
	errorOut := responder.(*resource_types.PutResourceTypesBadRequest).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("a resource type named %s already exists", name)
	if *errorOut.Reason != expected {
		t.Errorf("Unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestGetResourceTypes(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add a resource type.
	expected := addResourceType(db, "resource_type", "The resource type.")

	// List the resource types.
	resourceTypesOut := listResourceTypes(db)

	// Verify the number of resource types in the response.
	resourceTypes := resourceTypesOut.ResourceTypes
	if len(resourceTypes) != 1 {
		t.Fatalf("unexpected number of resource types listed: %d", len(resourceTypes))
	}

	// Verify the resource type values.
	actual := resourceTypes[0]
	if *actual.ID != *expected.ID {
		t.Errorf("unexpected resource type ID: %s", *actual.ID)
	}
	if *actual.Name != *expected.Name {
		t.Errorf("unexpected resource type name: %s", *actual.Name)
	}
	if actual.Description != expected.Description {
		t.Errorf("unexpected resource type description: %s", actual.Description)
	}
}

func TestModifyResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Add a resource type.
	original := addResourceType(db, "resource_type", "The resource type.")
	id := *original.ID

	// Modify the resource type.
	newName := "new_resource_type"
	newDescription := "New and Improved!"
	modified := modifyResourceType(db, id, newName, newDescription)

	// The modified resource type should have the original ID with the new name and description.
	if *modified.ID != id {
		t.Errorf("unexpected resource type ID: %s", *modified.ID)
	}
	if *modified.Name != newName {
		t.Errorf("unexpected resource type name: %s", *modified.Name)
	}
	if modified.Description != newDescription {
		t.Errorf("unexpected resource type description: %s", modified.Description)
	}

	// List the resource types.
	resourceTypesOut := listResourceTypes(db)

	// Verify the number of resource types in the response.
	resourceTypes := resourceTypesOut.ResourceTypes
	if len(resourceTypes) != 1 {
		t.Fatalf("unexpected number of resource types listed: %d", len(resourceTypes))
		return
	}

	// Verify the resource type values.
	listed := resourceTypes[0]
	if *listed.ID != id {
		t.Errorf("unexpected resource type ID listed: %s", *listed.ID)
	}
	if *listed.Name != newName {
		t.Errorf("unexpected resource type name listed: %s", *listed.Name)
	}
	if listed.Description != newDescription {
		t.Errorf("unexpected resource type description listed: %s", listed.Description)
	}
}

func TestModifyNonExistentResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Attempt to modify a non-existent resource type.
	responder := modifyResourceTypeAttempt(db, FAKE_ID, "n", "d")
	errorOut := responder.(*resource_types.PostResourceTypesIDNotFound).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("resource type %s not found", FAKE_ID)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestModifyDuplicateResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Create two new resource types.
	rt1 := addResourceType(db, "rt1", "rt1")
	rt2 := addResourceType(db, "rt2", "rt2")

	// Attempt to rename the second resource type to the name of the first resource type.
	responder := modifyResourceTypeAttempt(db, *rt2.ID, *rt1.Name, rt2.Description)
	errorOut := responder.(*resource_types.PostResourceTypesIDBadRequest).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("another resource type named %s already exists", *rt1.Name)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestDeleteResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Create two resource types.
	rt1 := addResourceType(db, "rt1", "rt1")
	rt2 := addResourceType(db, "rt2", "rt2")

	// Delete the second resource type.
	deleteResourceType(db, *rt2.ID)

	// List the resource types.
	resourceTypesOut := listResourceTypes(db)

	// Verify the number of resource types in the response.
	resourceTypes := resourceTypesOut.ResourceTypes
	if len(resourceTypes) != 1 {
		t.Fatalf("unexpected number of resource types listed: %d", len(resourceTypes))
		return
	}

	// Verify the resource type values.
	listed := resourceTypes[0]
	if *listed.ID != *rt1.ID {
		t.Errorf("unexpected resource type ID listed: %s", *listed.ID)
	}
	if *listed.Name != *rt1.Name {
		t.Errorf("unexpected resource type name listed: %s", *listed.Name)
	}
	if listed.Description != rt1.Description {
		t.Errorf("unexpected resource type description listed: %s", listed.Description)
	}
}

func TestDeleteNonExistentResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)

	// Attempt to delete a non-existent resource type.
	responder := deleteResourceTypeAttempt(db, FAKE_ID)
	errorOut := responder.(*resource_types.DeleteResourceTypesIDNotFound).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("resource type %s not found", FAKE_ID)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

// TODO: add another test for an attempt to delete a resource type that is associated with a resource
