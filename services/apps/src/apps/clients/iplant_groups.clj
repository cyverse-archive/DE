(ns apps.clients.iplant-groups
  (:require [apps.util.config :as config]
            [cemerick.url :as curl]
            [clj-http.client :as http]
            [clojure.string :as string]
            [kameleon.uuids :refer [uuidify]]))

(def grouper-user "de_grouper")
(def grouper-user-group-fmt "iplant:de:%s:users:de-users")
(def grouper-app-permission-def-fmt "iplant:de:%s:apps:app-permission-def")

(defn- grouper-user-group
  []
  (format grouper-user-group-fmt (config/env-name)))

(defn- grouper-app-permission-def
  []
  (format grouper-app-permission-def-fmt (config/env-name)))

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
