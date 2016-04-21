(ns metadata.routes.domain.ontologies
  (:use [common-swagger-api.schema]
        [metadata.routes.domain.common])
  (:require [schema.core :as s])
  (:import [java.util Date UUID]))

(def OntologyVersionParam (describe String "The unique version of the Ontology"))
(def OntologyHierarchyRootParam (describe String "The Class IRI with which to root the hierarchy"))

(s/defschema OntologyDetails
  {:iri        (describe String "The unique IRI for this Ontology")
   :version    OntologyVersionParam
   :created_by (describe NonBlankString "The user who uploaded this Ontology")
   :created_on (describe Date "The date this Ontology was uploaded")})

(s/defschema OntologyDetailsList
  {:ontologies (describe [OntologyDetails] "List of saved Ontologies")})

(s/defschema OntologyClass
  {:iri
   (describe String "The unique IRI for this Ontology Class")

   :label
   (describe (s/maybe String) "The label annotation of this Ontology Class")

   (s/optional-key :description)
   (describe (s/maybe String) "The description annotation of this Ontology Class")})

(s/defschema OntologyClassHierarchy
  (merge OntologyClass
         {(s/optional-key :subclasses)
          (describe [(s/recursive #'OntologyClassHierarchy)] "Subclasses of this Ontology Class")}))

(s/defschema OntologyHierarchy
  {:hierarchy (describe (s/maybe OntologyClassHierarchy) "An Ontology Class hierarchy")})

(s/defschema OntologyHierarchyList
  {:hierarchies (describe [OntologyClassHierarchy] "A list of Ontology Class hierarchies")})

(s/defschema OntologyHierarchyFilterRequest
  {:target-ids
   (describe [UUID] "List of IDs to filter the hierarchy")

   :target-types
   (describe [TargetTypeEnum] "The types of the given IDs")})
