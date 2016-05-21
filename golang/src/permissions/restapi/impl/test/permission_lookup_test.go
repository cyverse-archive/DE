package test

import (
	// "database/sql"
	"fmt"
	"permissions/clients/grouper"
	// "permissions/models"
	// "permissions/restapi/operations/permissions"
	"testing"
	// middleware "github.com/go-swagger/go-swagger/httpkit/middleware"
	// impl "permissions/restapi/impl/permissions"
)

var groups = []*grouper.GroupInfo{
	&grouper.GroupInfo{ID: "g1id", Name: "g1"},
	&grouper.GroupInfo{ID: "g2id", Name: "g2"},
	&grouper.GroupInfo{ID: "g3id", Name: "g3"},
}

var groupMemberships = map[string][]*grouper.GroupInfo{
	"s1": groups,
	"s2": groups[:1],
}

var mockGrouperClient = grouper.NewMockGrouperClient(groupMemberships)

func TestBySubject(t *testing.T) {
	if !shouldrun() {
		return
	}

	// Initialize the database.
	db := initdb(t)
	addDefaultResourceTypes(db, t)

	// TODO: implement the test.
}
