(ns apps.test-fixtures
  (:use [korma.db :only [create-db default-connection]])
  (:require [apps.util.config :as config]
            [apps.user :as user]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [metadata-client.core :as metadata-client]))

(def default-config-path "/etc/iplant/de/apps.properties")
(def default-db-uri "jdbc:postgresql://dedb/de?user=de&password=notprod")

(defn getenv [name default]
  (or (System/getenv name) default))

(defn with-config [f]
  (let [config-path (getenv "APPS_CONFIG_PATH" default-config-path)]
    (require 'apps.util.config :reload)
    (apps.util.config/load-config-from-file config-path {:log-config? false})
    (metadata-client/with-metadata-base (config/metadata-base) (f))))

(defn with-test-db [f]
  (default-connection (create-db {:connection-uri (or (System/getenv "DBURI") default-db-uri)}))
  (f))

(defn with-test-user [f]
  (user/with-user [{:user       "ipctest"
                    :email      "ipctest@cyverse.org"
                    :first-name "IPC"
                    :last-name  "Test"}]
                  (f)))

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (f)))
