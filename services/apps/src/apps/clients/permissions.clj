(ns apps.clients.permissions
  (:use [kameleon.uuids :only [uuidify]])
  (:require [apps.util.config :as config]
            [apps.util.service :as service]
            [clojure.tools.logging :as log]
            [permissions-client.core :as pc]))

(defn- client []
  (config/permissions-client))

(defn- rt-app []
  (config/app-resource-type))

(defn- rt-analysis []
  (config/analysis-resource-type))

(defn- group-permissions
  "Groups permissions by resource ID. The resource ID must be a UUID."
  [perms]
  (into {} (map (juxt (comp uuidify :name :resource) :permission_level) perms)))

(defn extract-error-message
  [body]
  ((some-fn :reason :message) (service/parse-json body)))

(defn register-private-app
  [user app-id]
  (pc/grant-permission (client) (rt-app) app-id "user" user "own"))

(defn- filter-perms-response
  [response filter-fn]
  (group-permissions (filter filter-fn (:permissions response))))

(defn- load-app-permissions*
  ([user filter-fn]
   (filter-perms-response (pc/get-subject-permissions-for-resource-type (client) "user" user (rt-app) true)
                          filter-fn))
  ([user min-level filter-fn]
   (filter-perms-response (pc/get-subject-permissions-for-resource-type (client) "user" user (rt-app) true min-level)
                          filter-fn)))

(defn load-app-permissions
  ([user]
   (load-app-permissions* user (constantly true)))
  ([user app-ids]
   (load-app-permissions* user (comp (set app-ids) uuidify :name :resource)))
  ([user app-ids min-level]
   (load-app-permissions* user min-level (comp (set app-ids) uuidify :name :resource))))

(defn- format-perms-listing
  [user perms]
  (->> (map (juxt (comp :subject_id :subject) :permission_level) (:permissions perms))
       (remove (comp (partial = user) first))
       (map (fn [[subject level]] {:user subject :permission level}))))

(defn list-app-permissions
  [user app-ids]
  (for [app-id app-ids]
    [(uuidify app-id) (format-perms-listing user (pc/list-resource-permissions (client) (rt-app) app-id))]))
