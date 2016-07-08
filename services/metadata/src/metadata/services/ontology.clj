(ns metadata.services.ontology
  (:use [slingshot.slingshot :only [throw+]]
        [korma.db :only [transaction]])
  (:require [clojure.set :as sets]
            [clojure.string :as string]
            [clj-time.core :as time]
            [clj-time.format :as time-format]
            [clojure.tools.logging :as log]
            [clojure-commons.exception-util :as ex-util]
            [metadata.persistence.avu :as avu-db]
            [metadata.persistence.ontologies :as ont-db]
            [metadata.util.ontology :as util]
            [ring.middleware.multipart-params :as multipart]))

(defn- format-ontology-version
  "Appends the current date/time to the given ontology ID's IRI/version."
  [{:keys [iri version]}]
  (let [date-time (time-format/unparse (time-format/formatter "yyyy/MM/dd/HH-mm-ss.S") (time/now))]
    (string/join "/" [iri version date-time])))

(defn- subclass?
  "Returns true if the given class's parent_iri equals the given iri.
  Prevents add-subclasses infinite loops by checking if iri and parent_iri are also not nil."
  [iri {:keys [parent_iri]}]
  (and iri parent_iri (= iri parent_iri)))

(defn- add-subclasses
  [{:keys [iri] :as class} classes]
  (let [class      (dissoc class :parent_iri)
        subclasses (filter (partial subclass? iri) classes)
        subclasses (map #(add-subclasses % classes) subclasses)]
    (if (empty? subclasses)
      class
      (assoc class :subclasses subclasses))))

(defn- format-hierarchy
  "Formats the ontology class hierarchy rooted at the class with the given IRI."
  [ontology-version root-iri]
  (let [groups (ont-db/get-ontology-class-hierarchy ontology-version root-iri)
        root   (first (filter #(= root-iri (:iri %)) groups))]
    (add-subclasses root groups)))

(defn- multipart-xml-parser-handler
  "Handler for ring.middleware.multipart-params/multipart-params-request which parses an Ontology XML
  document and returns its class hierarchies."
  [{istream :stream :as params}]
  (slurp istream))

(defn wrap-multipart-xml-parser
  "Middleware which parses an Ontology XML document's class hierarchies from a multipart request."
  [handler]
  (fn [{:keys [params] :as request}]
    (handler (multipart/multipart-params-request request {:store multipart-xml-parser-handler}))))

(defn get-ontology-details
  "Fetches the details of a saved Ontology, excluding its XML."
  [ontology-version]
  (ont-db/get-ontology-details ontology-version))

(defn get-ontology-details-listing
  "Fetches a list of Ontology details, excluding their XML."
  []
  {:ontologies (ont-db/list-ontologies)})

(defn save-ontology-xml
  "Saves the given Ontology XML in the database, along with its parsed IRI, and a version based on that
  IRI, the ontology version, and the current date/time."
  [user ontology-xml]
  (let [ontology (util/parse-ontology-xml ontology-xml)
        ontology-id (util/format-ontology-id ontology)
        version (format-ontology-version ontology-id)]
    (ont-db/add-ontology-xml user version (:iri ontology-id) ontology-xml)
    (get-ontology-details version)))

(defn delete-ontology
  "Marks an Ontology as deleted in the database.
   Throws an exception if the ontology-version is not found."
  [user ontology-version]
  (transaction
    (let [ontology (ont-db/get-ontology-details ontology-version)]
      (when-not ontology
        (ex-util/not-found "An ontology with this version was not found."
                           :version ontology-version))
      (log/info user "deleting ontology" ontology-version)
      (ont-db/mark-ontology-deleted ontology-version)))
  nil)

(defn get-hierarchy
  "Gets an Ontology Hierarchy rooted at the given root-iri."
  [ontology-version root-iri]
  {:hierarchy (format-hierarchy ontology-version root-iri)})

(defn list-hierarchies
  "Lists Ontology Hierarchies saved for the given ontology-version."
  [ontology-version]
  (let [roots (ont-db/get-ontology-hierarchy-roots ontology-version)]
    {:hierarchies (map (comp (partial format-hierarchy ontology-version) :class_iri) roots)}))

(defn- filter-new-classes
  [ontology-version hierarchy]
  (let [classes         (util/hierarchy->class-set hierarchy)
        found-class-ids (set (map :iri (ont-db/get-classes ontology-version)))]
    (remove #(contains? found-class-ids (:iri %)) classes)))

(defn- filter-new-hierarchy-pairs
  [ontology-version hierarchy]
  (let [class-subclass-pairs  (util/hierarchy->class-subclass-pairs hierarchy)
        found-hierarchy-pairs (set (ont-db/get-ontology-hierarchy-pairs ontology-version))]
    (sets/difference class-subclass-pairs found-hierarchy-pairs)))

(defn save-hierarchy
  "Adds the given ontology hierarchies to the database."
  [user ontology-version root-iri]
  (transaction
   (let [ontology-xml (ont-db/get-ontology-xml ontology-version)
         ontology (util/parse-ontology-xml ontology-xml)
         hierarchy (util/build-hierarchy ontology root-iri)
         new-classes (filter-new-classes ontology-version hierarchy)
         new-hierarchies (filter-new-hierarchy-pairs ontology-version hierarchy)]
     (log/info user "adding" ontology-version "hierarchy" root-iri)
     (when-not (empty? new-classes)
       (ont-db/add-classes ontology-version new-classes))
     (when-not (empty? new-hierarchies)
       (ont-db/add-hierarchies ontology-version new-hierarchies))))
  (get-hierarchy ontology-version root-iri))

(defn delete-hierarchy
  "Deletes all associated ontology_classes (and ontology_hierarchies by cascade) saved under the given
   `root-iri` for the given `ontology-version`."
  [user ontology-version root-iri]
  (transaction
    (let [hierarchy  (format-hierarchy ontology-version root-iri)
          class-iris (map :iri (util/hierarchy->class-set hierarchy))]
      (log/info user "deleting hierarchy" ontology-version root-iri)
      (when-not (empty? class-iris)
        (ont-db/delete-classes ontology-version class-iris))))
  (list-hierarchies ontology-version))

(defn filter-hierarchy
  "Filters an Ontology Hierarchy, rooted at the given root-iri, returning only the hierarchy's
   leaf-classes that are associated with the given targets."
  [ontology-version root-iri attr target-types target-ids]
  (let [hierarchy (format-hierarchy ontology-version root-iri)
        iri-set (set (map :value (avu-db/get-avus-by-attr target-types target-ids attr)))]
    {:hierarchy (util/filter-hierarchy iri-set hierarchy)}))

(defn filter-unclassified-targets
  "Filters the given target IDs by returning a list of any that are not associated with any Ontology
   classes of the hierarchy rooted at the given root-iri."
  [ontology-version root-iri attr target-types target-ids]
  (let [target-ids (set target-ids)
        hierarchy  (format-hierarchy ontology-version root-iri)
        iri-set    (set (map :iri (util/hierarchy->class-set hierarchy)))
        found-ids  (set (map :target_id (avu-db/filter-targets-by-attr-values target-types
                                                                              target-ids
                                                                              attr
                                                                              iri-set)))]
    {:target-ids (seq (sets/difference target-ids found-ids))}))
