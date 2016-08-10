(ns apps.routes.schemas.groups
  (:use [common-swagger-api.schema :only [describe]])
  (:require [schema.core :as s]))

(s/defschema Group
  {:name
   (describe String "The internal group name.")

   :type
   (describe String "The group type name.")

   (s/optional-key :description)
   (describe String "A brief description of the group.")

   (s/optional-key :display_extension)
   (describe String "The displayable group name extension.")

   (s/optional-key :display_name)
   (describe String "The displayable group name.")

   (s/optional-key :extension)
   (describe String "The internal group name extension.")

   :id_index
   (describe String "The sequential ID index number.")

   :id
   (describe String "The group ID.")})
