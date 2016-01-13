(ns apps.clients.iplant-groups
  (:use [slingshot.slingshot :only [throw+]])
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
