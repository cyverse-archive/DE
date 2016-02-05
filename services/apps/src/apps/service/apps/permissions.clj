(ns apps.service.apps.permissions
  (:use [medley.core :only [map-kv]])
  (:require [apps.clients.notifications :as cn]
            [apps.service.apps.util :as apps-util]
            [clojure.tools.logging :as log]
            [clojure-commons.error-codes :as ce]))

;; TODO: this will have to change to account for the possibility of duplicate app IDs
;; if more apps clients are ever added, which will require a larger refactoring than
;; just this function, anyway.
(defn- load-app-names
  [apps-client requests]
  (->> (mapcat :apps requests)
       (map #(if (string? %) % (:app_id %)))
       set
       (#(.loadAppTables apps-client %))
       (apply merge)
       (map-kv (fn [k v] [k (:name v)]))))

(defn process-app-sharing-requests
  [apps-client app-sharing-requests]
  (let [app-names (load-app-names apps-client app-sharing-requests)]
    (for [{sharee :user user-app-sharing-requests :apps} app-sharing-requests]
      {:user sharee
       :apps (.shareAppsWithUser apps-client app-names sharee user-app-sharing-requests)})))

(defn- share-apps-with-user
  [apps-client app-names sharee user-app-sharing-requests]
  (for [{app-id :app_id level :permission} user-app-sharing-requests]
    (.shareAppWithUser apps-client app-names sharee app-id level)))

(defn process-user-app-sharing-requests
  [apps-client app-names sharee user-app-sharing-requests]
  (let [responses (share-apps-with-user apps-client app-names sharee user-app-sharing-requests)]
    (cn/send-app-sharing-notifications (:shortUsername (.getUser apps-client)) sharee responses)
    (mapv #(dissoc % :sharer_category :sharee_category) responses)))

(defn app-sharing-success
  [app-names app-id level sharer-category sharee-category]
  {:app_id          (str app-id)
   :app_name        (apps-util/get-app-name app-names app-id)
   :sharer_category sharer-category
   :sharee_category sharee-category
   :permission      level
   :success         true})

(defn app-sharing-failure
  [app-names app-id level sharer-category sharee-category reason]
  {:app_id          (str app-id)
   :app_name        (apps-util/get-app-name app-names app-id)
   :sharer_category sharer-category
   :sharee_category sharee-category
   :permission      level
   :success         false
   :error           {:error_code ce/ERR_BAD_REQUEST
                     :reason     reason}})

(defn process-app-unsharing-requests
  [apps-client app-unsharing-requests]
  (let [app-names (load-app-names apps-client app-unsharing-requests)]
    (for [{sharee :user app-ids :apps} app-unsharing-requests]
      {:user sharee
       :apps (.unshareAppsWithUser apps-client app-names sharee app-ids)})))

(defn- unshare-apps-with-user
  [apps-client app-names sharee app-ids]
  (for [app-id app-ids]
    (.unshareAppWithUser apps-client app-names sharee app-id)))

(defn process-user-app-unsharing-requests
  [apps-client app-names sharee app-ids]
  (let [responses (unshare-apps-with-user apps-client app-names sharee app-ids)]
    (cn/send-app-unsharing-notifications (:shortUsername (.getUser apps-client)) sharee responses)
    (mapv #(dissoc % :sharer_category) responses)))

(defn app-unsharing-success
  [app-names app-id sharer-category]
  {:app_id          (str app-id)
   :app_name        (apps-util/get-app-name app-names app-id)
   :sharer_category sharer-category
   :success         true})

(defn app-unsharing-failure
  [app-names app-id sharer-category reason]
  {:app_id          (str app-id)
   :app_name        (apps-util/get-app-name app-names app-id)
   :sharer_category sharer-category
   :success         false
   :error           {:error_code ce/ERR_BAD_REQUEST
                     :reason     reason}})
