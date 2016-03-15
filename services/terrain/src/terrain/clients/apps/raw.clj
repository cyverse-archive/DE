(ns terrain.clients.apps.raw
  (:use [terrain.util.transformers :only [secured-params]])
  (:require [cemerick.url :as curl]
            [clj-http.client :as client]
            [terrain.util.config :as config]))

(def apps-sort-params [:limit :offset :sort-field :sort-dir])
(def apps-analysis-listing-params (conj apps-sort-params :include-hidden :filter))
(def apps-search-params (conj apps-sort-params :search))

(defn- apps-url
  [& components]
  (str (apply curl/url (config/apps-base) components)))

(defn get-all-workflow-elements
  [params]
  (client/get (apps-url "apps" "elements")
              {:query-params     (secured-params params [:include-hidden])
               :as               :stream
               :follow-redirects false}))

(defn get-workflow-elements
  [element-type params]
  (client/get (apps-url "apps" "elements" element-type)
              {:query-params     (secured-params params [:include-hidden])
               :as               :stream
               :follow-redirects false}))

(defn get-app-categories
  [params]
  (client/get (apps-url "apps" "categories")
              {:query-params     (secured-params params [:public])
               :as               :stream
               :follow-redirects false}))

(defn apps-in-category
  [category-id params]
  (client/get (apps-url "apps" "categories" category-id)
              {:query-params     (secured-params params apps-sort-params)
               :as               :stream
               :follow-redirects false}))

(defn search-apps
  [params]
  (client/get (apps-url "apps")
              {:query-params     (secured-params params apps-search-params)
               :as               :stream
               :follow-redirects false}))

