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

// App defines the interface for a user-sessions application
type App interface {
	Greeting(http.ResponseWriter, *http.Request)
	GetRequest(http.ResponseWriter, *http.Request)
	PutRequest(http.ResponseWriter, *http.Request)
	PostRequest(http.ResponseWriter, *http.Request)
	DeleteRequest(http.ResponseWriter, *http.Request)
}

// UserSessionRecord represents a user session stored in the database
type UserSessionRecord struct {
	ID      string
	Session string
	UserID  string
}

// convert makes sure that the JSON has the correct format. "wrap" tells convert
// whether to wrap the object in a map with "session" as the key.
func convert(record *UserSessionRecord, wrap bool) (map[string]interface{}, error) {
	var values map[string]interface{}

	if record.Session != "" {
		if err := json.Unmarshal([]byte(record.Session), &values); err != nil {
			return nil, err
		}
	}

	// We don't want the return value wrapped in a session object, so unwrap it
	// if it is wrapped.
	if !wrap {
		if _, ok := values["session"]; ok {
			return values["session"].(map[string]interface{}), nil
		}
		return values, nil
	}

	// We do want the return value wrapped in a session object, so wrap it if it
	// isn't already.
	if _, ok := values["session"]; !ok {
		newmap := make(map[string]interface{})
		newmap["session"] = values
		return newmap, nil
	}

	return values, nil
}

// DB is the interface for interacting with the sessions database.
type DB interface {
	isUser(username string) (bool, error)
	hasSessions(username string) (bool, error)
	getSessions(username string) ([]UserSessionRecord, error)
	insertSession(username, session string) error
	updateSession(username, session string) error
	deleteSession(username string) error
}

// SessionsDB handles interacting with the sessions database.
type SessionsDB struct {
	db *sql.DB
}

// NewSessionsDB returns a newly created *SessionsDB
func NewSessionsDB(db *sql.DB) *SessionsDB {
	return &SessionsDB{
		db: db,
	}
}

// isUser returnes whether or not the user is present in the sessions database.
func (s *SessionsDB) isUser(username string) (bool, error) {
	return queries.IsUser(s.db, username)
}

// hasSessions returns whether or not the given user has a session already.
func (s *SessionsDB) hasSessions(username string) (bool, error) {
	query := `SELECT COUNT(s.*)
              FROM user_sessions s,
                   users u
             WHERE s.user_id = u.id
               AND u.username = $1`
	var count int64
	if err := s.db.QueryRow(query, username).Scan(&count); err != nil {
		return false, err
	}
	return count > 0, nil
}

// getSessions returns a []UserSessionRecord of all of the sessions associated
// with the provided username.
func (s *SessionsDB) getSessions(username string) ([]UserSessionRecord, error) {
	query := `SELECT s.id AS id,
                   s.user_id AS user_id,
                   s.session AS session
              FROM user_sessions s,
                   users u
             WHERE s.user_id = u.id
               AND u.username = $1`

	rows, err := s.db.Query(query, username)
	if err != nil {
		return nil, err
	}
	defer rows.Close()

	var sessions []UserSessionRecord
	for rows.Next() {
		var session UserSessionRecord
		if err := rows.Scan(&session.ID, &session.UserID, &session.Session); err != nil {
			return nil, err
		}
		sessions = append(sessions, session)
	}

	if err := rows.Err(); err != nil {
		return sessions, err
	}

	return sessions, nil
}

// insertSession adds a new session to the database for the user.
func (s *SessionsDB) insertSession(username, session string) error {
	query := `INSERT INTO user_sessions (user_id, session)
                 VALUES ($1, $2)`
	userID, err := queries.UserID(s.db, username)
	if err != nil {
		return err
	}
	_, err = s.db.Exec(query, userID, session)
	return err
}

// updateSession updates the session in the database for the user.
func (s *SessionsDB) updateSession(username, session string) error {
	query := `UPDATE ONLY user_sessions
                    SET session = $2
                  WHERE user_id = $1`
	userID, err := queries.UserID(s.db, username)
	if err != nil {
		return err
	}
	_, err = s.db.Exec(query, userID, session)
	return err
}

