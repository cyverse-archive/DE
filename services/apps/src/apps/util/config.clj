(ns apps.util.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [cemerick.url :as curl]
            [cheshire.core :as cheshire]
            [clojure-commons.config :as cc]
            [clojure.tools.logging :as log]
            [common-cfg.cfg :as cfg]))

(def docs-uri "/docs")

(def svc-info
  {:desc "Framework for hosting DiscoveryEnvironment metadata services."
   :app-name "apps"
   :group-id "org.iplantc"
   :art-id "apps"
   :service "apps"})

(def ^:private props
  "A ref for storing the configuration properties."
  (ref nil))

(def ^:private config-valid
  "A ref for storing a configuration validity flag."
  (ref true))

(def ^:private configs
  "A ref for storing the symbols used to get configuration settings."
  (ref []))

(cc/defprop-int listen-port
  "The port that apps listens to."
  [props config-valid configs]
  "apps.app.listen-port")

(cc/defprop-str env-name
  "The name of the DE deployment environment."
  [props config-valid configs]
  "apps.app.environment-name")

(cc/defprop-optboolean agave-enabled
  "Enables or disables all features that require connections to Agave."
  [props config-valid configs]
  "apps.features.agave" true)

(cc/defprop-optboolean agave-jobs-enabled
  "Enables or disables Agave job submission."
  [props config-valid configs]
  "apps.features.agave.jobs" false)

(cc/defprop-str db-driver-class
  "The name of the JDBC driver to use."
  [props config-valid configs]
  "apps.db.driver" )

(cc/defprop-str db-subprotocol
  "The subprotocol to use when connecting to the database (e.g.
   postgresql)."
  [props config-valid configs]
  "apps.db.subprotocol")

(cc/defprop-str db-host
  "The host name or IP address to use when
   connecting to the database."
  [props config-valid configs]
  "apps.db.host")

(cc/defprop-str db-port
  "The port number to use when connecting to the database."
  [props config-valid configs]
  "apps.db.port")

(cc/defprop-str db-name
  "The name of the database to connect to."
  [props config-valid configs]
  "apps.db.name")

(cc/defprop-str db-user
  "The username to use when authenticating to the database."
  [props config-valid configs]
  "apps.db.user")

(cc/defprop-str db-password
  "The password to use when authenticating to the database."
  [props config-valid configs]
  "apps.db.password")

(cc/defprop-str jex-base-url
  "The base URL to use when connecting to the JEX."
  [props config-valid configs]
  "apps.jex.base-url")

(cc/defprop-str data-info-base-url
  "The base URL to use when connecting to the JEX."
  [props config-valid configs]
  "apps.data-info.base-url")

(cc/defprop-str workspace-root-app-category
  "The name of the root app category in a user's workspace."
  [props config-valid configs]
  "apps.workspace.root-app-category")

(cc/defprop-str workspace-default-app-categories
  "The names of the app categories immediately under the root app category in a user's workspace."
  [props config-valid configs]
  "apps.workspace.default-app-categories")

(cc/defprop-int workspace-dev-app-category-index
  "The index of the category within a user's workspace for apps under
   development."
  [props config-valid configs]
  "apps.workspace.dev-app-category-index")

(cc/defprop-int workspace-favorites-app-category-index
  "The index of the category within a user's workspace for favorite apps."
  [props config-valid configs]
  "apps.workspace.favorites-app-category-index")

(cc/defprop-uuid workspace-beta-app-category-id
  "The UUID of the default Beta app category."
  [props config-valid configs]
  "apps.workspace.beta-app-category-id")

(cc/defprop-uuid workspace-public-id
  "The UUID of the default Beta app category."
  [props config-valid configs]
  "apps.workspace.public-id")

(cc/defprop-str uid-domain
  "The domain name to append to the user identifier to get the fully qualified
   user identifier."
  [props config-valid configs]
  "apps.uid.domain")

(cc/defprop-str irods-home
  "The path to the home directory in iRODS."
  [props config-valid configs]
  "apps.irods.home")

(cc/defprop-str jex-batch-group-name
  "The group name to submit to the JEX for batch jobs."
  [props config-valid configs]
  "apps.batch.group")

(cc/defprop-str path-list-info-type
  "The info type for HT Analysis Path Lists."
  [props config-valid configs]
  "apps.batch.path-list.info-type")

(cc/defprop-int path-list-max-paths
  "The maximum number of paths to process per HT Analysis Path Lists."
  [props config-valid configs]
  "apps.batch.path-list.max-paths")

