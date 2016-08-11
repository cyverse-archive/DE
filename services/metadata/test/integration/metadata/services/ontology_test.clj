(ns integration.metadata.services.ontology-test
  (:use [clojure.test]
        [korma.core :exclude [update]]
        [integration]
        [metadata.services.ontology])
  (:require [metadata.persistence.ontologies :as db]))

(def ^:dynamic test-ontology nil)
(def test-topic-iri "http://edamontology.org/topic_3511")
(def test-operation-iri "http://edamontology.org/operation_2422")

(defn- create-test-ontology
  []
  (let [test-xml (System/getenv "TEST_EDAM_ONTOLOGY_XML")]
    (when test-xml
      (save-ontology-xml test-username (slurp test-xml)))))

(defn- permanently-delete-ontology
  [version]
  (delete :ontologies (where {:version version})))

(defn with-test-xml
  [f]
  (binding [test-ontology (create-test-ontology)]
    (when test-ontology
      (f)
      (permanently-delete-ontology (:version test-ontology)))))


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


(deftest test-add-ontology
  (testing "Adding ontologies"
    (let [test-ontology-2 (create-test-ontology)]
      (is (<= 2 (count (:ontologies (get-ontology-details-listing)))))
      (is (not= (:version test-ontology) (:version test-ontology-2)))
      (permanently-delete-ontology (:version test-ontology-2)))))


(deftest test-save-hierarchies
  (testing "Saving and Removing ontology hierarchies"
    (is (empty? (-> test-ontology :version list-hierarchies :hierarchies)))

    (let [topic-hierarchy (save-hierarchy test-username (:version test-ontology) test-topic-iri)]
      (is (= test-topic-iri (-> topic-hierarchy :hierarchy :iri)))
      (is (< 0 (count (-> topic-hierarchy :hierarchy :subclasses))))
      (is (= topic-hierarchy (get-hierarchy (:version test-ontology) test-topic-iri))))

    (save-hierarchy test-username (:version test-ontology) test-operation-iri)
    (is (< 0 (count (-> (get-hierarchy (:version test-ontology) test-operation-iri)
                        :hierarchy
                        :subclasses))))

    (is (= 2 (count (-> test-ontology :version list-hierarchies :hierarchies))))

    (delete-hierarchy test-username (:version test-ontology) test-topic-iri)
    (is (empty? (:hierarchy (get-hierarchy (:version test-ontology) test-topic-iri))))

    (delete-hierarchy test-username (:version test-ontology) test-operation-iri)
    (is (empty? (:hierarchy (get-hierarchy (:version test-ontology) test-operation-iri))))

    (is (empty? (-> test-ontology :version list-hierarchies :hierarchies)))))
