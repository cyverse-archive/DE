package test

import (
	"database/sql"
	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/clients/grouper"
	"permissions/models"
	impl "permissions/restapi/impl/permissions"
	"permissions/restapi/operations/permissions"
	"testing"
)

var groups = []*grouper.GroupInfo{
	&grouper.GroupInfo{ID: "g1id", Name: "g1"},
	&grouper.GroupInfo{ID: "g2id", Name: "g2"},
	&grouper.GroupInfo{ID: "g3id", Name: "g3"},
}

var groupMemberships = map[string][]*grouper.GroupInfo{
	"s1":   groups,
	"s2":   groups[:1],
	"g1id": groups[1:1],
}

var mockGrouperClient = grouper.NewMockGrouperClient(groupMemberships)

func bySubjectAttempt(db *sql.DB, subjectType, subjectId string, lookup bool) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildBySubjectHandler(db, grouper.Grouper(mockGrouperClient))

	// Attempt to look up the permissions.
	params := permissions.BySubjectParams{
		SubjectType: subjectType,
		SubjectID:   subjectId,
		Lookup:      &lookup,
	}
	return handler(params)
}

func bySubject(db *sql.DB, subjectType, subjectId string, lookup bool) *models.PermissionList {
	responder := bySubjectAttempt(db, subjectType, subjectId, lookup)
	return responder.(*permissions.BySubjectOK).Payload
}

func bySubjectAndResourceTypeAttempt(
	db *sql.DB, subjectType, subjectId, resourceType string, lookup bool,
) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildBySubjectAndResourceTypeHandler(db, grouper.Grouper(mockGrouperClient))

	// Attempt to look up the permissions.
	params := permissions.BySubjectAndResourceTypeParams{
		SubjectType:  subjectType,
		SubjectID:    subjectId,
		ResourceType: resourceType,
		Lookup:       &lookup,
	}
	return handler(params)
}

func bySubjectAndResourceType(
	db *sql.DB, subjectType, subjectId, resourceType string, lookup bool,
) *models.PermissionList {
	responder := bySubjectAndResourceTypeAttempt(db, subjectType, subjectId, resourceType, lookup)
	return responder.(*permissions.BySubjectAndResourceTypeOK).Payload
}

func bySubjectAndResourceAttempt(
	db *sql.DB, subjectType, subjectId, resourceType, resourceName string, lookup bool,
) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildBySubjectAndResourceHandler(db, grouper.Grouper(mockGrouperClient))

	// Attempt to look up the permissions.
	params := permissions.BySubjectAndResourceParams{
		SubjectType:  subjectType,
		SubjectID:    subjectId,
		ResourceType: resourceType,
		ResourceName: resourceName,
		Lookup:       &lookup,
	}
	return handler(params)
}

func bySubjectAndResource(
	db *sql.DB, subjectType, subjectId, resourceType, resourceName string, lookup bool,
) *models.PermissionList {
	responder := bySubjectAndResourceAttempt(db, subjectType, subjectId, resourceType, resourceName, lookup)
	return responder.(*permissions.BySubjectAndResourceOK).Payload
}

func checkPerm(t *testing.T, p *models.Permission, i int32, resource, subject, level string) {
	if *p.Resource.Name != resource {
		t.Errorf("unexpected resource in result %d: %s", i, *p.Resource.Name)
	}
	if string(p.Subject.SubjectID) != subject {
		t.Errorf("unexpected subject in result %d: %s", i, string(p.Subject.SubjectID))
	}
	if string(p.PermissionLevel) != level {
		t.Errorf("unexpected permission level in result %d: %s", i, string(p.PermissionLevel))
	}
}

func TestBySubject(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "r1", "own")
	putPermission(db, "group", "g1id", "analysis", "r2", "read")
	putPermission(db, "group", "g2id", "analysis", "r3", "read")

	// Look up the permissions and verify that we get the expected number of results.
	perms := bySubject(db, "user", "s2", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "r1", "s2", "own")
	checkPerm(t, perms[1], 1, "r2", "g1id", "read")
}