// deleteSession deletes the user's session from the database.
func (s *SessionsDB) deleteSession(username string) error {
	query := `DELETE FROM ONLY user_sessions WHERE user_id = $1`
	userID, err := queries.UserID(s.db, username)
	if err != nil {
		return err
	}
	_, err = s.db.Exec(query, userID)
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

// UserSessionsApp is an implementation of the App interface created to manage
// user sessions.
type UserSessionsApp struct {
	sessions DB
	router   *mux.Router
}

// New returns a new *UserSessionsApp
func New(db DB) *UserSessionsApp {
	app := &UserSessionsApp{
		sessions: db,
		router:   mux.NewRouter(),
	}
	app.router.HandleFunc("/", app.Greeting).Methods("GET")
	app.router.HandleFunc("/{username}", app.GetRequest).Methods("GET")
	app.router.HandleFunc("/{username}", app.PutRequest).Methods("PUT")
	app.router.HandleFunc("/{username}", app.PostRequest).Methods("POST")
	app.router.HandleFunc("/{username}", app.DeleteRequest).Methods("DELETE")
	return app
}

// Greeting prints out a greeting to the writer.
func (u *UserSessionsApp) Greeting(writer http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(writer, "Hello from user-sessions.")
}

func (u *UserSessionsApp) getUserSessionForRequest(username string, wrap bool) ([]byte, error) {
	sessions, err := u.sessions.getSessions(username)
	if err != nil {
		return nil, fmt.Errorf("Error getting sessions for username %s: %s", username, err)
	}

	var retval UserSessionRecord
	if len(sessions) >= 1 {
		retval = sessions[0]
	}

	response, err := convert(&retval, wrap)
	if err != nil {
		return nil, fmt.Errorf("Error generating response for username %s: %s", username, err)
	}

	var jsoned []byte
	if len(response) > 0 {
		jsoned, err = json.Marshal(response)
		if err != nil {
			return nil, fmt.Errorf("Error generating session JSON for user %s: %s", username, err)
		}
	} else {
		jsoned = []byte("{}")
	}

	return jsoned, nil
}

// GetRequest handles writing out a user's session as a response.
func (u *UserSessionsApp) GetRequest(writer http.ResponseWriter, r *http.Request) {
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

	logcabin.Info.Printf("Getting user session for %s", username)
	if userExists, err = u.sessions.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		badRequest(writer, fmt.Sprintf("User %s does not exist", username))
		return
	}

	jsoned, err := u.getUserSessionForRequest(username, false)
	if err != nil {
		errored(writer, err.Error())
	}

	writer.Write(jsoned)
}

// PutRequest handles creating a new user session.
func (u *UserSessionsApp) PutRequest(writer http.ResponseWriter, r *http.Request) {
	u.PostRequest(writer, r)
}

// PostRequest handles modifying an existing user session.
func (u *UserSessionsApp) PostRequest(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		hasSession bool
		err        error
		ok         bool
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = u.sessions.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		badRequest(writer, fmt.Sprintf("User %s does not exist", username))
		return
	}

	if hasSession, err = u.sessions.hasSessions(username); err != nil {
		errored(writer, fmt.Sprintf("Error checking session for user %s: %s", username, err))
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
	if !hasSession {
		if err = u.sessions.insertSession(username, bodyString); err != nil {
			errored(writer, fmt.Sprintf("Error inserting session for user %s: %s", username, err))
			return
		}
	} else {
		if err = u.sessions.updateSession(username, bodyString); err != nil {
			errored(writer, fmt.Sprintf("Error updating session for user %s: %s", username, err))
			return
		}
	}

	jsoned, err := u.getUserSessionForRequest(username, true)
	if err != nil {
		errored(writer, err.Error())
		return
	}

	writer.Write(jsoned)
}

// DeleteRequest handles deleting a user session.
func (u *UserSessionsApp) DeleteRequest(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		hasSession bool
		err        error
		ok         bool
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = u.sessions.isUser(username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		badRequest(writer, fmt.Sprintf("User %s does not exist", username))
		return
	}

	if hasSession, err = u.sessions.hasSessions(username); err != nil {
		errored(writer, fmt.Sprintf("Error checking session for user %s: %s", username, err))
		return
	}

	if !hasSession {
		return
	}

	if err = u.sessions.deleteSession(username); err != nil {
		errored(writer, fmt.Sprintf("Error deleting session for user %s: %s", username, err))
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
	sessionsDB := NewSessionsDB(db)
	app := New(sessionsDB)
	logcabin.Error.Fatal(http.ListenAndServe(fixAddr(*port), app.router))
}
