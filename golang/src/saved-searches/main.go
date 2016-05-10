package main

import (
	"configurate"
	"database/sql"
	"encoding/json"
	"flag"
	"fmt"
	"io/ioutil"
	"logcabin"
	"net/http"
	"os"
	"queries"
	"strings"

	"github.com/gorilla/mux"
	_ "github.com/lib/pq"
	"github.com/olebedev/config"
)

var (
	gitref  string
	appver  string
	builtby string
)

// AppVersion prints the version information to stdout
func AppVersion() {
	if appver != "" {
		fmt.Printf("App-Version: %s\n", appver)
	}
	if gitref != "" {
		fmt.Printf("Git-Ref: %s\n", gitref)
	}
	if builtby != "" {
		fmt.Printf("Built-By: %s\n", builtby)
	}
}

func badRequest(writer http.ResponseWriter, msg string) {
	writer.WriteHeader(http.StatusBadRequest)
	writer.Write([]byte(msg))
	logcabin.Error.Print(msg)
}

func errored(writer http.ResponseWriter, msg string) {
	writer.WriteHeader(http.StatusInternalServerError)
	writer.Write([]byte(msg))
	logcabin.Error.Print(msg)
}

func notFound(writer http.ResponseWriter, msg string) {
	writer.WriteHeader(http.StatusNotFound)
	writer.Write([]byte(msg))
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

	notFound(writer, string(retval))

	return
}

// SavedSearches contains the application state for saved-searches
type SavedSearches struct {
	cfg    *config.Config
	db     *sql.DB
	router *mux.Router
}

// New returns a new *SavedSearches
func New(cfg *config.Config, db *sql.DB) *SavedSearches {
	var s *SavedSearches
	router := mux.NewRouter()
	s = &SavedSearches{
		cfg:    cfg,
		db:     db,
		router: router,
	}
	router.HandleFunc("/", s.Greeting).Methods("GET")
	router.HandleFunc("/{username}", s.Get).Methods("GET")
	router.HandleFunc("/{username}", s.Post).Methods("PUT")
	router.HandleFunc("/{username}", s.Post).Methods("POST")
	router.HandleFunc("/{username}", s.Delete).Methods("DELETE")
	return s
}

func (s *SavedSearches) hasSavedSearches(username string) (bool, error) {
	var (
		err    error
		exists bool
	)

	query := `SELECT EXISTS(
              SELECT 1
                FROM user_saved_searches s,
                     users u
               WHERE s.user_id = u.id
                 AND u.username = $1) AS exists`

	if err = s.db.QueryRow(query, username).Scan(&exists); err != nil {
		return false, err
	}

	return exists, nil
}