(cc/defprop-int path-list-max-size
  "The maximum size of each HT Analysis Path List that can be fetched from the data-info service."
  [props config-valid configs]
  "apps.batch.path-list.max-size")

(cc/defprop-str agave-base-url
  "The base URL to use when connecting to Agave."
  [props config-valid configs agave-enabled]
  "apps.agave.base-url")

(cc/defprop-str agave-key
  "The API key to use when authenticating to Agave."
  [props config-valid configs agave-enabled]
  "apps.agave.key")

(cc/defprop-str agave-secret
  "The API secret to use when authenticating to Agave."
  [props config-valid configs agave-enabled]
  "apps.agave.secret")

(cc/defprop-str agave-oauth-base
  "The base URL for the Agave OAuth 2.0 endpoints."
  [props config-valid configs agave-enabled]
  "apps.agave.oauth-base")

(cc/defprop-int agave-oauth-refresh-window
  "The number of minutes before a token expires to refresh it."
  [props config-valid configs agave-enabled]
  "apps.agave.oauth-refresh-window")

(cc/defprop-str agave-redirect-uri
  "The redirect URI used after Agave authorization."
  [props config-valid configs agave-enabled]
  "apps.agave.redirect-uri")

(cc/defprop-str agave-callback-base
  "The base URL for receiving job status update callbacks from Agave."
  [props config-valid configs agave-enabled]
  "apps.agave.callback-base")

(cc/defprop-optstr agave-storage-system
  "The storage system that Agave should use when interacting with the DE."
  [props config-valid configs agave-enabled]
  "apps.agave.storage-system"
  "data.iplantcollaborative.org")

(cc/defprop-int agave-read-timeout
  "The maximum amount of time to wait for a response from Agave in milliseconds."
  [props config-valid configs agave-enabled]
  "apps.agave.read-timeout")

(cc/defprop-int agave-page-length
  "The maximum number of entities to receive from a single Agave service call."
  [props config-valid configs agave-enabled]
  "apps.agave.page-length")

(cc/defprop-str pgp-keyring-path
  "The path to the PGP keyring file."
  [props config-valid configs agave-enabled]
  "apps.pgp.keyring-path")

(cc/defprop-str pgp-key-password
  "The password used to unlock the PGP key."
  [props config-valid configs agave-enabled]
  "apps.pgp.key-password")

(def data-info-base
  (memoize
   (fn []
     (if (System/getenv "DATA_INFO_PORT")
       (cfg/env-setting "DATA_INFO_PORT")
       (data-info-base-url)))))

(cc/defprop-str notification-agent-base
  "The base URL for the notification agent."
  [props config-valid configs]
  "apps.notificationagent.base-url")

(cc/defprop-str ipg-base
  "The base URL for the iplant-groups service."
  [props config-valid configs]
  "apps.iplant-groups.base-url")

(cc/defprop-str de-grouper-user
  "The username that the DE uses to authenticate to Grouper."
  [props config-valid configs]
  "apps.iplant-groups.grouper-user")

(cc/defprop-str metadata-base
  "The base URL for the metadata service."
  [props config-valid configs]
  "apps.metadata.base-url")

(cc/defprop-int job-status-poll-interval
  "The job status polling interval in minutes."
  [props config-valid configs]
  "apps.jobs.poll-interval")

(def get-default-app-categories
  (memoize
   (fn []
     (cheshire/decode (workspace-default-app-categories) true))))

(defn- oauth-settings
  [api-name api-key api-secret auth-uri token-uri redirect-uri refresh-window]
  {:api-name       api-name
   :client-key     api-key
   :client-secret  api-secret
   :auth-uri       auth-uri
   :token-uri      token-uri
   :redirect-uri   redirect-uri
   :refresh-window (* refresh-window 60 1000)})

(def agave-oauth-settings
  (memoize
   #(oauth-settings
     "agave"
     (agave-key)
     (agave-secret)
     (str (curl/url (agave-oauth-base) "authorize"))
     (str (curl/url (agave-oauth-base) "token"))
     (agave-redirect-uri)
     (agave-oauth-refresh-window))))

(defn log-environment
  []
  (log/warn "ENV?: apps.data-info.base-url = " (data-info-base)))

(defn- validate-config
  "Validates the configuration settings after they've been loaded."
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:type :clojure-commons.exception/invalid-configuration})))

(defn load-config-from-file
  "Loads the configuration settings from a file."
  [cfg-path]
  (cc/load-config-from-file cfg-path props)
  (cc/log-config props)
  (log-environment)
  (validate-config))
