package test

import (
	"database/sql"
	"fmt"
	"permissions/models"
	"permissions/restapi/operations/resources"
	"testing"

	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	permsdb "permissions/restapi/impl/db"
	impl "permissions/restapi/impl/resources"
)

func listResourcesDirectly(db *sql.DB, t *testing.T) []*models.ResourceOut {

	// Start a transaction.
	tx, err := db.Begin()
	if err != nil {
		t.Fatalf("unable to list resources: %s", err)
	}
	defer tx.Rollback()

	// List the resources.
	resources, err := permsdb.ListResources(tx, nil, nil)
	if err != nil {
		t.Fatalf("unable to list resources: %s", err)
	}

	return resources
}

func addResourceAttempt(db *sql.DB, name, resourceType string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildAddResourceHandler(db)

	// Attempt to add the resource to the database.
	resourceIn := &models.ResourceIn{Name: &name, ResourceType: &resourceType}
	params := resources.AddResourceParams{ResourceIn: resourceIn}
	return handler(params)
}

func addResource(db *sql.DB, name, resourceType string) *models.ResourceOut {
	responder := addResourceAttempt(db, name, resourceType)
	return responder.(*resources.AddResourceCreated).Payload
}

func listResourcesAttempt(db *sql.DB, resourceType, name *string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildListResourcesHandler(db)

	// Attempt to list the resources.
	params := resources.ListResourcesParams{ResourceTypeName: resourceType, ResourceName: name}
	return handler(params)
}

func listResources(db *sql.DB, resourceType, name *string) *models.ResourcesOut {
	responder := listResourcesAttempt(db, resourceType, name)
	return responder.(*resources.ListResourcesOK).Payload
}

func updateResourceAttempt(db *sql.DB, id, name string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildUpdateResourceHandler(db)

	// Attempt to update the resource.
	resourceUpdate := &models.ResourceUpdate{Name: &name}
	params := resources.UpdateResourceParams{ID: id, ResourceUpdate: resourceUpdate}
	return handler(params)
}

func updateResource(db *sql.DB, id, name string) *models.ResourceOut {
	responder := updateResourceAttempt(db, id, name)
	return responder.(*resources.UpdateResourceOK).Payload
}

func deleteResourceAttempt(db *sql.DB, id string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildDeleteResourceHandler(db)

	// Attempt to delete the resource.
	params := resources.DeleteResourceParams{ID: id}
	return handler(params)
}

func deleteResource(db *sql.DB, id string) {
	responder := deleteResourceAttempt(db, id)
	_ = responder.(*resources.DeleteResourceOK)
}

func TestAddResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add a resource.
	resourceName := "resource"
	resourceType := "app"
	resource := addResource(db, resourceName, resourceType)

	// Verify the name and description.
	if *resource.Name != resourceName {
		t.Errorf("unexpected resource name: %s", *resource.Name)
	}
	if *resource.ResourceType != resourceType {
		t.Errorf("unexpected resource type: %s", *resource.ResourceType)
	}

	// List the resources and verify that we got the expected number of results.
	resources := listResourcesDirectly(db, t)
	if len(resources) != 1 {
		t.Fatalf("unexpected number of resource types: %d", len(resources))
	}

	// Verify that we got the expected result.
	resource = resources[0]
	if *resource.Name != resourceName {
		t.Errorf("unexpected resource name listed: %s", *resource.Name)
	}
	if *resource.ResourceType != resourceType {
		t.Errorf("unexpected resource type listed: %s", *resource.ResourceType)
	}
}

func TestAddDuplicateResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)
	addResource(db, "resource", "app")

	// Attempt to add a duplicate resource.
	resourceName := "resource"
	resourceType := "app"
	responder := addResourceAttempt(db, resourceName, resourceType)
	errorOut := responder.(*resources.AddResourceBadRequest).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("a resource named, '%s', with type, '%s', already exists", resourceName, resourceType)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestAddResourceInvalidType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Attempt to add a resource with an invalid type.
	resourceName := "resource"
	resourceType := "invisible_resource_type"
	responder := addResourceAttempt(db, resourceName, resourceType)
	errorOut := responder.(*resources.AddResourceBadRequest).Payload

	// Verify that we got the expected error message.
	expected := fmt.Sprintf("no resource type named, '%s', found", resourceType)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestListResources(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add a resource to the database.
	r1 := addResource(db, "r1", "app")

	// List the resources and verify we get the expected number of results.
	result := listResources(db, nil, nil)
	if len(result.Resources) != 1 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}

	// Verify that we got the expected result.
	resource := result.Resources[0]
	if *resource.Name != *r1.Name {
		t.Errorf("unexpected resource name: %s", *resource.Name)
	}
	if *resource.ResourceType != *r1.ResourceType {
		t.Errorf("unexpected resource type: %s", *resource.ResourceType)
	}
}

