(ns metadata.routes.domain.ontologies
  (:use [common-swagger-api.schema]
        [common-swagger-api.schema.ontologies]
        [metadata.routes.domain.common])
  (:require [schema.core :as s]))

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
