(ns data-info.util.schema
  (:require [schema.spec.variant :as variant]
            [schema.spec.core :as spec :include-macros true]
            [ring.swagger.json-schema :as rsjs]
            [schema.core :as s]))

;; This record type acts like the first schema, but acts like the second schema
;; for the sake of swagger documentation (as controlled by the `extend-protocol`
;; below).

(defrecord DocOnly [schema-real schema-doc]
  s/Schema
  (spec [this]
    (variant/variant-spec
     spec/+no-precondition+
     [{:schema schema-real}]))
  (explain [this] (list 'doc-only (s/explain schema-real) (s/explain schema-doc))))

(defn doc-only [schema-to-use schema-to-doc]
  (DocOnly. schema-to-use schema-to-doc))

(extend-protocol rsjs/JsonSchema
  DocOnly
  (convert [e _]
    (rsjs/->swagger (:schema-doc e))))
