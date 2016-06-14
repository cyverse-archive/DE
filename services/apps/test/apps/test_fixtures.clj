(ns apps.test-fixtures
  (:use [korma.db :only [create-db default-connection]])
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]))

(def default-uri "jdbc:postgresql://dedb/de?user=de&password=notprod")

(defn with-test-db [f]
  (default-connection (create-db {:connection-uri (or (System/getenv "DBURI") default-uri)}))
  (f))

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (f)))
