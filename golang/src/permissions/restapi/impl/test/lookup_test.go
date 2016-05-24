package test

import (
	"database/sql"
	middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	"permissions/clients/grouper"
	"permissions/models"
	impl "permissions/restapi/impl/permission_lookup"
	lookup "permissions/restapi/operations/permission_lookup"
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

func lookupBySubjectAttempt(db *sql.DB, subjectType, subjectId string) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildBySubjectHandler(db, grouper.Grouper(mockGrouperClient))

	// Attempt to look up the permissions.
	params := lookup.BySubjectParams{SubjectType: subjectType, SubjectID: subjectId}
	return handler(params)
}

func lookupBySubject(db *sql.DB, subjectType, subjectId string) *models.PermissionList {
	responder := lookupBySubjectAttempt(db, subjectType, subjectId)
	return responder.(*lookup.BySubjectOK).Payload
}

func lookupBySubjectAndResourceTypeAttempt(
	db *sql.DB, subjectType, subjectId, resourceType string,
) middleware.Responder {

	// Build the request handler.
	handler := impl.BuildBySubjectAndResourceTypeHandler(db, grouper.Grouper(mockGrouperClient))

	// Attempt to look up the permissions.
	params := lookup.BySubjectAndResourceTypeParams{
		SubjectType:  subjectType,
		SubjectID:    subjectId,
		ResourceType: resourceType,
	}
	return handler(params)
}

func lookupBySubjectAndResourceType(db *sql.DB, subjectType, subjectId, resourceType string) *models.PermissionList {
	responder := lookupBySubjectAndResourceTypeAttempt(db, subjectType, subjectId, resourceType)
	return responder.(*lookup.BySubjectAndResourceTypeOK).Payload
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
	perms := lookupBySubject(db, "user", "s2").Permissions
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
	perms := lookupBySubject(db, "user", "s2").Permissions
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
	responder := lookupBySubjectAttempt(db, "group", "s2")
	errorOut := responder.(*lookup.BySubjectBadRequest).Payload
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
	perms := lookupBySubject(db, "group", "g1id").Permissions
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
	perms := lookupBySubjectAndResourceType(db, "user", "s2", "app").Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "s2", "own")
	checkPerm(t, perms[1], 1, "app2", "g1id", "write")

	// Look up analysis permissions and verify that we get the expected number of results.
	perms = lookupBySubjectAndResourceType(db, "user", "s2", "analysis").Permissions
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
	responder := lookupBySubjectAndResourceTypeAttempt(db, "group", "s2", "app")
	errorOut := responder.(*lookup.BySubjectAndResourceTypeBadRequest).Payload
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
	perms := lookupBySubjectAndResourceType(db, "user", "s2", "blargle").Permissions
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
	perms := lookupBySubjectAndResourceType(db, "group", "g1id", "app").Permissions
	if len(perms) != 2 {
		t.Fatalf("unexpected number of results: %d", len(perms))
	}

	// Verify that we got the expected results.
	checkPerm(t, perms[0], 0, "app1", "g1id", "read")
	checkPerm(t, perms[1], 1, "app2", "g1id", "write")
}
