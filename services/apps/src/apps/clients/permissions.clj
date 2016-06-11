(ns apps.clients.permissions
  (:use [clojure-commons.error-codes :only [clj-http-error?]]
        [clostache.parser :only [render]]
        [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [try+]])
  (:require [apps.clients.iplant-groups :as ipg]
            [apps.util.config :as config]
            [apps.util.service :as service]
            [clojure.tools.logging :as log]
            [permissions-client.core :as pc]))

(defn- client []
  (config/permissions-client))

(defn- rt-app []
  (config/app-resource-type))

(defn- rt-analysis []
  (config/analysis-resource-type))

(defn- get-failure-reason
  "Extracts the failure reason from an error response body."
  [body]
  ((some-fn :message :reason) (service/parse-json body)))

(defn- group-permissions
  "Groups permissions by resource ID. The resource ID must be a UUID."
  [perms]
  (into {} (map (juxt (comp uuidify :name :resource) :permission_level) perms)))

(defn extract-error-message
  [body]
  ((some-fn :reason :message) (service/parse-json body)))

(defn- register-private-resource
  [resource-type user resource-id]
  (pc/grant-permission (client) resource-type resource-id "user" user "own"))

(def register-private-app (partial register-private-resource (rt-app)))
(def register-private-analysis (partial register-private-resource (rt-analysis)))

(defn- filter-perms-response
  [response filter-fn]
  (group-permissions (filter filter-fn (:permissions response))))

(defn- load-resource-permissions*
  ([resource-type user filter-fn]
   (filter-perms-response
    (pc/get-subject-permissions-for-resource-type (client) "user" user resource-type true)
    filter-fn))
  ([resource-type user min-level filter-fn]
   (filter-perms-response
    (pc/get-subject-permissions-for-resource-type (client) "user" user resource-type true min-level)
    filter-fn)))

(defn- resource-id-filter
  [resource-ids]
  (comp (set resource-ids) uuidify :name :resource))

(defn- load-resource-permissions
  ([resource-type user]
   (load-resource-permissions* resource-type user (constantly true)))
  ([resource-type user resource-ids]
   (if (= (count resource-ids) 1)
     ((comp group-permissions :permissions)
      (pc/get-subject-permissions-for-resource (client) "user" user resource-type (first resource-ids) true))
     (load-resource-permissions* resource-type user (resource-id-filter resource-ids))))
  ([resource-type user resource-ids min-level]
   (if (= (count resource-ids) 1)
     ((comp group-permissions :permissions)
      (pc/get-subject-permissions-for-resource (client) "user" user resource-type (first resource-ids) true min-level))
     (load-resource-permissions* resource-type user min-level (resource-id-filter resource-ids)))))

(def load-app-permissions (partial load-resource-permissions (rt-app)))
(def load-analysis-permissions (partial load-resource-permissions (rt-analysis)))

(defn- format-perms-listing
  [user perms]
  (->> (map (juxt (comp :subject_id :subject) :permission_level) (:permissions perms))
       (remove (comp (partial = user) first))
       (map (fn [[subject level]] {:user subject :permission level}))))

(defn- list-resource-permissions
  [resource-type user resource-ids]
  (for [resource-id resource-ids]
    [(uuidify resource-id)
     (format-perms-listing user (pc/list-resource-permissions (client) resource-type resource-id))]))

(def list-app-permissions (partial list-resource-permissions (rt-app)))
(def list-analysis-permissions (partial list-resource-permissions (rt-analysis)))

(defn delete-app-resource
  [app-id]
  (pc/delete-resource (client) app-id (rt-app)))

(defn- revoke-app-user-permission
  "Revokes a user's permission to access an app, ignoring cases where the user didn't already have access."
  [user app-id]
  (try+
   (pc/revoke-permission (client) (rt-app) app-id "user" user)
   (catch [:status 404] _)))

(defn make-app-public
  [user app-id]
  (revoke-app-user-permission user app-id)
  (pc/grant-permission (client) (rt-app) app-id "group" (ipg/grouper-user-group-id) "read"))

(defn- resource-sharing-log-msg
  [action resource-type resource-name subject-type subject-id reason]
  (render "unable to {{action}} {{resource-type}}:{{resource-name}} with {{subject-type}}:{{subject-id}}: {{reason}}"
          {:action        action
           :resource-type resource-type
           :resource-name resource-name
           :subject-type  subject-type
           :subject-id    subject-id
           :reason        reason}))

(defn- share-resource
  [resource-type resource-name subject-type subject-id level]
  (try+
   (pc/grant-permission (client) resource-type resource-name subject-type subject-id level)
   nil
   (catch clj-http-error? {:keys [body]}
     (let [reason (get-failure-reason body)]
       (log/error (resource-sharing-log-msg "share" resource-type resource-name subject-type subject-id reason)))
     "the app sharing request failed")))

(defn- unshare-resource
  [resource-type resource-name subject-type subject-id]
  (try+
   (pc/revoke-permission (client) resource-type resource-name subject-type subject-id)
   nil
   (catch clj-http-error? {:keys [body]}
     (let [reason (get-failure-reason body)]
       (log/error (resource-sharing-log-msg "unshare" resource-type resource-name subject-type subject-id reason)))
     "the app unsharing request failed")))

(def share-app (partial share-resource (rt-app)))
(def unshare-app (partial unshare-resource (rt-app)))
(def share-analysis (partial share-resource (rt-analysis)))
(def unshare-analysis (partial unshare-resource (rt-analysis)))
