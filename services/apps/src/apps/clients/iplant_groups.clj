(ns apps.clients.iplant-groups
  (:use [medley.core :only [remove-vals]]
        [slingshot.slingshot :only [throw+]])
  (:require [apps.util.config :as config]
            [cemerick.url :as curl]
            [clj-http.client :as http]
            [clojure.string :as string]
            [clojure-commons.exception :as cx]
            [kameleon.uuids :refer [uuidify]]))

(def grouper-user "de_grouper")
(def grouper-user-group-fmt "iplant:de:%s:users:de-users")
(def grouper-app-permission-def-fmt "iplant:de:%s:apps:app-permission-def")
(def grouper-app-resource-name-fmt "iplant:de:%s:apps:%s")

(defn- grouper-user-group
  []
  (format grouper-user-group-fmt (config/env-name)))

(defn- grouper-app-permission-def
  []
  (format grouper-app-permission-def-fmt (config/env-name)))

(defn- grouper-app-resource-name
  [app-id]
  (format grouper-app-resource-name-fmt (config/env-name) app-id))

(defn- grouper-url
  [& components]
  (str (apply curl/url (config/ipg-base) components)))

(defn lookup-subject
  "Uses iplant-groups's subject lookup by ID endpoint to retrieve user details."
  [user short-username]
  (-> (http/get (grouper-url "subjects" short-username) {:query-params {:user user} :as :json})
      (:body)))

(defn load-app-permissions
  "Loads app permissions from Grouper."
  [user]
  (let [id-from-resource #(last (string/split % #":"))]
    (->> (http/get (grouper-url "attributes" "permissions")
                   {:query-params {:user          grouper-user
                                   :role          (grouper-user-group)
                                   :attribute_def (grouper-app-permission-def)
                                   :subject_id    user}
                    :as           :json})
         (:body)
         (:assignments)
         (group-by (comp uuidify id-from-resource :name :attribute_definition_name)))))

(defn- create-resource
  "Creates a new permission name in grouper."
  [resource-name permission-def]
  (:body (http/post (grouper-url "attributes")
                    {:query-params {:user grouper-user}
                     :form-params  {:name                 resource-name
                                    :attribute_definition {:name permission-def}}
                     :content-type :json
                     :as           :json})))

(defn- grant-role-user-permission
  "Grants permission to access a resource to an individual user."
  [user role resource-name action]
  (:body (http/put (grouper-url "attributes" resource-name "permissions" "memberships" role user action)
                   {:query-params {:user grouper-user}
                    :form-params  {:allowed true}
                    :content-type :json
                    :as           :json})))

(defn register-private-app
  "Registers a new private app in Grouper."
  [user app-id]
  (let [app-resource-name (grouper-app-resource-name app-id)]
    (create-resource app-resource-name (grouper-app-permission-def))
    (grant-role-user-permission user (grouper-user-group) app-resource-name "own")))

(defn- format-role-permissions
  "Formats the role permissions for a permission update request."
  [role-permissions]
  (mapv (fn [[role-name action-name]]
          {:role_name role-name :action_name action-name})
        role-permissions))

(defn- format-membership-permissions
  "Formats the membership permissions for a permission update request."
  [membership-permissions]
  (mapv (fn [[role-name subject-id action-name]]
          {:role_name role-name :subject_id subject-id :action_name action-name})
        membership-permissions))

(defn- permission-update-body
  "Formats the request body for altering the the permissions of a single app in bulk."
  [role-permissions membership-permissions]
  (remove-vals nil? {:role_permissions       (format-role-permissions role-permissions)
                     :membership_permissions (format-membership-permissions membership-permissions)}))

(defn- public-permission-update-body
  "Formats the request body for making a resource publicly accessible in the DE."
  []
  (permission-update-body [[(grouper-user-group) "read"]] []))

(defn make-app-public
  "Makes an app publicly accessible in Grouper."
  [app-id]
  (:body (http/put (grouper-url "attributes" (grouper-app-resource-name app-id) "permissions")
                   {:query-params {:user grouper-user}
                    :form-params  (public-permission-update-body)
                    :content-type :json
                    :as           :json})))
