package test

import (
	"database/sql"
	"permissions/models"
	"permissions/restapi/operations/permissions"
	"testing"

	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	impl "permissions/restapi/impl/permissions"
)

func grantPermissionAttempt(
	db *sql.DB,
	subject *models.SubjectIn,
	resource *models.ResourceIn,
	level models.PermissionLevel,
) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildGrantPermissionHandler(db)

	// Attempt to add the permission.
	req := &models.PermissionGrantRequest{Subject: subject, Resource: resource, PermissionLevel: level}
	params := permissions.GrantPermissionParams{PermissionGrantRequest: req}
	return handler(params)
}

func grantPermission(
	db *sql.DB,
	subject *models.SubjectIn,
	resource *models.ResourceIn,
	level models.PermissionLevel,
) *models.Permission {
	responder := grantPermissionAttempt(db, subject, resource, level)
	return responder.(*permissions.GrantPermissionOK).Payload
}

func listPermissionsAttempt(db *sql.DB) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildListPermissionsHandler(db)

	// Attempt to list the permissions.
	return handler()
}

func listPermissions(db *sql.DB) *models.PermissionList {
	responder := listPermissionsAttempt(db)
	return responder.(*permissions.ListPermissionsOK).Payload
}

func TestGrantPermission(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Define a subject.
	subjectIn := newSubjectIn("s1", "user")
	subjectOut := addSubject(db, subjectIn.SubjectID, subjectIn.SubjectType)

	// Define a resource.
	resourceIn := newResourceIn("r1", "app")
	resourceOut := addResource(db, *resourceIn.Name, *resourceIn.ResourceType)

	// Grant the subject access to the resource.
	permission := grantPermission(db, subjectIn, resourceIn, "own")

	// Verify that we got the expected result.
	if permission.Subject.ID != subjectOut.ID {
		t.Errorf("unexpected internal subject ID returned: %s", permission.Subject.ID)
	}
	if permission.Subject.SubjectID != subjectOut.SubjectID {
		t.Errorf("unexpected external subject ID returned: %s", permission.Subject.SubjectID)
	}
	if permission.Subject.SubjectType != subjectOut.SubjectType {
		t.Errorf("unexpedted subject type returned: %s", permission.Subject.SubjectType)
	}
	if *permission.Resource.ID != *resourceOut.ID {
		t.Errorf("unexpected resource ID returned: %s", *permission.Resource.ID)
	}
	if *permission.Resource.Name != *resourceOut.Name {
		t.Errorf("unexpected resource name returned: %s", *permission.Resource.Name)
	}
	if *permission.Resource.ResourceType != *resourceOut.ResourceType {
		t.Errorf("unexpected resource type returned: %s", *permission.Resource.ResourceType)
	}
	if permission.PermissionLevel != models.PermissionLevel("own") {
		t.Errorf("unexpected permission level returned: %v", permission.PermissionLevel)
	}
}
