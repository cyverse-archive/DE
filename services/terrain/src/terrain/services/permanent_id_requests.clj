(ns terrain.services.permanent-id-requests
  (:use [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [throw+]]
        [terrain.auth.user-attributes :only [current-user]])
  (:require [cheshire.core :as json]
            [terrain.clients.data-info :as data-info]
            [terrain.clients.metadata.raw :as metadata]
            [terrain.util.service :as service]))

(defn list-permanent-id-requests
  [params]
  (metadata/list-permanent-id-requests params))

(defn create-permanent-id-request
  [params body]
  (let [{type :type folder-id :folder} (service/decode-json body)
        folder-id (uuidify folder-id)
        user (:shortUsername current-user)
        {:keys [path] :as folder} (data-info/stat-by-uuid user folder-id)
        target-type (metadata/resolve-data-type (:type folder))]
    (when-not (data-info/owns? user path)
      (throw+ {:type :clojure-commons.exception/not-owner
               :error "User does not own given folder."
               :user user
               :folder-id folder-id}))
    (when-not (= target-type "folder")
      (throw+ {:type :clojure-commons.exception/bad-request-field
               :error "The given data ID does not belong to a folder."
               :user user
               :file folder}))
    (metadata/create-permanent-id-request
      (json/encode
        {:type type
         :target_id folder-id
         :target_type target-type
         :original_path path}))))

(defn list-permanent-id-request-status-codes
  [params]
  (metadata/list-permanent-id-request-status-codes))

(defn list-permanent-id-request-types
  [params]
  (metadata/list-permanent-id-request-types))

(defn get-permanent-id-request
  [request-id params]
  (metadata/get-permanent-id-request request-id))

(defn admin-list-permanent-id-requests
  [params]
  (metadata/admin-list-permanent-id-requests params))

(defn admin-get-permanent-id-request
  [request-id params]
  (metadata/admin-get-permanent-id-request request-id))

(defn update-permanent-id-request
  [request-id params body]
  (metadata/update-permanent-id-request request-id body))
