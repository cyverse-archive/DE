(ns apps.routes.api
  (:use [service-logging.middleware :only [wrap-logging clean-context]]
        [compojure.core :only [wrap-routes]]
        [clojure-commons.query-params :only [wrap-query-params]]
        [common-swagger-api.schema]
        [apps.user :only [store-current-user]]
        [ring.middleware keyword-params nested-params]
        [service-logging.middleware :only [add-user-to-context]]
        [ring.util.response :only [redirect]])
  (:require [compojure.route :as route]
            [apps.routes.admin :as admin-routes]
            [apps.routes.analyses :as analysis-routes]
            [apps.routes.apps :as app-routes]
            [apps.routes.apps.categories :as app-category-routes]
            [apps.routes.apps.elements :as app-element-routes]
            [apps.routes.apps.pipelines :as pipeline-routes]
            [apps.routes.apps.metadata :as metadata-routes]
            [apps.routes.callbacks :as callback-routes]
            [apps.routes.collaborators :as collaborator-routes]
            [apps.routes.oauth :as oauth-routes]
            [apps.routes.reference-genomes :as reference-genome-routes]
            [apps.routes.status :as status-routes]
            [apps.routes.tools :as tool-routes]
            [apps.routes.users :as user-routes]
            [apps.routes.workspaces :as workspace-routes]
            [apps.util.config :as config]
            [apps.util.service :as service]
            [clojure-commons.exception :as cx]))

(defapi app
  {:exceptions cx/exception-handlers}
  (swagger-ui config/docs-uri
    :validator-url nil)
  (swagger-docs
    {:info {:title "Discovery Environment Apps API"
            :description "Documentation for the Discovery Environment Apps REST API"
            :version "2.0.0"}
     :tags [{:name "service-info", :description "Service Status Information"}
            {:name "callbacks", :description "General callback functions"}
            {:name "app-categories", :description "App Category endpoints."}
            {:name "app-hierarchies", :description "App Hierarchy endpoints."}
            {:name "app-element-types", :description "App Element endpoints."}
            {:name "apps", :description "App endpoints."}
            {:name "app-metadata", :description "App Metadata endpoints."}
            {:name "pipelines", :description "Pipeline endpoints."}
            {:name "analyses", :description "Analysis endpoints."}
            {:name "tool-data-containers", :description "Tool Docker Data Container endpoints."}
            {:name "tools", :description "Tool endpoints."}
            {:name "workspaces", :description "Workspace endpoints."}
            {:name "users", :description "User endpoints."}
            {:name "tool-requests", :description "Tool Request endpoints."}
            {:name "reference-genomes", :description "Reference Genome endpoints."}
            {:name "oauth-routes", :description "OAuth callback routes."}
            {:name "collaborator-routes", :description "Collaborator Information Routes"}
            {:name "admin-apps", :description "Admin App endpoints."}
            {:name "admin-app-metadata", :description "Admin App Metadata endpoints."}
            {:name "admin-categories", :description "Admin App Category endpoints."}
            {:name "admin-ontologies", :description "Admin App Ontology endpoints."}
            {:name "admin-container-images", :description "Admin Tool Docker Images endpoints."}
            {:name "admin-data-containers", :description "Admin Docker Data Container endpoints."}
            {:name "admin-tools", :description "Admin Tool endpoints."}
            {:name "admin-reference-genomes", :description "Admin Reference Genome endpoints."}
            {:name "admin-tool-requests", :description "Admin Tool Request endpoints."}]})
  (middlewares
    [clean-context
     wrap-keyword-params
     wrap-query-params
     (wrap-routes wrap-logging)]
    (context* "/" []
      :tags ["service-info"]
      status-routes/status)
    (context* "/callbacks" []
      :tags ["callbacks"]
      callback-routes/callbacks))
  (middlewares
    [clean-context
     wrap-keyword-params
     wrap-query-params
     add-user-to-context
     store-current-user
     wrap-logging]
    (context* "/apps/categories" []
      :tags ["app-categories"]
      app-category-routes/app-categories)
    (context* "/apps/hierarchies" []
      :tags ["app-hierarchies"]
      app-category-routes/app-hierarchies)
    (context* "/apps/elements" []
      :tags ["app-element-types"]
      app-element-routes/app-elements)
    (context* "/apps" []
      :tags ["apps"]
      app-routes/apps)
    (context* "/apps/pipelines" []
      :tags ["pipelines"]
      pipeline-routes/pipelines)
    (context* "/apps/:app-id/metadata" []
      :tags ["app-metadata"]
      metadata-routes/app-metadata)
    (context* "/analyses" []
      :tags ["analyses"]
      analysis-routes/analyses)
    (context* "/tools/data-containers" []
      :tags ["tool-data-containers"]
      tool-routes/data-containers)
    (context* "/tools" []
      :tags ["tools"]
      tool-routes/tools)
    (context* "/workspaces" []
      :tags ["workspaces"]
      workspace-routes/workspaces)
    (context* "/users" []
      :tags ["users"]
      user-routes/users)
    (context* "/tool-requests" []
      :tags ["tool-requests"]
      tool-routes/tool-requests)
    (context* "/reference-genomes" []
      :tags ["reference-genomes"]
      reference-genome-routes/reference-genomes)
    (context* "/oauth" []
      :tags ["oauth-routes"]
      oauth-routes/oauth)
    (context* "/collaborators" []
      :tags ["collaborator-routes"]
      collaborator-routes/collaborators)
    (context* "/admin/apps" []
      :tags ["admin-apps"]
      admin-routes/admin-apps)
    (context* "/admin/apps/categories" []
      :tags ["admin-categories"]
      admin-routes/admin-categories)
    (context* "/admin/apps/:app-id/metadata" []
      :tags ["admin-app-metadata"]
      metadata-routes/admin-app-metadata)
    (context* "/admin/ontologies" []
      :tags ["admin-ontologies"]
      admin-routes/admin-ontologies)
    (context* "/admin/reference-genomes" []
      :tags ["admin-reference-genomes"]
      admin-routes/reference-genomes)
    (context* "/admin/tools/container-images" []
      :tags ["admin-container-images"]
      tool-routes/container-images)
    (context* "/admin/tools/data-containers" []
      :tags ["admin-data-containers"]
      tool-routes/admin-data-containers)
    (context* "/admin/tools" []
      :tags ["admin-tools"]
      tool-routes/admin-tools)
    (context* "/admin/tool-requests" []
      :tags ["admin-tool-requests"]
      admin-routes/admin-tool-requests)
    (route/not-found (service/unrecognized-path-response))))
