(ns apps.service.apps.de.permissions
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [clojure.string :as string]
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

(defn- check-app-permissions
  [user required-level app-ids]
  (let [perms (iplant-groups/load-app-permissions user app-ids)]
    (when-let [forbidden-apps (seq (filter (partial lacks-permission-level perms required-level) app-ids))]
      (cxu/forbidden (str "insufficient privileges for apps: " (string/join ", " forbidden-apps))))))

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
