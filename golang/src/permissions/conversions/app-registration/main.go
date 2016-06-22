package main

import (
	"configurate"
	"database/sql"
	"flag"
	"fmt"
	"logcabin"
	"net/url"

	_ "github.com/lib/pq"
)

func determineDEDatabaseURI(dedburi, dburi, dedbname string) (string, error) {
	if dedburi != "" {
		return dedburi, nil
	}

	// Parse the permissions database URI.
	permsdburi, err := url.Parse(dburi)
	if err != nil {
		return "", err
	}

	// Create a new URI based on the permissions database URI.
	uri := &url.URL{
		Scheme:   permsdburi.Scheme,
		Opaque:   permsdburi.Opaque,
		User:     permsdburi.User,
		Host:     permsdburi.Host,
		Path:     fmt.Sprintf("/%s", dedbname),
		RawPath:  permsdburi.RawPath,
		RawQuery: permsdburi.RawQuery,
		Fragment: permsdburi.Fragment,
	}
	return uri.String(), nil
}

func cleanUpPerms(db *sql.DB) error {
	if _, err := db.Exec("DELETE FROM subjects"); err != nil {
		return err
	}
	if _, err := db.Exec("DELETE FROM resources"); err != nil {
		return err
	}
	return nil
}

func listApps(deDb *sql.DB) (*sql.Rows, error) {
	query := `SELECT
	              a.id,
	              a.is_public,
	              (SELECT DISTINCT regexp_replace(u.username, '@.*', '')
	               FROM app_category_app aca
	               JOIN app_categories ac ON aca.app_category_id = ac.id
	               JOIN workspace w ON ac.workspace_id = w.id
	               JOIN users u ON w.user_id = u.id
	               WHERE a.id = aca.app_id
	               AND ac.name = 'Apps under development'
	               AND NOT w.is_public) AS username
	          FROM app_listing a`
	return deDb.Query(query)
}

func defineDeUsersGroup(db *sql.DB, groupID string) error {
	stmt := "INSERT INTO subjects (subject_ID, subject_type) VALUES ($1, 'group')"
	if _, err := db.Exec(stmt, groupID); err != nil {
		return err
	}
	return nil
}

