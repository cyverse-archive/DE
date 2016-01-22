(ns iplant_groups.routes.domain.params
  (:use [common-swagger-api.schema :only [describe NonBlankString StandardUserQueryParams]])
  (:require [clojure.string :as string]
            [schema.core :as s]))

(def SubjectIdPathParam
  (describe String "The subject identifier."))

(def GroupNamePathParam
  (describe String "The full group name."))

(def FolderNamePathParam
  (describe String "The full folder name."))

(def AttributeNamePathParam
  (describe String "The full attribute name."))

(s/defschema SearchParams
  (assoc StandardUserQueryParams
    :search (describe NonBlankString "The partial name of the entity to search for.")))

(s/defschema GroupSearchParams
  (assoc SearchParams
    (s/optional-key :folder)
    (describe NonBlankString "The name of the folder to search for.")))

(s/defschema AttributeSearchParams
  (assoc SearchParams
    (s/optional-key :exact)
    (describe Boolean "If true, match this name (including stems) exactly.")))

(s/defschema AttributeAssignmentSearchParams
  (assoc StandardUserQueryParams
    (s/optional-key :attribute_def_id)
    (describe NonBlankString "The id of an attribute/permission definition to search with.")

    (s/optional-key :attribute_def)
    (describe NonBlankString "The name of an attribute/permission definition to search with.")

    (s/optional-key :attribute_def_name_ids)
    (describe [NonBlankString] "The ids of attribute name/permision resources to search with.")

    (s/optional-key :attribute_def_names)
    (describe [NonBlankString] "The names of attribute name/permission resources to search with.")

    (s/optional-key :subject_id)
    (describe NonBlankString "The id of a subject to search with.")

    (s/optional-key :role_id)
    (describe NonBlankString "The id of a role-type group to search with.")

    (s/optional-key :role)
    (describe NonBlankString "The name of a role-type group to search with.")

    (s/optional-key :action_names)
    (describe [NonBlankString] "A list of action names to search with.")

    (s/optional-key :immediate_only)
    (describe NonBlankString "Indicates whether only immediate assignments should be returned.")))
