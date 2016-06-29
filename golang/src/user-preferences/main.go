package main

import (
	"configurate"
	"database/sql"
	"dbutil"
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"logcabin"
	"net/http"
	"os"
	"queries"
	"strings"
	"version"

	"github.com/gorilla/mux"
	_ "github.com/lib/pq"
	"github.com/olebedev/config"
)

// App defines the interface for a user-preferences application
type App interface {
	Greeting(http.ResponseWriter, *http.Request)
	GetRequest(http.ResponseWriter, *http.Request)
	PutRequest(http.ResponseWriter, *http.Request)
	PostRequest(http.ResponseWriter, *http.Request)
	DeleteRequest(http.ResponseWriter, *http.Request)
}

// UserPreferencesRecord represents a user's preferences stored in the database
type UserPreferencesRecord struct {
	ID          string
	Preferences string
	UserID      string
}

// convert makes sure that the JSON has the correct format. "wrap" tells convert
// whether to wrap the object in a map with "preferences" as the key.
func convert(record *UserPreferencesRecord, wrap bool) (map[string]interface{}, error) {
	var values map[string]interface{}

	if record.Preferences != "" {
		if err := json.Unmarshal([]byte(record.Preferences), &values); err != nil {
			return nil, err
		}
	}

	// We don't want the return value wrapped in a preferences object, so unwrap it
	// if it is wrapped.
	if !wrap {
		if _, ok := values["preferences"]; ok {
			return values["preferences"].(map[string]interface{}), nil
		}
		return values, nil
	}

	// We do want the return value wrapped in a preferences object, so wrap it if it
	// isn't already.
	if _, ok := values["preferences"]; !ok {
		newmap := make(map[string]interface{})
		newmap["preferences"] = values
		return newmap, nil
	}

	return values, nil
}

// DB defines the interface for interacting with the user-prefs db.
type DB interface {
	isUser(username string) (bool, error)
	hasPreferences(username string) (bool, error)
	getPreferences(username string) ([]UserPreferencesRecord, error)
	insertPreferences(username, prefs string) error
	updatePreferences(username, prefs string) error
	deletePreferences(username string) error
}

// PrefsDB implements the DB interface for interacting with the user-preferences
// database.
type PrefsDB struct {
	db *sql.DB
}

// NewPrefsDB returns a newly created *PrefsDB.
func NewPrefsDB(db *sql.DB) *PrefsDB {
	return &PrefsDB{
		db: db,
	}
}

// isUser returns whether the user exists in the database or not.
func (p *PrefsDB) isUser(username string) (bool, error) {
	return queries.IsUser(p.db, username)
}