func getSubjectID(db *sql.DB, subjectType, externalSubjectID string) (*string, error) {
	query := "SELECT id FROM subjects WHERE subject_type = $1 AND subject_id = $2"
	rows, err := db.Query(query, subjectType, externalSubjectID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Quit now if there are no matching rows.
	if !rows.Next() {
		return nil, nil
	}

	// Extract the subject ID from the first row.
	var subjectID *string
	if err := rows.Scan(&subjectID); err != nil {
		return nil, err
	}
	return subjectID, nil
}

func addSubject(db *sql.DB, subjectType, externalSubjectID string) (*string, error) {
	query := "INSERT INTO subjects (subject_type, subject_id) VALUES ($1, $2) RETURNING id"
	row := db.QueryRow(query, subjectType, externalSubjectID)

	// Extract the subject ID.
	var subjectID *string
	if err := row.Scan(&subjectID); err != nil {
		logcabin.Error.Print(err)
		return nil, err
	}
	return subjectID, nil
}

func lookUpSubjectID(db *sql.DB, subjectType, externalSubjectID string) (*string, error) {
	subjectID, err := getSubjectID(db, subjectType, externalSubjectID)
	if err != nil {
		return nil, err
	}
	if subjectID != nil {
		return subjectID, nil
	}
	return addSubject(db, subjectType, externalSubjectID)
}

func getResourceID(db *sql.DB, appID string) (*string, error) {
	query := `SELECT id FROM resources
            WHERE resource_type_id = (SELECT id FROM resource_types WHERE name = 'app')
            AND name = $1`
	rows, err := db.Query(query, appID)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	// Quit now if there are no matching rows.
	if !rows.Next() {
		return nil, nil
	}

	// Extract the resource ID from the first row.
	var resourceID *string
	if err := rows.Scan(&resourceID); err != nil {
		return nil, err
	}
	return resourceID, nil
}

func addResource(db *sql.DB, appID string) (*string, error) {
	query := `INSERT INTO resources (name, resource_type_id)
            (SELECT $1, id FROM resource_types WHERE name = 'app')
            RETURNING id`
	row := db.QueryRow(query, appID)

	// Extract the resource ID.
	var resourceID *string
	if err := row.Scan(&resourceID); err != nil {
		logcabin.Error.Print(err)
		return nil, err
	}
	return resourceID, nil
}

func lookUpResourceID(db *sql.DB, appID string) (*string, error) {
	resourceID, err := getResourceID(db, appID)
	if err != nil {
		return nil, err
	}
	if resourceID != nil {
		return resourceID, nil
	}
	return addResource(db, appID)
}

func registerApp(db *sql.DB, appID, subjectType, externalSubjectID, level string) error {

	// Look up the subject ID, adding the subject to the database if necessary.
	subjectID, err := lookUpSubjectID(db, subjectType, externalSubjectID)
	if err != nil {
		return err
	}

	// Look up the app ID, adding the app to the database if necessary.
	resourceID, err := lookUpResourceID(db, appID)
	if err != nil {
		return err
	}

	// Add the permission.
	stmt := `INSERT INTO permissions (subject_id, resource_id, permission_level_id)
           (SELECT $1, $2, id FROM permission_levels WHERE name = $3)`
	_, err = db.Exec(stmt, *subjectID, resourceID, level)
	return err
}

func runConversion(db, deDb *sql.DB, deUsersGroupID string) error {

	// Clean up the permissions database.
	if err := cleanUpPerms(db); err != nil {
		return err
	}

	// Define the DE users group as a subject in the permissions database.
	if err := defineDeUsersGroup(db, deUsersGroupID); err != nil {
		return err
	}

	// Get the app listing from the DE database.
	apps, err := listApps(deDb)
	if err != nil {
		return err
	}
	defer apps.Close()

	// Register each app in the permissions database.
	var appID, username *string
	var isPublic *bool
	for apps.Next() {
		if err := apps.Scan(&appID, &isPublic, &username); err != nil {
			return err
		}
		if *isPublic {
			if err := registerApp(db, *appID, "group", deUsersGroupID, "read"); err != nil {
				return err
			}
		} else if username != nil {
			if err := registerApp(db, *appID, "user", *username, "own"); err != nil {
				return err
			}
		}
	}

	return nil
}

func getDEUsersGroupID(grouperDb *sql.DB, folderNamePrefix string) (string, error) {
	query := "SELECT id FROM grouper_groups WHERE name = $1"
	row := grouperDb.QueryRow(query, fmt.Sprintf("%s:users:de-users", folderNamePrefix))

	// Extract the group ID.
	var groupID *string
	if err := row.Scan(&groupID); err != nil {
		logcabin.Error.Print(err)
		return "", err
	}

	return *groupID, nil
}

func main() {
	config := flag.String("config", "", "The path to the configuration file.")
	deDburi := flag.String("de-database-uri", "", "The URI to use when connecting to the DE database.")
	deDbname := flag.String("de-database-name", "de", "The name of the DE database.")

	// Parse the command line arguments.
	flag.Parse()

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
	defer db.Close()
	if err := db.Ping(); err != nil {
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
	defer grouperDb.Close()
	if err := grouperDb.Ping(); err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Retrieve the Grouper folder name prefix.
	grouperFolderNamePrefix, err := cfg.String("grouperdb.folder_name_prefix")
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Determine the DE Users group ID.
	deUsersGroupID, err := getDEUsersGroupID(grouperDb, grouperFolderNamePrefix)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Determine the DE database URI.
	*deDburi, err = determineDEDatabaseURI(*deDburi, dburi, *deDbname)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Establish the connection to the DE database.
	deDb, err := sql.Open("postgres", *deDburi)
	if err != nil {
		logcabin.Error.Fatal(err.Error())
	}
	defer deDb.Close()
	if err := deDb.Ping(); err != nil {
		logcabin.Error.Fatal(err.Error())
	}

	// Run the conversion.
	if err := runConversion(db, deDb, deUsersGroupID); err != nil {
		logcabin.Error.Fatal(err.Error())
	}
}
