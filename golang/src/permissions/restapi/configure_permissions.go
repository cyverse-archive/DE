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
	"github.com/olebedev/config"

	"permissions/clients/grouper"
	"permissions/restapi/operations"
	"permissions/restapi/operations/permissions"
	"permissions/restapi/operations/resource_types"
	"permissions/restapi/operations/resources"
	"permissions/restapi/operations/status"
	"permissions/restapi/operations/subjects"

	permissions_impl "permissions/restapi/impl/permissions"
	resource_types_impl "permissions/restapi/impl/resource_types"
	resources_impl "permissions/restapi/impl/resources"
	status_impl "permissions/restapi/impl/status"
	subjects_impl "permissions/restapi/impl/subjects"
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
var grouperClient *grouper.GrouperClient

// Initialize the service.
func initService() error {
	var (
		err error
		cfg *config.Config
	)
	if cfg, err = configurate.Init(options.CfgPath); err != nil {
		return err
	}

	dburi, err := cfg.String("db.uri")
	if err != nil {
		return err
	}

	db, err = sql.Open("postgres", dburi)
	if err != nil {
		return err
	}

	grouperDburi, err := cfg.String("grouperdb.uri")
	if err != nil {
		return err
	}

	grouperFolderNamePrefix, err := cfg.String("grouperdb.folder_name_prefix")
	if err != nil {
		return err
	}

	grouperClient, err = grouper.NewGrouperClient(grouperDburi, grouperFolderNamePrefix)
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

	api.ResourceTypesDeleteResourceTypeByNameHandler = resource_types.DeleteResourceTypeByNameHandlerFunc(
		resource_types_impl.BuildDeleteResourceTypeByNameHandler(db),
	)

	api.ResourceTypesPostResourceTypesHandler = resource_types.PostResourceTypesHandlerFunc(
		resource_types_impl.BuildResourceTypesPostHandler(db),
	)

	api.ResourceTypesPutResourceTypesIDHandler = resource_types.PutResourceTypesIDHandlerFunc(
		resource_types_impl.BuildResourceTypesIDPutHandler(db),
	)

	api.ResourceTypesDeleteResourceTypesIDHandler = resource_types.DeleteResourceTypesIDHandlerFunc(
		resource_types_impl.BuildResourceTypesIDDeleteHandler(db),
	)

	api.ResourcesAddResourceHandler = resources.AddResourceHandlerFunc(
		resources_impl.BuildAddResourceHandler(db),
	)

	api.ResourcesDeleteResourceByNameHandler = resources.DeleteResourceByNameHandlerFunc(
		resources_impl.BuildDeleteResourceByNameHandler(db),
	)

	api.ResourcesListResourcesHandler = resources.ListResourcesHandlerFunc(
		resources_impl.BuildListResourcesHandler(db),
	)

	api.ResourcesUpdateResourceHandler = resources.UpdateResourceHandlerFunc(
		resources_impl.BuildUpdateResourceHandler(db),
	)

	api.ResourcesDeleteResourceHandler = resources.DeleteResourceHandlerFunc(
		resources_impl.BuildDeleteResourceHandler(db),
	)

	api.SubjectsAddSubjectHandler = subjects.AddSubjectHandlerFunc(
		subjects_impl.BuildAddSubjectHandler(db),
	)

	api.SubjectsListSubjectsHandler = subjects.ListSubjectsHandlerFunc(
		subjects_impl.BuildListSubjectsHandler(db),
	)

	api.SubjectsUpdateSubjectHandler = subjects.UpdateSubjectHandlerFunc(
		subjects_impl.BuildUpdateSubjectHandler(db),
	)

	api.SubjectsDeleteSubjectHandler = subjects.DeleteSubjectHandlerFunc(
		subjects_impl.BuildDeleteSubjectHandler(db),
	)

	api.PermissionsListPermissionsHandler = permissions.ListPermissionsHandlerFunc(
		permissions_impl.BuildListPermissionsHandler(db),
	)

	api.PermissionsGrantPermissionHandler = permissions.GrantPermissionHandlerFunc(
		permissions_impl.BuildGrantPermissionHandler(db),
	)

	api.PermissionsRevokePermissionHandler = permissions.RevokePermissionHandlerFunc(
		permissions_impl.BuildRevokePermissionHandler(db),
	)

	api.PermissionsPutPermissionHandler = permissions.PutPermissionHandlerFunc(
		permissions_impl.BuildPutPermissionHandler(db),
	)

	api.PermissionsBySubjectHandler = permissions.BySubjectHandlerFunc(
		permissions_impl.BuildBySubjectHandler(db, grouperClient),
	)

	api.PermissionsBySubjectAndResourceTypeHandler = permissions.BySubjectAndResourceTypeHandlerFunc(
		permissions_impl.BuildBySubjectAndResourceTypeHandler(db, grouperClient),
	)

	api.PermissionsBySubjectAndResourceHandler = permissions.BySubjectAndResourceHandlerFunc(
		permissions_impl.BuildBySubjectAndResourceHandler(db, grouperClient),
	)

	api.PermissionsListResourcePermissionsHandler = permissions.ListResourcePermissionsHandlerFunc(
		permissions_impl.BuildListResourcePermissionsHandler(db),
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