// hasPreferences returns whether or not the given user has preferences already.
func (p *PrefsDB) hasPreferences(username string) (bool, error) {
	query := `SELECT COUNT(p.*)
              FROM user_preferences p,
                   users u
             WHERE p.user_id = u.id
               AND u.username = $1`
	var count int64
	if err := p.db.QueryRow(query, username).Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

// getPreferences returns a []UserPreferencesRecord of all of the preferences associated
// with the provided username.
func (p *PrefsDB) getPreferences(username string) ([]UserPreferencesRecord, error) {
	query := `SELECT p.id AS id,
                   p.user_id AS user_id,
                   p.preferences AS preferences
              FROM user_preferences p,
                   users u
             WHERE p.user_id = u.id
               AND u.username = $1`

	rows, err := p.db.Query(query, username)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var prefs []UserPreferencesRecord
	for rows.Next() {
		var pref UserPreferencesRecord
		if err := rows.Scan(&pref.ID, &pref.UserID, &pref.Preferences); err != nil {
			return nil, err
		}
		prefs = append(prefs, pref)
	}

	if err := rows.Err(); err != nil {
		return prefs, err
	}

	return prefs, nil
}

// insertPreferences adds a new preferences to the database for the user.
func (p *PrefsDB) insertPreferences(username, prefs string) error {
	query := `INSERT INTO user_preferences (user_id, preferences)
                 VALUES ($1, $2)`
	userID, err := queries.UserID(p.db, username)
	if err != nil {
		return err
	}
	_, err = p.db.Exec(query, userID, prefs)
	return err
}

// updatePreferences updates the preferences in the database for the user.
func (p *PrefsDB) updatePreferences(username, prefs string) error {
	query := `UPDATE ONLY user_preferences
                    SET preferences = $2
                  WHERE user_id = $1`
	userID, err := queries.UserID(p.db, username)
	if err != nil {
		return err
	}
	_, err = p.db.Exec(query, userID, prefs)
	return err
}

// deletePreferences deletes the user's preferences from the database.
func (p *PrefsDB) deletePreferences(username string) error {
	query := `DELETE FROM ONLY user_preferences WHERE user_id = $1`
	userID, err := queries.UserID(p.db, username)
	if err != nil {
		return err
	}
	_, err = p.db.Exec(query, userID)
	return err
}

func badRequest(writer http.ResponseWriter, msg string) {
	http.Error(writer, msg, http.StatusBadRequest)
	logcabin.Error.Print(msg)
}

func errored(writer http.ResponseWriter, msg string) {
	http.Error(writer, msg, http.StatusInternalServerError)
	logcabin.Error.Print(msg)
}

func handleNonUser(writer http.ResponseWriter, username string) {
	var (
		retval []byte
		err    error
	)

	retval, err = json.Marshal(map[string]string{
		"user": username,
	})
	if err != nil {
		errored(writer, fmt.Sprintf("Error generating json for non-user %s", err))
		return
	}

	badRequest(writer, string(retval))

	return
}

// UserPreferencesApp is an implementation of the App interface created to manage
// user preferences.
type UserPreferencesApp struct {
	prefs  DB
	router *mux.Router
}

// New returns a new *UserPreferencesApp
func New(db DB) *UserPreferencesApp {
	p := &UserPreferencesApp{
		prefs:  db,
		router: mux.NewRouter(),
	}
	p.router.HandleFunc("/", p.Greeting).Methods("GET")
	p.router.HandleFunc("/{username}", p.GetRequest).Methods("GET")
	p.router.HandleFunc("/{username}", p.PutRequest).Methods("PUT")
	p.router.HandleFunc("/{username}", p.PostRequest).Methods("POST")
	p.router.HandleFunc("/{username}", p.DeleteRequest).Methods("DELETE")
	return p
}

// Greeting prints out a greeting to the writer.
func (u *UserPreferencesApp) Greeting(writer http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(writer, "Hello from user-preferences.")
}

func (u *UserPreferencesApp) getUserPreferencesForRequest(username string, wrap bool) ([]byte, error) {
	var retval UserPreferencesRecord

	prefs, err := u.prefs.getPreferences(username)
	if err != nil {
		return nil, fmt.Errorf("Error getting preferences for username %s: %s", username, err)
	}

	if len(prefs) >= 1 {
		retval = prefs[0]
	}

	response, err := convert(&retval, wrap)
	if err != nil {
		return nil, fmt.Errorf("Error generating response for username %s: %s", username, err)
	}

	var jsoned []byte
	if len(response) > 0 {
		jsoned, err = json.Marshal(response)
		if err != nil {
			return nil, fmt.Errorf("Error generating preferences JSON for user %s: %s", username, err)
		}
	} else {
		jsoned = []byte("{}")
	}

	return jsoned, nil
}

// GetRequest handles writing out a user's preferences as a response.
func (u *UserPreferencesApp) GetRequest(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		err        error
		ok         bool
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	logcabin.Info.Printf("Getting user preferences for %s", username)
	if userExists, err = u.prefs.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		handleNonUser(writer, username)
		return
	}

	jsoned, err := u.getUserPreferencesForRequest(username, false)
	if err != nil {
		errored(writer, err.Error())
	}

	writer.Write(jsoned)
}

