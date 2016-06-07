(ns apps.service.apps.de.permissions
  (:use [clojure-commons.error-codes :only [clj-http-error?]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [apps.clients.permissions :as perms-client]
            [apps.persistence.app-metadata :as amp]
            [apps.service.apps.util :as apps-util]
            [apps.util.service :as service]
            [clojure.string :as string]
            [clojure-commons.exception-util :as cxu]))

(defn check-app-permissions
  [user required-level app-ids]
  (let [accessible-app-ids (set (keys (perms-client/load-app-permissions user app-ids required-level)))]
    (when-let [forbidden-apps (seq (remove accessible-app-ids app-ids))]
      (cxu/forbidden (str "insufficient privileges for apps: " (string/join ", " forbidden-apps))))))

(defn- format-app-permissions
  [app-names [app-id app-perms]]
  {:id          (str app-id)
   :name        (apps-util/get-app-name app-names app-id)
   :permissions app-perms})

(defn list-app-permissions
  [{user :shortUsername} app-ids]
  (check-app-permissions user "read" app-ids)
  (map (partial format-app-permissions (amp/get-app-names app-ids))
       (perms-client/list-app-permissions user app-ids)))

(defn has-app-permission
  [user app-id required-level]
  (try+
    (seq (perms-client/load-app-permissions user [app-id] required-level))
    (catch clj-http-error? {:keys [body]}
      (throw+ {:type   ::permission-load-failure
               :reason (perms-client/extract-error-message body)}))))
