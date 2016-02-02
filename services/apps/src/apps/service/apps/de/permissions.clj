(ns apps.service.apps.de.permissions
  (:use [clojure-commons.error-codes :only [clj-http-error?]]
        [slingshot.slingshot :only [try+]])
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [apps.util.service :as service]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure-commons.exception-util :as cxu]))

(def permission-precedence (into {} (map-indexed (fn [i v] (vector v i)) ["own" "write" "read"])))

(defn get-permission-level
  ([perms app-id]
     (get-permission-level (perms app-id)))
  ([perms]
     (first (sort-by permission-precedence (map :action_name perms)))))

(defn has-permission-level
  [perms required-level app-id]
  (some (comp (partial = required-level) :action_name) (perms app-id)))

(def lacks-permission-level (complement has-permission-level))

(defn check-app-permissions
  [user required-level app-ids]
  (let [perms (iplant-groups/load-app-permissions user app-ids)]
    (when-let [forbidden-apps (seq (filter (partial lacks-permission-level perms required-level) app-ids))]
      (cxu/forbidden (str "insufficient privileges for apps: " (string/join ", " forbidden-apps))))))

(defn- get-permission-error
  [user required-level app-id]
  (try+
   (let [perms (iplant-groups/load-app-permissions user [app-id])]
     (when (lacks-permission-level perms required-level app-id)
       (str "insufficient privileges for app: " (str app-id))))
   (catch clj-http-error? {:keys [body]}
     (let [reason (:grouper_result_message (service/parse-json body))]
       (str "unable to load permissions for " app-id ": " reason)))))

(defn- format-app-permissions
  [user perms app-id]
  (->> (group-by (comp string/lower-case :id :subject) (perms app-id))
       (map (fn [[subject subject-perms]] {:user subject :permission (get-permission-level subject-perms)}))
       (remove (comp (partial = user) :user))
       (hash-map :id (str app-id) :permissions)))

(defn list-app-permissions
  [{user :shortUsername} app-ids]
  (check-app-permissions user "read" app-ids)
  (map (partial format-app-permissions user (iplant-groups/list-app-permissions app-ids)) app-ids))

(defn share-app-with-user
  [{user :shortUsername} sharee app-id level]
  (if-let [permission-error (get-permission-error user "own" app-id)]
    permission-error
    (iplant-groups/share-app app-id sharee level)))

(defn unshare-app-with-user
  [{user :shortUsername} sharee app-id]
  (if-let [permission-error (get-permission-error user "own" app-id)]
    permission-error
    (iplant-groups/unshare-app app-id sharee)))