func TestBySubjectMultiplePermissions(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "r1", "own")
	putPermission(db, "group", "g1id", "app", "r1", "read")
	putPermission(db, "user", "s2", "analysis", "r2", "read")
	putPermission(db, "group", "g1id", "analysis", "r2", "write")

	// Look up the permissions and verify that we get the expected number of results.
	perms := bySubject(db, "user", "s2", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "r1", "s2", "own")
	checkPerm(t, perms[1], 1, "r2", "g1id", "write")
}

func TestBySubjectIncorrectSubjectType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "r1", "own")
	putPermission(db, "group", "g1id", "app", "r1", "read")
	putPermission(db, "user", "s2", "analysis", "r2", "read")
	putPermission(db, "group", "g1id", "analysis", "r2", "write")

	// Attempt the lookup.
	responder := bySubjectAttempt(db, "group", "s2", true)
	errorOut := responder.(*permissions.BySubjectBadRequest).Payload
	expected := "incorrect type for subject, s2: group"
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestBySubjectGroupsNotTransitive(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "r1", "own")
	putPermission(db, "group", "g1id", "app", "r1", "read")
	putPermission(db, "user", "s2", "analysis", "r2", "read")
	putPermission(db, "group", "g1id", "analysis", "r2", "write")
	putPermission(db, "group", "g2id", "analysis", "r3", "own")

	// Look up permissions and verify that we get the expected number of results.
	perms := bySubject(db, "group", "g1id", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "r1", "g1id", "read")
	checkPerm(t, perms[1], 1, "r2", "g1id", "write")
}

func TestBySubjectNonLookup(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "r1", "own")
	putPermission(db, "group", "g1id", "app", "r1", "read")
	putPermission(db, "user", "s2", "analysis", "r2", "read")
	putPermission(db, "group", "g1id", "analysis", "r2", "write")
	putPermission(db, "group", "g2id", "analysis", "r3", "own")

	// List permissions for s2 and verify that we get the expected results.
	perms := bySubject(db, "user", "s2", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "r1", "s2", "own")
	checkPerm(t, perms[1], 1, "r2", "s2", "read")

	// List permissions for g1id and verify that we get the expected results.
	perms = bySubject(db, "group", "g1id", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "r1", "g1id", "read")
	checkPerm(t, perms[1], 1, "r2", "g1id", "write")
}

func TestBySubjectAndResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")
	putPermission(db, "user", "s2", "analysis", "analysis1", "own")
	putPermission(db, "group", "g1id", "analysis", "analysis1", "read")
	putPermission(db, "user", "s2", "analysis", "analysis2", "read")
	putPermission(db, "group", "g1id", "analysis", "analysis2", "write")
	putPermission(db, "group", "g2id", "analysis", "analysis3", "own")

	// Look up app permissions and verify that we get the expected number of results.
	perms := bySubjectAndResourceType(db, "user", "s2", "app", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "s2", "own")
	checkPerm(t, perms[1], 1, "app2", "g1id", "write")

	// Look up analysis permissions and verify that we get the expected number of results.
	perms = bySubjectAndResourceType(db, "user", "s2", "analysis", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "analysis1", "s2", "own")
	checkPerm(t, perms[1], 1, "analysis2", "g1id", "write")
}

func TestBySubjectAndResourceTypeIncorrectSubjectType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")

	// Look up permissions and verify that we get the expected number of results.
	responder := bySubjectAndResourceTypeAttempt(db, "group", "s2", "app", true)
	errorOut := responder.(*permissions.BySubjectAndResourceTypeBadRequest).Payload
	expected := "incorrect type for subject, s2: group"
	if *errorOut.Reason != expected {
		t.Errorf("unexpected failure reason: %s", *errorOut.Reason)
	}
}

