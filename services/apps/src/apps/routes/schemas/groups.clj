(ns apps.routes.schemas.groups
  (:use [common-swagger-api.schema :only [describe NonBlankString]])
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

(s/defschema Subject
  {:id
   (describe String "The subject ID.")

   (s/optional-key :name)
   (describe String "The subject name.")

   (s/optional-key :first_name)
   (describe String "The subject's first name.")

   (s/optional-key :last_name)
   (describe String "The subject's last name.")

   (s/optional-key :email)
   (describe String "The subject email.")

   (s/optional-key :institution)
   (describe String "The subject institution.")

   (s/optional-key :attribute_values)
   (describe [String] "A list of additional attributes applied to the subject.")

   :source_id
   (describe String "The ID of the source of the subject information.")})

(s/defschema GroupMembers
  {:members (describe [Subject] "The list of group members.")})

(s/defschema GroupMembersUpdate
  {:members (describe [NonBlankString] "The new list of member subject IDs.")})

(s/defschema GroupMembersUpdateResponse
  {:failures (describe [String] "The list of subject IDs that could not be added to the group.")
   :members  (describe [Subject] "The updated list of group members.")})
