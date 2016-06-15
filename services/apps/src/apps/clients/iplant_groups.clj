(ns apps.clients.iplant-groups
  (:require [apps.util.config :as config]
            [cemerick.url :as curl]
            [clj-http.client :as http]))

(def grouper-user-group-fmt "iplant:de:%s:users:de-users")

(defn- grouper-user-group
  []
  (format grouper-user-group-fmt (config/env-name)))

(defn- grouper-url
  [& components]
  (str (apply curl/url (config/ipg-base) components)))

(defn lookup-group-id
  "Looks up a group identifier in Grouper."
  [group-name]
  ((comp :id :body)
   (http/get (grouper-url "groups" group-name)
             {:query-params {:user (config/de-grouper-user)}
              :as           :json})))

(def grouper-user-group-id (memoize (fn [] (lookup-group-id (grouper-user-group)))))

(defn lookup-subject
  "Uses iplant-groups's subject lookup by ID endpoint to retrieve user details."
  [user short-username]
  (-> (http/get (grouper-url "subjects" short-username) {:query-params {:user user} :as :json})
      (:body)))

(defn add-de-user
  "Adds a user to the de-users group."
  [subject-id]
  (http/put (grouper-url "groups" (grouper-user-group) "members" subject-id)
            {:query-params {:user (config/de-grouper-user)}}))
