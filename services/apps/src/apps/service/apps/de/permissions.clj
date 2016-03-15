(ns apps.service.apps.de.permissions
  (:use [clojure-commons.error-codes :only [clj-http-error?]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [apps.persistence.app-metadata :as amp]
            [apps.service.apps.util :as apps-util]
            [apps.util.service :as service]
            [clojure.string :as string]
            [clojure-commons.exception-util :as cxu]))

(defn check-app-permissions
  [user required-level app-ids]
  (let [perms (iplant-groups/load-app-permissions user app-ids)]
    (when-let [forbidden-apps (seq (iplant-groups/find-forbidden-resources perms required-level app-ids))]
      (cxu/forbidden (str "insufficient privileges for apps: " (string/join ", " forbidden-apps))))))

(defn load-app-permissions
  [user app-id]
  (try+
   (iplant-groups/load-app-permissions user [app-id])
   (catch clj-http-error? {:keys [body]}
     (throw+ {:type   ::permission-load-failure
              :reason (:grouper_result_message (service/parse-json body))}))))

(defn- format-app-permissions
  [user perms app-names app-id]
  (->> (group-by (comp string/lower-case :id :subject) (perms app-id))
       (map iplant-groups/format-permission)
       (remove (comp (partial = user) :user))
       (hash-map :id (str app-id) :name (apps-util/get-app-name app-names app-id) :permissions)))

(defn list-app-permissions
  [{user :shortUsername} app-ids]
  (check-app-permissions user "read" app-ids)
  (let [app-perms (iplant-groups/list-app-permissions app-ids)
        app-names (amp/get-app-names app-ids)]
    (map (partial format-app-permissions user app-perms app-names) app-ids)))

(defn has-app-permission
  [user app-id required-level]
  (-> (iplant-groups/load-app-permissions user [app-id])
      (iplant-groups/has-permission-level required-level app-id)))
