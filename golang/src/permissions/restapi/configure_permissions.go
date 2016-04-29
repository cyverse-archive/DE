package restapi

import (
	"configurate"
	"crypto/tls"
	"database/sql"
	"fmt"
	"logcabin"
	"net/http"
	"strings"

	errors "github.com/go-swagger/go-swagger/errors"
	httpkit "github.com/go-swagger/go-swagger/httpkit"
	swag "github.com/go-swagger/go-swagger/swag"
	_ "github.com/lib/pq"

	"permissions/restapi/operations"
	"permissions/restapi/operations/resource_types"
	"permissions/restapi/operations/status"

	resource_types_impl "permissions/restapi/impl/resource_types"
	status_impl "permissions/restapi/impl/status"
)

// This file is safe to edit. Once it exists it will not be overwritten

// Command line options that aren't managed by go-swagger.
var options struct {
	CfgPath string `long:"config" default:"/etc/iplant/de/permissions.yaml" description:"The path to the config file"`
}

// Register the command-line options.
func configureFlags(api *operations.PermissionsAPI) {
	api.CommandLineOptionsGroups = []swag.CommandLineOptionsGroup{
		swag.CommandLineOptionsGroup{"Service Options", "", &options},
	}
}

// Validate the custom command-line options.
func validateOptions() error {
	if options.CfgPath == "" {
		return fmt.Errorf("--config must be set")
	}

	return nil
}

// The database connection.
var db *sql.DB

// Initialize the service.
func initService() error {
	if err := configurate.Init(options.CfgPath); err != nil {
		return err
	}

	dburi, err := configurate.C.String("db.uri")
	if err != nil {
		return err
	}
	logcabin.Info.Printf("DB URI: %s\n", dburi)

	db, err = sql.Open("postgres", dburi)
	if err != nil {
		return err
	}

	if err := db.Ping(); err != nil {
		return err
	}

	return nil
}

// Clean up when the service exits.
func cleanup() {
	logcabin.Info.Printf("Closing the database connection.")
	db.Close()
}

func configureAPI(api *operations.PermissionsAPI) http.Handler {
	if err := validateOptions(); err != nil {
		logcabin.Error.Fatal(err)
	}

	if err := initService(); err != nil {
		logcabin.Error.Fatal(err)
	}

	api.ServeError = errors.ServeError

	api.JSONConsumer = httpkit.JSONConsumer()

	api.JSONProducer = httpkit.JSONProducer()

	api.StatusGetHandler = status.GetHandlerFunc(status_impl.BuildStatusHandler(SwaggerJSON))

	api.ResourceTypesGetResourceTypesHandler = resource_types.GetResourceTypesHandlerFunc(
		resource_types_impl.BuildResourceTypesGetHandler(db),
	)

	api.ResourceTypesPutResourceTypesHandler = resource_types.PutResourceTypesHandlerFunc(
		resource_types_impl.BuildResourceTypesPutHandler(db),
	)

	api.ResourceTypesPostResourceTypesIDHandler = resource_types.PostResourceTypesIDHandlerFunc(
		resource_types_impl.BuildResourceTypesIDPostHandler(db),
	)

	api.ResourceTypesDeleteResourceTypesIDHandler = resource_types.DeleteResourceTypesIDHandlerFunc(
		resource_types_impl.BuildResourceTypesIDDeleteHandler(db),
	)

	api.ServerShutdown = cleanup

	return setupGlobalMiddleware(api.Serve(setupMiddlewares))
}

// The TLS configuration before HTTPS server starts.
func configureTLS(tlsConfig *tls.Config) {
	// Make all necessary changes to the TLS configuration here.
}

// The middleware configuration is for the handler executors. These do not apply to the swagger.json document.
// The middleware executes after routing but before authentication, binding and validation
func setupMiddlewares(handler http.Handler) http.Handler {
	return handler
}

// The middleware configuration happens before anything, this middleware also applies to serving the swagger.json
// document. So this is a good place to plug in a panic handling middleware, logging and metrics
func setupGlobalMiddleware(handler http.Handler) http.Handler {
	return uiMiddleware(handler)
}

// The middleware to serve up the interactive Swagger UI.
func uiMiddleware(handler http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if r.URL.Path == "/docs" {
			http.Redirect(w, r, "/docs/", http.StatusFound)
			return
		}
		if strings.Index(r.URL.Path, "/docs/") == 0 {
			http.StripPrefix("/docs/", http.FileServer(http.Dir("docs"))).ServeHTTP(w, r)
			return
		}
		handler.ServeHTTP(w, r)
	})
}
