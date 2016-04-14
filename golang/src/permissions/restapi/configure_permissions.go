package restapi

import (
	"crypto/tls"
	"net/http"
	"strings"

	errors "github.com/go-swagger/go-swagger/errors"
	httpkit "github.com/go-swagger/go-swagger/httpkit"

	"permissions/restapi/impl"
	"permissions/restapi/operations"
	"permissions/restapi/operations/status"
)

// This file is safe to edit. Once it exists it will not be overwritten

func configureFlags(api *operations.PermissionsAPI) {
	// api.CommandLineOptionsGroups = []swag.CommandLineOptionsGroup{ ... }
}

func configureAPI(api *operations.PermissionsAPI) http.Handler {
	// configure the api here
	api.ServeError = errors.ServeError

	api.JSONConsumer = httpkit.JSONConsumer()

	api.JSONProducer = httpkit.JSONProducer()

	api.StatusGetHandler = status.GetHandlerFunc(impl.BuildStatusHandler(SwaggerJSON))

	api.ServerShutdown = func() {}

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
