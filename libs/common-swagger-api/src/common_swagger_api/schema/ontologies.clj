(ns common-swagger-api.schema.ontologies
  (:use [common-swagger-api.schema :only [describe NonBlankString]])
  (:require [schema.core :as s])
  (:import [java.util Date]))

(def OntologyVersionParam (describe String "The unique version of the Ontology"))
(def OntologyClassIRIParam (describe String "A unique Class IRI"))

(s/defschema OntologyDetails
  {:iri        (describe (s/maybe String) "The unique IRI for this Ontology")
   :version    OntologyVersionParam
   :created_by (describe NonBlankString "The user who uploaded this Ontology")
   :created_on (describe Date "The date this Ontology was uploaded")})
