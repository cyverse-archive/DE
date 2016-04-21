(ns metadata.persistence.ontologies
  (:use [korma.core :exclude [update]]))

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

(defn list-ontologies
  []
  (select :ontologies
          (fields :version
                  :iri
                  :created_by
                  :created_on)))

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

(defn- format-class-subclass-pair
  [ontology-version [class-iri subclass-iri]]
  {:ontology_version ontology-version
   :class_iri        class-iri
   :subclass_iri     subclass-iri})

(defn add-hierarchies
  [ontology-version class-subclass-pairs]
  (when-not (empty? class-subclass-pairs)
    (let [hierarchy-values (map (partial format-class-subclass-pair ontology-version) class-subclass-pairs)]
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