func TestListResourcesByName(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some resources to the database.
	r1 := addResource(db, "r1", "app")
	addResource(db, "r2", "app")

	// List the resources and verify we get the expected number of results.
	result := listResources(db, nil, r1.Name)
	if len(result.Resources) != 1 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}

	// Verify that we got the expected result.
	resource := result.Resources[0]
	if *resource.Name != *r1.Name {
		t.Errorf("unexpected resource name: %s", *resource.Name)
	}
	if *resource.ResourceType != *r1.ResourceType {
		t.Errorf("unexpected resource type: %s", *resource.ResourceType)
	}
}

func TestListResourcesByType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some resources to the database.
	r1 := addResource(db, "r1", "app")
	addResource(db, "r1", "analysis")

	// List the resources and verify we get the expected number of results.
	result := listResources(db, r1.ResourceType, nil)
	if len(result.Resources) != 1 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}

	// Verify that we got the expected result.
	resource := result.Resources[0]
	if *resource.Name != *r1.Name {
		t.Errorf("unexpected resource name: %s", *resource.Name)
	}
	if *resource.ResourceType != *r1.ResourceType {
		t.Errorf("unexpected resource type: %s", *resource.ResourceType)
	}
}

func TestListResourcesByNameAndType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some resources to the database.
	r1 := addResource(db, "r1", "app")
	addResource(db, "r2", "app")
	addResource(db, "r1", "analysis")
	addResource(db, "r2", "analysis")

	// List the resources and verify we get the expected number of results.
	result := listResources(db, r1.ResourceType, r1.Name)
	if len(result.Resources) != 1 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}

	// Verify that we got the expected result.
	resource := result.Resources[0]
	if *resource.Name != *r1.Name {
		t.Errorf("unexpected resource name: %s", *resource.Name)
	}
	if *resource.ResourceType != *r1.ResourceType {
		t.Errorf("unexpected resource type: %s", *resource.ResourceType)
	}
}

func TestListResourcesEmpty(t *testing.T) {
	if !shouldrun() {
		return
	}

	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add a resource to the database.
	result := listResources(db, nil, nil)
	if result.Resources == nil {
		t.Errorf("recieved a nil resource list")
	}
}

func TestUpdateResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add a resource to the database.
	r2 := addResource(db, "r2", "app")

	// Update the resource and verify the result,
	d2 := updateResource(db, *r2.ID, "d2")
	if *d2.Name != "d2" {
		t.Errorf("unexpected resource name: %s", *d2.Name)
	}
	if *d2.ResourceType != "app" {
		t.Errorf("unexpected resource type: %s", *d2.ResourceType)
	}

	// List the resources and verify that we get the expected number of results.
	result := listResources(db, nil, nil)
	if len(result.Resources) != 1 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}

	// Verify that we got the expected result.
	resource := result.Resources[0]
	if *resource.Name != *d2.Name {
		t.Errorf("unexpected resource name listed: %s", *resource.Name)
	}
	if *resource.ResourceType != *d2.ResourceType {
		t.Errorf("unexpected resource type listed: %s", *resource.ResourceType)
	}
}

func TestUpdateNonExistentResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Attempt to update a non-existent resource.
	responder := updateResourceAttempt(db, FAKE_ID, "foo")

	// Verify that we got the expected result.
	errorOut := responder.(*resources.UpdateResourceNotFound).Payload
	expected := fmt.Sprintf("resource, %s, not found", FAKE_ID)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure message: %s", *errorOut.Reason)
	}
}

func TestUpdateResourceDuplicateName(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add two resources to the database.
	r1 := addResource(db, "r1", "app")
	r2 := addResource(db, "r2", "app")

	// Attempt to give the second resource the first one's name.
	responder := updateResourceAttempt(db, *r2.ID, *r1.Name)

	// Verify that we got the expected result.
	errorOut := responder.(*resources.UpdateResourceBadRequest).Payload
	expected := fmt.Sprintf("a resource of the same type named, '%s', already exists", *r1.Name)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure message: %s", *errorOut.Reason)
	}
}

func TestDeleteResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add a resource to the database then delete it.
	r1 := addResource(db, "r1", "app")
	deleteResource(db, *r1.ID)

	// Verify that the resource was deleted.
	result := listResources(db, nil, nil)
	if len(result.Resources) != 0 {
		t.Fatalf("unexpected number of resources listed: %d", len(result.Resources))
	}
}

func TestDeleteNonExistentResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Attempt to delete a non-existent resource.
	responder := deleteResourceAttempt(db, FAKE_ID)

	// Verify that we got the expected result.
	errorOut := responder.(*resources.DeleteResourceNotFound).Payload
	expected := fmt.Sprintf("resource, %s, not found", FAKE_ID)
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure message: %s", *errorOut.Reason)
	}
}
