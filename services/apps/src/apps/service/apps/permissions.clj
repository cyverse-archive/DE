(ns apps.service.apps.permissions
  (:require [clojure.tools.logging :as log]
            [clojure-commons.error-codes :as ce]))

(defn process-app-sharing-requests
  [apps-client app-sharing-requests]
  (for [{sharee :user user-app-sharing-requests :apps} app-sharing-requests]
    {:user sharee
     :apps (.shareAppsWithUser apps-client sharee user-app-sharing-requests)}))

(defn process-user-app-sharing-requests
  [apps-client sharee user-app-sharing-requests]
  (for [{app-id :app_id level :permission :as request} user-app-sharing-requests]
    (do (log/spy :warn request)
        (.shareAppWithUser apps-client sharee app-id level))))

(defn app-sharing-success
  [app-id level]
  {:app_id     app-id
   :permission level
   :success    true})

(defn app-sharing-failure
  [app-id level reason]
  {:app_id     app-id
   :permission level
   :success    false
   :error      {:error_code ce/ERR_BAD_REQUEST
                :reason     reason}})
