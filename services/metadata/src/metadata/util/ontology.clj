(ns metadata.util.ontology
  (:require [clojure.java.io :as io]
            [clojure.set :as sets])
  (:import [clojure.lang APersistentSet]
           [org.semanticweb.owlapi.model IRI OWLClass OWLDataFactory OWLOntology]
           [org.semanticweb.owlapi.apibinding OWLManager]
           [org.semanticweb.owlapi.reasoner OWLReasoner]
           [org.semanticweb.owlapi.reasoner.structural StructuralReasonerFactory]))

(def ^:private version-iri "http://usefulinc.com/ns/doap#Version")
(def ^:private description-iri "http://www.geneontology.org/formats/oboInOwl#hasDefinition")

(defn- get-class
  [^OWLDataFactory data-factory ^String iri]
  (.getOWLClass data-factory (IRI/create iri)))

(defn- get-class-label
  "Returns the label of the given class."
  [^OWLOntology ontology ^OWLClass owl-class]
  (let [label-annotation (first (filter #(-> % (.getProperty) (.isLabel))
                                        (.getAnnotations owl-class ontology)))]
    (when label-annotation
      (-> label-annotation (.getValue) (.getLiteral)))))

(defn- find-annotation-value
  "Returns the literal value of the first annotation that matches the given IRI."
  [annotations iri]
  (let [annotation (first (filter #(= iri (-> % (.getProperty) (.getIRI) (.toString)))
                                  annotations))]
    (when annotation
      (-> annotation (.getValue) (.getLiteral)))))

(defn- get-hierarchy
  [^OWLReasoner reasoner ^OWLOntology ontology ^OWLClass owl-class]
  (when (.isSatisfiable reasoner owl-class)
    (let [class-map {:label (get-class-label ontology owl-class)
                     :iri (.toString (.getIRI owl-class))
                     :description (find-annotation-value (.getAnnotations owl-class ontology) description-iri)}
          subclasses (remove nil? (map (partial get-hierarchy reasoner ontology)
                                       (.getFlattened (.getSubClasses reasoner owl-class true))))]
      (if (empty? subclasses)
        class-map
        (assoc class-map :subclasses subclasses)))))

(defn build-hierarchy
  [^OWLOntology ontology ^String root-iri]
  (let [manager         (.getOWLOntologyManager ontology)
        reasonerFactory (StructuralReasonerFactory.)
        reasoner        (.createNonBufferingReasoner reasonerFactory ontology)]
    (get-hierarchy reasoner ontology (get-class (.getOWLDataFactory manager) root-iri))))

(defn parse-ontology-xml
  [^String ontology-xml]
  (let [input-stream (io/input-stream (.getBytes ontology-xml))]
    (.loadOntologyFromOntologyDocument (OWLManager/createOWLOntologyManager) input-stream)))

(defn format-ontology-id
  [^OWLOntology ontology]
  (let [ontology-id (.getOntologyID ontology)]
    {:iri     (some-> ontology-id (.getDefaultDocumentIRI) (.toString))
     :version (or (some-> ontology-id (.getVersionIRI) (.toString))
                  (find-annotation-value (.getAnnotations ontology) version-iri))}))

(defn hierarchy->class-subclass-pairs
  "Returns a set of the [class-iri subclass-iri] pairs found in the given hierarchy."
  ([hierarchy]
   (hierarchy->class-subclass-pairs #{} hierarchy))
  ([pair-set {:keys [iri subclasses]}]
  (apply sets/union pair-set
         (set (map #(vector iri (:iri %)) subclasses))
         (map (partial hierarchy->class-subclass-pairs pair-set) subclasses))))

(defn hierarchy->class-set
  "Returns a set of class maps found in the given hierarchy."
  ([hierarchy]
   (hierarchy->class-set #{} hierarchy))
  ([class-set hierarchy]
  (apply sets/union class-set
         #{(select-keys hierarchy [:iri :label :description])}
         (map (partial hierarchy->class-set class-set) (:subclasses hierarchy)))))

(defn filter-hierarchy
  "Filters the given hierarchy, returning only leaf-classes that have an IRI found in the given iri-set."
  [^APersistentSet iri-set {:keys [iri subclasses] :as hierarchy}]
  (let [filtered-subclasses (remove nil? (map (partial filter-hierarchy iri-set) subclasses))]
    (when (or (seq filtered-subclasses) (contains? iri-set iri))
      (if (empty? filtered-subclasses)
        (dissoc hierarchy :subclasses)
        (assoc hierarchy :subclasses filtered-subclasses)))))
