(ns integration-tests
  (:use [clojure.test])
  (:require [metadata.util.config :as config]
            [metadata.util.db :as db]))

(defn- integration-test-setup
  []
  (config/load-config-from-file (System/getenv "TEST_METADATA_CONFIG_PATH"))
  (db/define-database))

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (integration-test-setup)
    (f)))
