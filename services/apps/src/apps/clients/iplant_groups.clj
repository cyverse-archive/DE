(ns apps.clients.iplant-groups
  (:use [slingshot.slingshot :only [try+]])
  (:require [apps.util.config :as config]
            [cemerick.url :as curl]
            [clj-http.client :as http]
            [clojure.string :as string]))

(def ^:private grouper-environment-base-fmt "iplant:de:%s")

(defn- grouper-environment-base
  []
  (format grouper-environment-base-fmt (config/env-name)))

(defn remove-environment-from-group
  [group-name]
  (string/replace-first group-name (str (grouper-environment-base) ":") ""))

(def ^:private grouper-standard-group-fmt "%s:users:%s")

(defn- grouper-standard-group
  [group-name]
  (format grouper-standard-group-fmt (grouper-environment-base) group-name))

(def ^:private grouper-user-group (partial grouper-standard-group "de-users"))
(def ^:private grouper-workshop-group (partial grouper-standard-group "workshop-users"))

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

(defn lookup-subject-groups
  "Uses iplant-groups groups-for-subject lookup by ID endpoint to retrieve a user's groups"
  [short-username]
  (-> (http/get (grouper-url "subjects" short-username "groups") {:query-params {:user (config/de-grouper-user) :folder (grouper-environment-base)} :as :json})
      :body))

(defn add-de-user
  "Adds a user to the de-users group."
  [subject-id]
  (http/put (grouper-url "groups" (grouper-user-group) "members" subject-id)
            {:query-params {:user (config/de-grouper-user)}}))

(defn- get-group
  "Retrieves information about a DE group."
  [group-name]
  (try+
   (:body (http/get (grouper-url "groups" group-name)
                    {:query-params {:user (config/de-grouper-user)}
                     :as           :json}))
   (catch [:status 404] _ nil)))

(defn- create-group
  "Creates a group."
  [group-name group-type]
  (:body (http/post (grouper-url "groups")
                    {:query-params {:user (config/de-grouper-user)}
                     :form-params  {:name group-name
                                    :type group-type}
                     :content-type :json
                     :as           :json})))

(defn- get-group-members
  "Retrieves a list of members belonging to a group."
  [group-name]
  (:body (http/get (grouper-url "groups" group-name "members")
                   {:query-params {:user (config/de-grouper-user)}
                    :as           :json})))

(defn get-or-create-group
  "Ensures that a group with the given name exists."
  [group-name group-type]
  (or (get-group group-name)
      (create-group group-name group-type)))

(defn get-workshop-group
  "Retrieves information about the workshop users group, creating the group if necessary."
  []
  (get-or-create-group (grouper-workshop-group) "role"))

(defn get-workshop-group-members
  "Retrieves the list of workshop group members, creating the group if necessary."
  []
  (get-workshop-group)
  (get-group-members (grouper-workshop-group)))