func TestBySubjectAndResourceTypeUnknownResourceType(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")

	// Look up permissions and verify that we get the expected number of results.
	perms := bySubjectAndResourceType(db, "user", "s2", "blargle", true).Permissions
	if len(perms) != 0 {
		t.Errorf("unexpected number of results: %d", len(perms))
	}
}

func TestBySubjectAndResourceTypeGroupsNotTransitive(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")

	// Look up permissions and verify that we get the expected number of results.
	perms := bySubjectAndResourceType(db, "group", "g1id", "app", true).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "g1id", "read")
	checkPerm(t, perms[1], 1, "app2", "g1id", "write")
}

func TestBySubjectAndResourceTypeNonLookup(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")
	putPermission(db, "user", "s2", "analysis", "analysis1", "own")
	putPermission(db, "group", "g1id", "analysis", "analysis1", "read")
	putPermission(db, "user", "s2", "analysis", "analysis2", "read")
	putPermission(db, "group", "g1id", "analysis", "analysis2", "write")
	putPermission(db, "group", "g2id", "analysis", "analysis3", "own")

	// Look up the app permissions for s2 and verify that we get the expected number of results.
	perms := bySubjectAndResourceType(db, "user", "s2", "app", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "s2", "own")
	checkPerm(t, perms[1], 1, "app2", "s2", "read")

	// Look up the app permissions for g1id and verify that we get the expected number of results.
	perms = bySubjectAndResourceType(db, "group", "g1id", "app", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "g1id", "read")
	checkPerm(t, perms[1], 1, "app2", "g1id", "write")

	// Look up the analysis permissions for s2 and verify that we got the expected number of results.
	perms = bySubjectAndResourceType(db, "user", "s2", "analysis", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "analysis1", "s2", "own")
	checkPerm(t, perms[1], 1, "analysis2", "s2", "read")

	// Look up the analysis permissions for g1id and verify that we get the expected number of results.
	perms = bySubjectAndResourceType(db, "group", "g1id", "analysis", false).Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we go the expected results.
	checkPerm(t, perms[0], 0, "analysis1", "g1id", "read")
	checkPerm(t, perms[1], 1, "analysis2", "g1id", "write")
}

func TestBySubjectAndResource(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")

	// Look up permissions for app1 and verify that we get the expected number of results.
	perms := bySubjectAndResource(db, "user", "s2", "app", "app1", true).Permissions
	if len(perms) != 1 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "s2", "own")

	// Look up permissions for app2 and verify that we get the expected number of results.
	perms = bySubjectAndResource(db, "user", "s2", "app", "app2", true).Permissions
	if len(perms) != 1 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app2", "g1id", "write")

	// Look up permissions for app3 and verify that we get the expected number of results.
	perms = bySubjectAndResource(db, "user", "s2", "app", "app3", true).Permissions
	if len(perms) != 0 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}
}

func TestBySubjectAndResourceNonLookup(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// Add some permissions.
	putPermission(db, "user", "s2", "app", "app1", "own")
	putPermission(db, "group", "g1id", "app", "app1", "read")
	putPermission(db, "user", "s2", "app", "app2", "read")
	putPermission(db, "group", "g1id", "app", "app2", "write")
	putPermission(db, "group", "g2id", "app", "app3", "own")

	// Look up permissions for app1 and verify that we get the expected number of results.
	perms := bySubjectAndResource(db, "user", "s2", "app", "app1", false).Permissions
	if len(perms) != 1 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "s2", "own")

	// Look up permissions for app2 and verify that we get the expected number of results.
	perms = bySubjectAndResource(db, "user", "s2", "app", "app2", false).Permissions
	if len(perms) != 1 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app2", "s2", "read")

	// Look up permissions for app3 and verify that we get the expected number of results.
	perms = bySubjectAndResource(db, "user", "s2", "app", "app3", true).Permissions
	if len(perms) != 0 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}
}
