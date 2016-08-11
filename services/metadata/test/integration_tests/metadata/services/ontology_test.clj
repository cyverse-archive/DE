(ns integration-tests.metadata.services.ontology-test
  (:use [clojure.test]
        [korma.core :exclude [update]]
        [integration-tests]
        [metadata.services.ontology])
  (:require [metadata.persistence.ontologies :as db]))

(def ^:dynamic test-ontology nil)

(defn- create-test-ontology
  []
  (let [test-xml (System/getenv "TEST_EDAM_ONTOLOGY_XML")]
    (when test-xml
      (save-ontology-xml test-username (slurp test-xml)))))

(defn with-test-xml
  [f]
  (binding [test-ontology (create-test-ontology)]
    (when test-ontology
      (f)
      (delete :ontologies (where (select-keys test-ontology [:version]))))))

;; Only run these tests in an integration test environment
(use-fixtures :once run-integration-tests with-test-xml)

(defn- filter-ontology-details
  [ontology-version ontologies]
  (filter #(= (:version %) ontology-version) ontologies))

(deftest test-delete-ontology
  (testing "Mark ontology deleted"
    (is (not (empty? (filter-ontology-details
                       (:version test-ontology)
                       (:ontologies (get-ontology-details-listing))))))

    (delete-ontology test-username (:version test-ontology))
    (is (empty? (filter-ontology-details
                  (:version test-ontology)
                  (:ontologies (get-ontology-details-listing)))))

    (db/set-ontology-deleted (:version test-ontology) false)
    (is (not (empty? (filter-ontology-details
                       (:version test-ontology)
                       (:ontologies (get-ontology-details-listing))))))))
