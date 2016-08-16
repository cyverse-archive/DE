(ns metadata.persistence.ontologies
  (:use [korma.core :exclude [update]]
        [kameleon.util.search])
  (:require [korma.core :as sql]))

(defn add-ontology-xml
  [user version iri ontology-xml]
  (insert :ontologies (values {:version    version
                               :iri        iri
                               :xml        ontology-xml
                               :created_by user})))

(defn get-ontology-xml
  [ontology-version]
  ((comp :xml first)
   (select :ontologies
           (fields :xml)
           (where {:version ontology-version}))))

(defn get-ontology-details
  [ontology-version]
  (first (select :ontologies
                 (fields :version
                         :iri
                         :created_by
                         :created_on)
                 (where {:version ontology-version}))))

(defn set-ontology-deleted
  [ontology-version deleted]
  (sql/update :ontologies
              (set-fields {:deleted deleted})
              (where {:version ontology-version})))

(defn list-ontologies
  []
  (select :ontologies
          (fields :version
                  :iri
                  :created_by
                  :created_on)
          (where {:deleted false})))

(defn add-classes
  [ontology-version classes]
  (let [class-values (map #(assoc % :ontology_version ontology-version) classes)]
    (when-not (empty? class-values)
      (insert :ontology_classes (values class-values)))))

(defn get-classes
  [ontology-version]
  (select :ontology_classes
          (fields :iri
                  :label
                  :description)
          (where {:ontology_version ontology-version})))

(defn- search-classes-base
  [ontology-version search-term]
  (let [search-term (str "%" (format-query-wildcards search-term) "%")]
    (-> (select* :ontology_classes)
        (where {:ontology_version    ontology-version
                (sqlfn lower :label) [like (sqlfn lower search-term)]}))))

(defn search-classes-subselect
  [ontology-version search-term]
  (-> (search-classes-base ontology-version search-term)
      (subselect (fields :iri))))

(defn delete-classes
  [ontology-version class-iris]
  (delete :ontology_classes
          (where {:ontology_version ontology-version
                  :iri              [in class-iris]})))

(defn add-hierarchies
  [ontology-version class-subclass-pairs]
  (when-not (empty? class-subclass-pairs)
    (let [hierarchy-values (map #(assoc % :ontology_version ontology-version) class-subclass-pairs)]
      (insert :ontology_hierarchies (values hierarchy-values)))))

(defn get-ontology-hierarchy-pairs
  [ontology-version]
  (select :ontology_hierarchies
          (fields :class_iri :subclass_iri)
          (where {:ontology_version ontology-version})))

(defn get-ontology-hierarchy-roots
  [ontology-version]
  (select :ontology_hierarchies
          (modifier "DISTINCT")
          (fields :class_iri)
          (where {:ontology_version ontology-version
                  :class_iri [not-in (subselect :ontology_hierarchies
                                                (fields :subclass_iri)
                                                (where {:ontology_version ontology-version}))]})))

(defn get-ontology-class-hierarchy
  "Gets the class hierarchy rooted at the class with the given IRI."
  [ontology-version root-iri]
  (select (sqlfn :ontology_class_hierarchy ontology-version root-iri)))