func (s *SavedSearches) getSavedSearches(username string) ([]string, error) {
	var (
		err    error
		retval []string
		rows   *sql.Rows
	)

	query := `SELECT s.saved_searches saved_searches
              FROM user_saved_searches s,
                   users u
             WHERE s.user_id = u.id
               AND u.username = $1`

	if rows, err = s.db.Query(query, username); err != nil {
		return nil, err
	}
	defer rows.Close()

	for rows.Next() {
		var search string
		if err = rows.Scan(&search); err != nil {
			return nil, err
		}
		retval = append(retval, search)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return retval, nil
}

func (s *SavedSearches) insertSavedSearches(username, searches string) error {
	var (
		err    error
		userID string
	)

	query := `INSERT INTO user_saved_searches (user_id, saved_searches) VALUES ($1, $2)`

	if userID, err = queries.UserID(s.db, username); err != nil {
		return err
	}

	_, err = s.db.Exec(query, userID, searches)
	return err
}

func (s *SavedSearches) updateSavedSearches(username, searches string) error {
	var (
		err    error
		userID string
	)

	query := `UPDATE ONLY user_saved_searches SET saved_searches = $2 WHERE user_id = $1`

	if userID, err = queries.UserID(s.db, username); err != nil {
		return err
	}

	_, err = s.db.Exec(query, userID, searches)
	return err
}

func (s *SavedSearches) deleteSavedSearches(username string) error {
	var (
		err    error
		userID string
	)

	query := `DELETE FROM ONLY user_saved_searches WHERE user_id = $1`

	if userID, err = queries.UserID(s.db, username); err != nil {
		return nil
	}

	_, err = s.db.Exec(query, userID)
	return err
}

// Greeting is the http handler for the / endpoint.
func (s *SavedSearches) Greeting(writer http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(writer, "Hello from saved-searches.")
}

// Get is the http handler for GET requests to the /{username} path.
func (s *SavedSearches) Get(writer http.ResponseWriter, r *http.Request) {
	var (
		username   string
		userExists bool
		err        error
		ok         bool
		searches   []string
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = queries.IsUser(s.db, username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		handleNonUser(writer, username)
		return
	}

	if searches, err = s.getSavedSearches(username); err != nil {
		errored(writer, err.Error())
		return
	}

	if len(searches) < 1 {
		fmt.Fprintf(writer, "{}")
		return
	}

	fmt.Fprintf(writer, searches[0])
}

// Post is the http handler for POST requests to the /{username} path.
func (s *SavedSearches) Post(writer http.ResponseWriter, r *http.Request) {
	var (
		username    string
		userExists  bool
		hasSearches bool
		err         error
		ok          bool
		v           = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	bodyBuffer, err := ioutil.ReadAll(r.Body)
	if err != nil {
		errored(writer, fmt.Sprintf("Error reading body: %s", err))
		return
	}

	// Make sure valid JSON was uploaded in the body.
	var parsedBody interface{}
	if err = json.Unmarshal(bodyBuffer, &parsedBody); err != nil {
		badRequest(writer, fmt.Sprintf("Error parsing body: %s", err.Error()))
		return
	}

	bodyString := string(bodyBuffer)

	if userExists, err = queries.IsUser(s.db, username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		handleNonUser(writer, username)
		return
	}

	if hasSearches, err = s.hasSavedSearches(username); err != nil {
		errored(writer, err.Error())
		return
	}

	var upsert func(string, string) error
	if hasSearches {
		upsert = s.updateSavedSearches
	} else {
		upsert = s.insertSavedSearches
	}
	if err = upsert(username, bodyString); err != nil {
		errored(writer, err.Error())
		return
	}

	retval := map[string]interface{}{
		"saved_searches": parsedBody,
	}
	jsoned, err := json.Marshal(retval)
	if err != nil {
		errored(writer, err.Error())
		return
	}

	writer.Write(jsoned)
}

// Delete is the http handler for DELETE requests to the /{username} path.
func (s *SavedSearches) Delete(writer http.ResponseWriter, r *http.Request) {
	var (
		err        error
		ok         bool
		userExists bool
		username   string
		v          = mux.Vars(r)
	)

	if username, ok = v["username"]; !ok {
		badRequest(writer, "Missing username in URL")
		return
	}

	if userExists, err = queries.IsUser(s.db, username); err != nil {
		badRequest(writer, fmt.Sprintf("Error checking for username %s: %s", username, err))
		return
	}

	if !userExists {
		return
	}

	if err = s.deleteSavedSearches(username); err != nil {
		errored(writer, err.Error())
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
		version = flag.Bool("version", false, "Print the version information")
		cfgPath = flag.String("config", "/etc/iplant/de/jobservices.yml", "The path to the config file")
		port    = flag.String("port", "60000", "The port number to listen on")
		err     error
		cfg     *config.Config
	)

	flag.Parse()

	if *version {
		AppVersion()
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

	logcabin.Info.Println("Connecting to the database...")
	db, err := sql.Open("postgres", dburi)
	if err != nil {
		logcabin.Error.Fatal(err)
	}
	defer db.Close()
	logcabin.Info.Println("Connected to the database.")

	if err := db.Ping(); err != nil {
		logcabin.Error.Fatal(err)
	}
	logcabin.Info.Println("Successfully pinged the database")

	app := New(cfg, db)
	logcabin.Error.Fatal(http.ListenAndServe(fixAddr(*port), app.router))
}