// PutRequest handles creating new user preferences.
func (u *UserPreferencesApp) PutRequest(writer http.ResponseWriter, r *http.Request) {
	u.PostRequest(writer, r)
}

// PostRequest handles modifying an existing user's preferences.
func (u *UserPreferencesApp) PostRequest(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		hasPrefs   bool
		err        error
		ok         bool
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = u.prefs.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		handleNonUser(writer, username)
		return
	}

	if hasPrefs, err = u.prefs.hasPreferences(username); err != nil {
		errored(writer, fmt.Sprintf("Error checking preferences for user %s: %s", username, err))
		return
	}

	var checked map[string]interface{}
	bodyBuffer, err := ioutil.ReadAll(r.Body)
	if err != nil {
		errored(writer, fmt.Sprintf("Error reading body: %s", err))
		return
	}

	if err = json.Unmarshal(bodyBuffer, &checked); err != nil {
		errored(writer, fmt.Sprintf("Error parsing request body: %s", err))
		return
	}

	bodyString := string(bodyBuffer)
	if !hasPrefs {
		if err = u.prefs.insertPreferences(username, bodyString); err != nil {
			errored(writer, fmt.Sprintf("Error inserting preferences for user %s: %s", username, err))
			return
		}
	} else {
		if err = u.prefs.updatePreferences(username, bodyString); err != nil {
			errored(writer, fmt.Sprintf("Error updating preferences for user %s: %s", username, err))
			return
		}
	}

	jsoned, err := u.getUserPreferencesForRequest(username, true)
	if err != nil {
		errored(writer, err.Error())
		return
	}

	writer.Write(jsoned)
}

// DeleteRequest handles deleting a user's preferences.
func (u *UserPreferencesApp) DeleteRequest(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		hasPrefs   bool
		err        error
		ok         bool
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = u.prefs.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		handleNonUser(writer, username)
		return
	}

	if hasPrefs, err = u.prefs.hasPreferences(username); err != nil {
		errored(writer, fmt.Sprintf("Error checking preferences for user %s: %s", username, err))
		return
	}

	if !hasPrefs {
		return
	}

	if err = u.prefs.deletePreferences(username); err != nil {
		errored(writer, fmt.Sprintf("Error deleting preferences for user %s: %s", username, err))
	}
}

func fixAddr(addr string) string {
	if !strings.HasPrefix(addr, ":") {
		return fmt.Sprintf(":%s", addr)
	}
	return addr
}

func main() {
	var (
		showVersion = flag.Bool("version", false, "Print the version information")
		cfgPath     = flag.String("config", "/etc/iplant/de/jobservices.yml", "The path to the config file")
		port        = flag.String("port", "60000", "The port number to listen on")
		err         error
		cfg         *config.Config
	)

	flag.Parse()

	if *showVersion {
		version.AppVersion()
		os.Exit(0)
	}

	if *cfgPath == "" {
		logcabin.Error.Fatal("--config must be set")
	}

	if cfg, err = configurate.Init(*cfgPath); err != nil {
		logcabin.Error.Fatal(err)
	}

	dburi, err := cfg.String("db.uri")
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	connector, err := dbutil.NewDefaultConnector("1m")
	if err != nil {
		logcabin.Error.Fatal(err)
	}

	logcabin.Info.Println("Connecting to the database...")
	db, err := connector.Connect("postgres", dburi)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer db.Close()
	logcabin.Info.Println("Connected to the database.")

	if err := db.Ping(); err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Println("Successfully pinged the database")

	logcabin.Info.Printf("Listening on port %s", *port)
	prefsDB := NewPrefsDB(db)
	app := New(prefsDB)
	logcabin.Error.Fatal(http.ListenAndServe(fixAddr(*port), app.router))
}
