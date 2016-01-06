(ns terrain.services.permanent-id-requests
  (:use [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [throw+]]
        [terrain.auth.user-attributes :only [current-user]])
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [clojure-commons.file-utils :as ft]
            [terrain.clients.data-info :as data-info]
            [terrain.clients.data-info.raw :as data-info-client]
            [terrain.clients.metadata.raw :as metadata]
            [terrain.util.config :as config]
            [terrain.util.service :as service]))

(defn- validate-request-target-type
  [{target-type :type :as folder}]
  (let [target-type (metadata/resolve-data-type target-type)]
    (when-not (= target-type "folder")
      (throw+ {:type :clojure-commons.exception/bad-request-field
               :error "The given data ID does not belong to a folder."
               :file folder}))
    target-type))

(defn- validate-owner
  [user {:keys [id path] :as data-item}]
  (when-not (data-info/owns? user path)
    (throw+ {:type :clojure-commons.exception/not-owner
             :error "User does not own given folder."
             :user user
             :folder-id id}))
  data-item)

(defn- create-staging-dir
  []
  (let [staging-path (config/permanent-id-staging-dir)
        curators     (set (config/permanent-id-curators))]
    (when-not (data-info/path-exists? (config/irods-user) staging-path)
      (log/warn "creating" staging-path "for:" (clojure.string/join ", " curators))
      (data-info-client/create-dirs (config/irods-user) [staging-path])
      (data-info/share (config/irods-user) curators [staging-path] "own"))))

(defn- validate-staging-dest
  [{:keys [path] :as data-item}]
  (let [staging-dest (ft/path-join (config/permanent-id-staging-dir) (ft/basename path))]
    (when (data-info/path-exists? (config/irods-user) staging-dest)
    (throw+ {:type :clojure-commons.exception/exists
             :error "A folder with this name has already been submitted for a Permanent ID request."
             :path staging-dest})))
  data-item)

(defn- validate-data-item
  [user data-item]
  (->> data-item
       (validate-owner user)
       validate-staging-dest))

(defn- stage-data-item
  [user {:keys [id path] :as data-item}]
  (let [folder-name (ft/basename path)
        curators (set (config/permanent-id-curators))]
    (log/warn "share" path "with:" (clojure.string/join ", " curators))
    (data-info/share user curators [path] "own")
    (data-info-client/move-single (config/irods-user) id (config/permanent-id-staging-dir))
    (when-not (contains? curators user)
      (data-info/share (config/irods-user) [user] [path] "write"))))

(defn- format-perm-id-req-response
  [user {:keys [target_id] :as response}]
  (-> response
      (dissoc :target_id :target_type :original_path)
      (assoc :folder (data-info/stat-by-uuid user (uuidify target_id)))))

(defn list-permanent-id-requests
  [params]
  (metadata/list-permanent-id-requests params))

(defn create-permanent-id-request
  [params body]
  (create-staging-dir)
  (let [{type :type folder-id :folder} (service/decode-json body)
        folder-id                      (uuidify folder-id)
        user                           (:shortUsername current-user)
        {:keys [path] :as folder}      (->> folder-id
                                            (data-info/stat-by-uuid user)
                                            (validate-data-item user))
        target-type                    (validate-request-target-type folder)
        response                       (-> {:type type
                                            :target_id folder-id
                                            :target_type target-type
                                            :original_path path}
                                         json/encode
                                         metadata/create-permanent-id-request
                                         :body
                                         service/decode-json)]
    (stage-data-item user folder)
    (format-perm-id-req-response user response)))

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
