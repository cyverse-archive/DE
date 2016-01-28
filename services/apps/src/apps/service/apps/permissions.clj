(ns apps.service.apps.permissions
  (:require [clojure-commons.error-codes :as ce]))

(defn process-app-sharing-requests
  [apps-client app-sharing-requests]
  (for [{sharee :user user-app-sharing-requests :apps} app-sharing-requests]
    {:user sharee
     :apps (.shareAppsWithUser apps-client sharee user-app-sharing-requests)}))

(defn process-user-app-sharing-requests
  [apps-client sharee user-app-sharing-requests]
  (for [{app-id :app_id level :permission} user-app-sharing-requests]
    (.shareAppWithUser apps-client sharee app-id level)))

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

(defn process-app-unsharing-requests
  [apps-client app-unsharing-requests]
  (for [{sharee :user app-ids :apps} app-unsharing-requests]
    {:user sharee
     :apps (.unshareAppsWithUser apps-client sharee app-ids)}))

(defn process-user-app-unsharing-requests
  [apps-client sharee app-ids]
  (for [app-id app-ids]
    (.unshareAppWithUser apps-client sharee app-id)))

(defn app-unsharing-success
  [app-id]
  {:app_id  app-id
   :success true})

(defn app-unsharing-failure
  [app-id reason]
  {:app_id  app-id
   :success false
   :error   {:error_code ce/ERR_BAD_REQUEST
             :reason     reason}})
