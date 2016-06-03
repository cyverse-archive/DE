(ns permissions-client.integration-test
  (:require [permissions-client.core :as pc])
  (:use [clojure.test]))

(def ^:dynamic base-uri "http://permissions:60000/")

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (f)))

(defn with-base-uri [f]
  (if-let [uri (System/getenv "PERMISSIONS_BASE_URI")]
    (binding [base-uri uri]
      (f))
    (f)))

(use-fixtures :once run-integration-tests with-base-uri)

(defn create-permissions-client []
  (pc/new-permissions-client base-uri))

(deftest test-get-status
  (let [status-info (pc/get-status (create-permissions-client))]
    (is (:version status-info))
    (is (:service status-info))
    (is (:description status-info))))
