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
	"regexp"
	"strings"
	"version"

	"github.com/gorilla/mux"
	_ "github.com/lib/pq"
	"github.com/olebedev/config"
)

func badRequest(writer http.ResponseWriter, msg string) {
	http.Error(writer, msg, http.StatusBadRequest)
	logcabin.Error.Print(msg)
}

func errored(writer http.ResponseWriter, msg string) {
	http.Error(writer, msg, http.StatusInternalServerError)
	logcabin.Error.Print(msg)
}

func notFound(writer http.ResponseWriter, msg string) {
	http.Error(writer, msg, http.StatusNotFound)
	logcabin.Error.Print(msg)
}

func validSHA1(sha1 string) (bool, error) {
	return regexp.MatchString("^[a-fA-F0-9]+$", sha1)
}

// Databaser is an interface for objects that interact with a data store. Mostly
// included to make unit tests easier to write.
type Databaser interface {
	hasSHA1(string) (bool, error)
	getTreeURLs(string) ([]string, error)
	deleteTreeURLs(string) error
	insertTreeURLs(string, string) error
	updateTreeURLs(string, string) error
}

// PostgresDB is a Databaser for storing information in a postgres database.
type PostgresDB struct {
	db *sql.DB
}

// NewPostgresDB returns a *PostgresDB
func NewPostgresDB(db *sql.DB) *PostgresDB {
	return &PostgresDB{
		db: db,
	}
}

func (p *PostgresDB) hasSHA1(sha1 string) (bool, error) {
	var (
		err    error
		exists bool
	)

	query := `SELECT EXISTS(SELECT 1 FROM tree_urls WHERE sha1 = $1) AS exists`

	if err = p.db.QueryRow(query, sha1).Scan(&exists); err != nil {
		return false, err
	}

	return exists, nil
}

func (p *PostgresDB) getTreeURLs(sha1 string) ([]string, error) {
	var (
		err    error
		query  string
		rows   *sql.Rows
		retval []string
	)

	query = `SELECT tree_urls FROM tree_urls WHERE sha1 = $1`

	if rows, err = p.db.Query(query, sha1); err != nil {
		return nil, err
	}
	defer rows.Close()

	for rows.Next() {
		var json string

		if err = rows.Scan(&json); err != nil {
			return nil, err
		}

		retval = append(retval, json)
	}

	if err = rows.Err(); err != nil {
		return nil, err
	}

	return retval, nil
}

func (p *PostgresDB) deleteTreeURLs(sha1 string) error {
	var (
		err   error
		query string
	)

	query = `DELETE FROM tree_urls WHERE sha1 = $1`
	_, err = p.db.Exec(query, sha1)
	return err
}

func (p *PostgresDB) insertTreeURLs(sha1, treeURLs string) error {
	var (
		err   error
		query string
	)

	logcabin.Info.Printf("Inserting tree URLs for %s: %s", sha1, treeURLs)
	query = `INSERT INTO tree_urls (sha1, tree_urls) VALUES ($1, $2)`
	_, err = p.db.Exec(query, sha1, treeURLs)
	return err
}

func (p *PostgresDB) updateTreeURLs(sha1, treeURLs string) error {
	var (
		err   error
		query string
	)

	logcabin.Info.Printf("Updating tree URLs for %s: %s", sha1, treeURLs)
	query = `UPDATE ONLY tree_urls SET tree_urls = $2 WHERE sha1 = $1`
	_, err = p.db.Exec(query, sha1, treeURLs)
	return err
}

// TreeURLs contains the application state for tree-urls
type TreeURLs struct {
	db     Databaser
	router *mux.Router
}

