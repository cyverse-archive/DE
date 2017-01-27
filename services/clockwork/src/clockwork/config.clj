(ns clockwork.config
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.config :as cc]
            [clojure-commons.error-codes :as ce]))

(def ^:private props
  "A ref for storing the configuration properties."
  (ref nil))

(def ^:private config-valid
  "A ref for storing a configuration validity flag."
  (ref true))

(def ^:private configs
  "A ref for storing the symbols used to get configuration settings."
  (ref []))

(cc/defprop-str irods-host
  "The host name or IP address to use when connecting to iRODS."
  [props config-valid configs]
  "clockwork.irods-host")

(cc/defprop-str irods-port
  "The port number to use when connecting to iRODS."
  [props config-valid configs]
  "clockwork.irods-port")

(cc/defprop-str irods-user
  "The username to use when authenticating to iRODS."
  [props config-valid configs]
  "clockwork.irods-user")

(cc/defprop-str irods-password
  "The password t use when authenticating to iRODS."
  [props config-valid configs]
  "clockwork.irods-password")

(cc/defprop-str irods-home
  "The base path to the directory containing the home directories in iRODS."
  [props config-valid configs]
  "clockwork.irods-home")

(cc/defprop-str irods-zone
  "The name of the iRODS zone."
  [props config-valid configs]
  "clockwork.irods-zone")

(cc/defprop-optstr irods-resource
  "The name of the default resource to use in iRODS."
  [props config-valid configs]
  "clockwork.irods-resource")

(cc/defprop-str notification-cleanup-start
  "The start time for the notification cleanup job."
  [props config-valid configs]
  "clockwork.notifications.cleanup-start")

(cc/defprop-int notification-cleanup-age
  "The minimum age of a notification in days before it's eligible for cleanup."
  [props config-valid configs]
  "clockwork.notifications.cleanup-age")

(cc/defprop-optboolean notification-cleanup-enabled
  "Indicates whether notification cleanup tasks are enabled."
  [props config-valid configs]
  "clockwork.notifications.cleanup-enable")

(cc/defprop-str notification-db-driver-class
  "The name of the JDBC driver to use."
  [props config-valid configs]
  "clockwork.notifications.db.driver" )

(cc/defprop-str notification-db-subprotocol
  "The subprotocol to use when connecting to the database (e.g. postgresql)."
  [props config-valid configs]
  "clockwork.notifications.db.subprotocol")

(cc/defprop-str notification-db-host
  "The host name or IP address to use when connecting to the database."
  [props config-valid configs]
  "clockwork.notifications.db.host")

(cc/defprop-str notification-db-port
  "The port number to use when connecting to the database."
  [props config-valid configs]
  "clockwork.notifications.db.port")

(cc/defprop-str notification-db-name
  "The name of the database to connect to."
  [props config-valid configs]
  "clockwork.notifications.db.name")

(cc/defprop-str notification-db-user
  "The username to use when authenticating to the database."
  [props config-valid configs]
  "clockwork.notifications.db.user")

(cc/defprop-str notification-db-password
  "The password to use when authenticating to the database."
  [props config-valid configs]
  "clockwork.notifications.db.password")

(defn- validate-config
  "Validates the configuration settings after they've been loaded."
  []
  (when-not (cc/validate-config configs config-valid)
    (throw+ {:error_code ce/ERR_CONFIG_INVALID})))

(defn load-config-from-file
  "Loads the configuration settings from a file."
  [cfg-path]
  (cc/load-config-from-file cfg-path props)
  (cc/log-config props)
  (validate-config))
