(ns apps.test-fixtures
  (:use [korma.db :only [create-db default-connection]])
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(def default-config-path "/etc/iplant/de/apps.properties")
(def default-db-uri "jdbc:postgresql://dedb/de?user=de&password=notprod")
(def default-jex-uri "https://jex-adapter:60000")
(def default-data-info-uri "https://data-info:60000")
(def default-notification-agent-uri "http://notification-agent:60000")
(def default-iplant-groups-uri "http://iplant-groups:60000")
(def default-metadata-uri "http://metadata:60000")
(def default-permissions-uri "http://permissions:60000")

(def ^:dynamic jex-uri nil)
(def ^:dynamic data-info-uri nil)
(def ^:dynamic notification-agent-uri nil)
(def ^:dynamic iplant-groups-uri nil)
(def ^:dynamic metadata-uri nil)
(def ^:dynamic permissions-uri nil)

(defn getenv [name default]
  (or (System/getenv name) default))

(defn with-config [f]
  (let [config-path (getenv "APPS_CONFIG_PATH" default-config-path)]
    (require 'apps.util.config :reload)
    (apps.util.config/load-config-from-file config-path {:log-config? false})
    (f)))

(defn with-service-uris [f]
  (binding [jex-uri                (getenv "JEX_URI" default-jex-uri)
            data-info-uri          (getenv "DATA_INFO_URI" default-data-info-uri)
            notification-agent-uri (getenv "NOTIFICATION_AGENT_URI" default-notification-agent-uri)
            iplant-groups-uri      (getenv "IPLANT_GROUPS_URI" default-iplant-groups-uri)
            metadata-uri           (getenv "METADATA_URI" default-metadata-uri)
            permissions-uri        (getenv "PERMISSIONS_URI" default-permissions-uri)]
    (f)))

(defn with-test-db [f]
  (default-connection (create-db {:connection-uri (or (System/getenv "DBURI") default-db-uri)}))
  (f))

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (f)))