// New returns a new *TreeURLs.
func New(db Databaser) *TreeURLs {
	var t *TreeURLs
	router := mux.NewRouter()
	t = &TreeURLs{
		db:     db,
		router: router,
	}
	router.HandleFunc("/", t.Greeting).Methods("GET")
	router.HandleFunc("/{sha1}", t.Get).Methods("GET")
	router.HandleFunc("/{sha1}", t.Post).Methods("PUT")
	router.HandleFunc("/{sha1}", t.Post).Methods("POST")
	router.HandleFunc("/{sha1}", t.Delete).Methods("DELETE")
	return t
}

// Greeting is the http handler for the / endpoint.
func (t *TreeURLs) Greeting(writer http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(writer, "Hello from tree-urls.")
}

// Get is http handler for GET requests to the /{sha1} path.
func (t *TreeURLs) Get(writer http.ResponseWriter, r *http.Request) {
	var (
		err  error
		ok   bool
		sha1 string
		urls []string
		v    = mux.Vars(r)
	)

	if sha1, ok = v["sha1"]; !ok {
		badRequest(writer, "Missing sha1 in URL")
		return
	}

	ok, err = validSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if !ok {
		badRequest(writer, fmt.Sprintf("Invalid SHA1 format: %s", sha1))
		return
	}

	ok, err = t.db.hasSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if !ok {
		notFound(writer, fmt.Sprintf("Not Found: %s", sha1))
		return
	}

	urls, err = t.db.getTreeURLs(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if len(urls) < 1 {
		notFound(writer, fmt.Sprintf("Not Found: %s", sha1))
		return
	}

	writer.Write([]byte(urls[0]))
}

// Post is the http handler for POST requests to the /{sha1} path.
func (t *TreeURLs) Post(writer http.ResponseWriter, r *http.Request) {
	var (
		err    error
		exists bool
		ok     bool
		sha1   string
		v      = mux.Vars(r)
	)

	if sha1, ok = v["sha1"]; !ok {
		badRequest(writer, "Missing sha1 in URL")
		return
	}

	ok, err = validSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if !ok {
		badRequest(writer, fmt.Sprintf("Invalid SHA1 format: %s", sha1))
		return
	}

	bodyBuffer, err := ioutil.ReadAll(r.Body)
	if err != nil {
		errored(writer, fmt.Sprintf("Error reading body: %s", err))
		return
	}

	// Make sure valid JSON was uploaded in the body.
	var tmp interface{}
	if err = json.Unmarshal(bodyBuffer, &tmp); err != nil {
		badRequest(writer, fmt.Sprintf("Error parsing body: %s", err.Error()))
		return
	}

	bodyString := string(bodyBuffer)

	exists, err = t.db.hasSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}

	var upsert func(string, string) error
	if exists {
		upsert = t.db.updateTreeURLs
	} else {
		upsert = t.db.insertTreeURLs
	}
	if err = upsert(sha1, bodyString); err != nil {
		errored(writer, err.Error())
		return
	}

	retval := map[string]interface{}{
		"tree_urls": bodyString,
	}
	jsoned, err := json.Marshal(retval)
	if err != nil {
		errored(writer, err.Error())
		return
	}

	writer.Write(jsoned)
}

//
// Delete is the http handler for DELETE requests to the /{sha1} path.
func (t *TreeURLs) Delete(writer http.ResponseWriter, r *http.Request) {
	var (
		err  error
		ok   bool
		sha1 string
		v    = mux.Vars(r)
	)

	if sha1, ok = v["sha1"]; !ok {
		badRequest(writer, "Missing sha1 in URL")
		return
	}

	ok, err = validSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if !ok {
		badRequest(writer, fmt.Sprintf("Invalid SHA1 format: %s", sha1))
		return
	}

	ok, err = t.db.hasSHA1(sha1)
	if err != nil {
		errored(writer, err.Error())
		return
	}
	if !ok {
		return
	}

	if err = t.db.deleteTreeURLs(sha1); err != nil {
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

	databaser := NewPostgresDB(db)
	app := New(databaser)
	logcabin.Error.Fatal(http.ListenAndServe(fixAddr(*port), app.router))
}
