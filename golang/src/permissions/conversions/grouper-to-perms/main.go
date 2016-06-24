package main

import (
	"configurate"
	"database/sql"
	"flag"
	"fmt"
	"github.com/go-swagger/go-swagger/httpkit/middleware"
	"logcabin"
	"os"
	"permissions/models"
	perms_impl "permissions/restapi/impl/permissions"
	perms "permissions/restapi/operations/permissions"
	"strings"
	"version"

	_ "github.com/lib/pq"
)

type requestHandler func(perms.PutPermissionParams) middleware.Responder

func addPermission(handler requestHandler, resourceType, resourceName, subjectType, subjectID, level string) {
	params := perms.PutPermissionParams{
		ResourceType: resourceType,
		ResourceName: resourceName,
		SubjectType:  subjectType,
		SubjectID:    subjectID,
		Permission:   &models.PermissionPutRequest{PermissionLevel: models.PermissionLevel(level)},
	}
	responder := handler(params)

	// Handle the responder.
	switch responder.(type) {
	default:
		logcabin.Error.Fatalf("unexpected responder type: %T", responder)
	case *perms.PutPermissionOK:
		// Do nothing.
	case *perms.PutPermissionBadRequest:
		logcabin.Error.Fatal(*responder.(*perms.PutPermissionBadRequest).Payload.Reason)
	case *perms.PutPermissionInternalServerError:
		logcabin.Error.Fatal(*responder.(*perms.PutPermissionInternalServerError).Payload.Reason)
	}
}

func runConversion(handler requestHandler, grouperDb *sql.DB, permissionDefName string) {

	// Extract the resource type name from the permisison def name.
	components := strings.Split(permissionDefName, ":")
	resourceType := strings.Split(components[len(components)-1], "-")[0]

	// Query the database.
	query := `SELECT gadn.extension AS attribute_def_name,
	                 gr.id AS group_id,
	                 gm.subject_id,
	                 gaaa.name AS action_name
	          FROM grouper_attribute_def gad
	          JOIN grouper_attribute_def_name gadn ON gad.id = gadn.attribute_def_id
	          JOIN grouper_attribute_assign gaa ON gadn.id = gaa.attribute_def_name_id
	          JOIN grouper_attr_assign_action gaaa ON gaa.attribute_assign_action_id = gaaa.id
	          LEFT JOIN grouper_groups gr ON gaa.owner_group_id = gr.id
	          LEFT JOIN grouper_members gm ON gaa.owner_member_id = gm.id
	          WHERE gad.attribute_def_type = 'perm'
	          AND gad.name = $1`
	rows, err := grouperDb.Query(query, permissionDefName)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}
	defer rows.Close()

	// Process each row in the database.
	var resourceName, groupID, subjectID, permissionLevel *string
	for rows.Next() {
		if err := rows.Scan(&resourceName, &groupID, &subjectID, &permissionLevel); err != nil {
			logcabin.Error.Fatal(err.Error())
		}

		// Add the permission.
		if subjectID == nil {
			addPermission(handler, resourceType, *resourceName, "group", *groupID, *permissionLevel)
		} else {
			addPermission(handler, resourceType, *resourceName, "user", *subjectID, *permissionLevel)
		}
	}
}

func main() {
	config := flag.String("config", "", "The path to the configuration file.")
	convertJobs := flag.Bool("jobs", true, "True if jobs should be included in the conversion.")
	showVersion := flag.Bool("version", false, "Display the version information and exit.")

	// Parse the command line arguments.
	flag.Parse()

	// Display the version information and exit if we're told to.
	if *showVersion {
		version.AppVersion()
		os.Exit(0)
	}

	// Validate the command-line options.
	if *config == "" {
		logcabin.Error.Fatal("--config must be set")
	}

	// Load the configuration file.
	cfg, err := configurate.Init(*config)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Retrieve the permissions database URI.
	dburi, err := cfg.String("db.uri")
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Establish the permissions database session.
	db, err := sql.Open("postgres", dburi)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Retrieve the Grouper database URI.
	grouperDburi, err := cfg.String("grouperdb.uri")
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Establish the Grouper database session.
	grouperDb, err := sql.Open("postgres", grouperDburi)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Retrieve the Grouper folder name prefix.
	grouperFolderNamePrefix, err := cfg.String("grouperdb.folder_name_prefix")
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Create a handler to add permissions to the database.
	handler := perms_impl.BuildPutPermissionHandler(db)

	// Determine which conversions to run.
	var permissionDefs []string
	if *convertJobs {
		permissionDefs = []string{"apps:app-permission-def", "analyses:analysis-permission-def"}
	} else {
		permissionDefs = []string{"apps:app-permission-def"}
	}

	// Run the conversion.
	for _, permissionDef := range permissionDefs {
		permissionDefName := fmt.Sprintf("%s:%s", grouperFolderNamePrefix, permissionDef)
		runConversion(requestHandler(handler), grouperDb, permissionDefName)
	}
}