(defn create-app
  [app]
  (client/post (apps-url "apps")
               {:query-params     (secured-params)
                :body             app
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn preview-args
  [app]
  (client/post (apps-url "apps" "arg-preview")
               {:query-params     (secured-params)
                :body             app
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn list-app-ids
  []
  (client/get (apps-url "apps" "ids")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn delete-apps
  [deletion-request]
  (client/post (apps-url "apps" "shredder")
               {:query-params     (secured-params)
                :body             deletion-request
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn list-permissions
  [body]
  (client/post (apps-url "apps" "permission-lister")
               {:query-params     (secured-params)
                :body             body
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn share
  [body]
  (client/post (apps-url "apps" "sharing")
               {:query-params     (secured-params)
                :body             body
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn unshare
  [body]
  (client/post (apps-url "apps" "unsharing")
               {:query-params     (secured-params)
                :body             body
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn get-app
  [app-id]
  (client/get (apps-url "apps" app-id)
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn delete-app
  [app-id]
  (client/delete (apps-url "apps" app-id)
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn relabel-app
  [app-id relabel-request]
  (client/patch (apps-url "apps" app-id)
                {:query-params     (secured-params)
                 :body             relabel-request
                 :content-type     :json
                 :as               :stream
                 :follow-redirects false}))

(defn update-app
  [app-id update-request]
  (client/put (apps-url "apps" app-id)
              {:query-params     (secured-params)
               :body             update-request
               :content-type     :json
               :as               :stream
               :follow-redirects false}))

(defn copy-app
  [app-id]
  (client/post (apps-url "apps" app-id "copy")
               {:query-params     (secured-params)
                :as               :stream
                :follow-redirects false}))

(defn get-app-details
  [app-id]
  (client/get (apps-url "apps" app-id "details")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn remove-favorite-app
  [app-id]
  (client/delete (apps-url "apps" app-id "favorite")
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn add-favorite-app
  [app-id]
  (client/put (apps-url "apps" app-id "favorite")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn app-publishable?
  [app-id]
  (client/get (apps-url "apps" app-id "is-publishable")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn make-app-public
  [app-id app]
  (client/post (apps-url "apps" app-id "publish")
               {:query-params     (secured-params)
                :body             app
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn delete-rating
  [app-id]
  (client/delete (apps-url "apps" app-id "rating")
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn rate-app
  [app-id rating]
  (client/post (apps-url "apps" app-id "rating")
               {:query-params     (secured-params)
                :body             rating
                :content-type     :json
                :as               :stream
                :follow-redirects false}))

(defn list-app-tasks
  [app-id]
  (client/get (apps-url "apps" app-id "tasks")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn get-app-ui
  [app-id]
  (client/get (apps-url "apps" app-id "ui")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn add-pipeline
  [pipeline]
  (client/post (apps-url "apps" "pipelines")
               {:query-params     (secured-params)
                :content-type     :json
                :body             pipeline
                :as               :stream
                :follow-redirects false}))

(defn update-pipeline
  [app-id pipeline]
  (client/put (apps-url "apps" "pipelines" app-id)
              {:query-params     (secured-params)
               :content-type     :json
               :body             pipeline
               :as               :stream
               :follow-redirects false}))

(defn copy-pipeline
  [app-id]
  (client/post (apps-url "apps" "pipelines" app-id "copy")
               {:query-params     (secured-params)
                :as               :stream
                :follow-redirects false}))

(defn edit-pipeline
  [app-id]
  (client/get (apps-url "apps" "pipelines" app-id "ui")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn list-jobs
  [params]
  (client/get (apps-url "analyses")
              {:query-params     (secured-params params apps-analysis-listing-params)
               :as               :stream
               :follow-redirects false}))

(defn list-job-permissions
  [body]
  (client/post (apps-url "analyses" "permission-lister")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn share-jobs
  [body]
  (client/post (apps-url "analyses" "sharing")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn unshare-jobs
  [body]
  (client/post (apps-url "analyses" "unsharing")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn submit-job
  [submission]
  (client/post (apps-url "analyses")
               {:query-params     (secured-params)
                :content-type     :json
                :body             submission
                :as               :stream
                :follow-redirects false}))

(defn update-job
  [analysis-id body]
  (client/patch (apps-url "analyses" analysis-id)
                {:query-params     (secured-params)
                 :content-type     :json
                 :body             body
                 :as               :stream
                 :follow-redirects false}))

(defn delete-job
  [analysis-id]
  (client/delete (apps-url "analyses" analysis-id)
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn delete-jobs
  [body]
  (client/post (apps-url "analyses" "shredder")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn get-job-params
  [analysis-id]
  (client/get (apps-url "analyses" analysis-id "parameters")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn get-job-relaunch-info
  [analysis-id]
  (client/get (apps-url "analyses" analysis-id "relaunch-info")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn list-job-steps
  [analysis-id]
  (client/get (apps-url "analyses" analysis-id "steps")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn stop-job
  [analysis-id]
  (client/post (apps-url "analyses" analysis-id "stop")
               {:query-params     (secured-params)
                :as               :stream
                :follow-redirects false}))

(defn categorize-apps
  [body]
  (client/post (apps-url "admin" "apps")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn permanently-delete-apps
  [body]
  (client/post (apps-url "admin" "apps" "shredder")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn admin-delete-app
  [app-id]
  (client/delete (apps-url "admin" "apps" app-id)
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn admin-update-app
  [app-id body]
  (client/patch (apps-url "admin" "apps" app-id)
                {:query-params     (secured-params)
                 :content-type     :json
                 :body             body
                 :as               :stream
                 :follow-redirects false}))

(defn get-admin-app-categories
  [params]
  (client/get (apps-url "admin" "apps" "categories")
              {:query-params     (secured-params params apps-sort-params)
               :as               :stream
               :follow-redirects false}))

(defn add-category
  [body]
  (client/post (apps-url "admin" "apps" "categories")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn delete-categories
  [body]
  (client/post (apps-url "admin" "apps" "categories" "shredder")
               {:query-params     (secured-params)
                :content-type     :json
                :body             body
                :as               :stream
                :follow-redirects false}))

(defn delete-category
  [category-id]
  (client/delete (apps-url "admin" "apps" "categories" category-id)
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn update-category
  [category-id body]
  (client/patch (apps-url "admin" "apps" "categories" category-id)
                {:query-params     (secured-params)
                 :content-type     :json
                 :body             body
                 :as               :stream
                 :follow-redirects false}))

(defn get-app-docs
  [app-id]
  (client/get (apps-url "apps" app-id "documentation")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn edit-app-docs
  [app-id docs]
  (client/patch (apps-url "apps" app-id "documentation")
                {:query-params     (secured-params)
                 :content-type     :json
                 :body             docs
                 :as               :stream
                 :follow-redirects false}))

(defn add-app-docs
  [app-id docs]
  (client/post (apps-url "apps" app-id "documentation")
               {:query-params     (secured-params)
                :content-type     :json
                :body             docs
                :as               :stream
                :follow-redirects false}))

(defn admin-edit-app-docs
  [app-id docs]
  (client/patch (apps-url "admin" "apps" app-id "documentation")
                {:query-params     (secured-params)
                 :content-type     :json
                 :body             docs
                 :as               :stream
                 :follow-redirects false}))

(defn admin-add-app-docs
  [app-id docs]
  (client/post (apps-url "admin" "apps" app-id "documentation")
               {:query-params     (secured-params)
                :content-type     :json
                :body             docs
                :as               :stream
                :follow-redirects false}))


(defn get-oauth-access-token
  [api-name params]
  (client/get (apps-url "oauth" "access-code" api-name)
              {:query-params     (secured-params params [:code :state])
               :as               :stream
               :follow-redirects false}))

(defn admin-list-tool-requests
  [params]
  (client/get (apps-url "admin" "tool-requests")
              {:query-params     (secured-params params (conj apps-sort-params :status))
               :as               :stream
               :follow-redirects false}))

(defn list-tool-request-status-codes
  [params]
  (client/get (apps-url "tool-requests" "status-codes")
              {:query-params     (secured-params params [:filter])
               :as               :stream
               :follow-redirects false}))

(defn get-tools-in-app
  [app-id]
  (client/get (apps-url "apps" app-id "tools")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn import-tools
  [body]
  (client/post (apps-url "admin" "tools")
               {:query-params     (secured-params)
                :as               :stream
                :body             body
                :content-type     :json
                :follow-redirects false}))

(defn delete-tool
  [tool-id]
  (client/delete (apps-url "admin" "tools" tool-id)
                 {:query-params     (secured-params)
                  :as               :stream
                  :follow-redirects false}))

(defn update-tool
  [tool-id params tool]
  (client/patch (apps-url "admin" "tools" tool-id)
                {:query-params     (secured-params params [:overwrite-public])
                 :as               :stream
                 :body             tool
                 :content-type     :json
                 :follow-redirects false}))

(defn search-tools
  [params]
  (client/get (apps-url "tools")
              {:query-params     (secured-params params [:search :include-hidden])
               :as               :stream
               :follow-redirects :false}))

(defn get-tool
  [tool-id]
  (client/get (apps-url "tools" tool-id)
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn list-reference-genomes
  [params]
  (client/get (apps-url "reference-genomes")
              {:query-params     (secured-params params [:deleted])
               :as               :stream
               :follow-redirects false}))

(defn get-reference-genome
  [reference-genome-id]
  (client/get (apps-url "reference-genomes" reference-genome-id)
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn get-workspace
  []
  (client/get (apps-url "workspaces")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn get-collaborators
  []
  (client/get (apps-url "collaborators")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn add-collaborators
  [body]
  (client/post (apps-url "collaborators")
               {:query-params     (secured-params)
                :as               :stream
                :body             body
                :content-type     :json
                :follow-redirects false}))

(defn remove-collaborators
  [body]
  (client/post (apps-url "collaborators" "shredder")
               {:query-params     (secured-params)
                :as               :stream
                :body             body
                :content-type     :json
                :follow-redirects false}))

(defn get-users-by-id
  [body]
  (client/post (apps-url "users" "by-id")
               {:query-params     (secured-params)
                :as               :stream
                :body             body
                :content-type     :json
                :follow-redirects false}))

(defn get-authenticated-user
  []
  (client/get (apps-url "users" "authenticated")
              {:query-params     (secured-params)
               :as               :stream
               :follow-redirects false}))

(defn record-login
  [ip-address user-agent]
  (let [params {:ip-address ip-address :user-agent user-agent}]
    (client/post (apps-url "users" "login")
                 {:query-params     (secured-params params)
                  :as               :stream
                  :follow-redirects false})))

(defn record-logout
  [ip-address login-time]
  (let [params {:ip-address ip-address :login-time login-time}]
    (client/post (apps-url "users" "logout")
                 {:query-params     (secured-params params)
                  :as               :stream
                  :follow-redirects false})))
