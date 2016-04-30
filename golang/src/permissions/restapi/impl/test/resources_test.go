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

func addDefaultResourceType(tx *sql.Tx, name, description string, t *testing.T) {
	rt := &models.ResourceTypeIn{Name: &name, Description: description}
	if _, err := permsdb.AddNewResourceType(tx, rt); err != nil {
		t.Fatalf("unable to add default resource types: %s", err)
	}
}

func addDefaultResourceTypes(db *sql.DB, t *testing.T) {

	// Start a transaction.
	tx, err := db.Begin()
	if err != nil {
		t.Fatalf("unable to add default resource types: %s", err)
	}

	// Add the default resource types.
	addDefaultResourceType(tx, "app", "app", t)
	addDefaultResourceType(tx, "analysis", "analysis", t)

	// Commit the transaction.
	if err := tx.Commit(); err != nil {
		tx.Rollback()
		t.Fatalf("unable to add default resource types: %s", err)
	}
}

func listResourcesDirectly(db *sql.DB, t *testing.T) []*models.ResourceOut {

	// Start a transaction.
	tx, err := db.Begin()
	if err != nil {
		t.Fatalf("unable to list resources: %s", err)
	}
	defer tx.Rollback()

	// List the resources.
	resources, err := permsdb.ListResources(tx)
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

func listResourcesAttempt(db *sql.DB) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildListResourcesHandler(db)

	// Attempt to list the resources.
	return handler()
}

func listResources(db *sql.DB) *models.ResourcesOut {
	responder := listResourcesAttempt(db)
	return responder.(*resources.ListResourcesOK).Payload
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
	result := listResources(db)
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
