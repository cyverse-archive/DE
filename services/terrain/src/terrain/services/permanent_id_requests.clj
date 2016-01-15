(ns terrain.services.permanent-id-requests
  (:use [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [try+ throw+]]
        [terrain.auth.user-attributes :only [current-user]]
        [terrain.services.filesystem.metadata :only [metadata-get]])
  (:require [cheshire.core :as json]
            [clojure.tools.logging :as log]
            [clojure-commons.file-utils :as ft]
            [terrain.clients.data-info :as data-info]
            [terrain.clients.data-info.raw :as data-info-client]
            [terrain.clients.ezid :as ezid]
            [terrain.clients.metadata.raw :as metadata]
            [terrain.util.config :as config]
            [terrain.util.service :as service]))

;; Status Codes.
(def ^:private status-code-completion "Completion")
(def ^:private status-code-failed "Failed")

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

(defn- validate-staging-dest
  [{:keys [path] :as data-item}]
  (let [staging-dest (ft/path-join (config/permanent-id-staging-dir) (ft/basename path))]
    (when (data-info/path-exists? (config/irods-user) staging-dest)
    (throw+ {:type :clojure-commons.exception/exists
             :error "A folder with this name has already been submitted for a Permanent ID request."
             :path staging-dest})))
  data-item)

(defn- validate-publish-dest
  [{:keys [path] :as data-item}]
  (let [publish-dest (ft/path-join (config/permanent-id-publish-dir) (ft/basename path))]
    (when (data-info/path-exists? (config/irods-user) publish-dest)
      (throw+ {:type :clojure-commons.exception/exists
               :error "A folder with this name has already been published."
               :path publish-dest})))
  data-item)

(defn- validate-data-item
  [user data-item]
  (->> data-item
       (validate-owner user)
       validate-staging-dest))

(defn- get-requested-data-item
  "Gets data-info stat for the given ID and checks if the data item is valid for a Permanent ID request."
  [user data-id]
  (->> data-id
       (data-info/stat-by-uuid user)
       (validate-data-item user)))

(defn- parse-service-json
  [response]
  (-> response :body service/decode-json))

(defn- submit-permanent-id-request
  "Submits the request to the metadata create-permanent-id-request endpoint."
  [type folder-id target-type path]
  (-> {:type type
       :target_id folder-id
       :target_type target-type
       :original_path path}
      json/encode
      metadata/create-permanent-id-request
      parse-service-json))

(defn- create-publish-dir
  "Creates the Permanent ID Requests publish directory, if it doesn't already exist."
  []
  (let [publish-path (config/permanent-id-publish-dir)]
    (when-not (data-info/path-exists? (config/irods-user) publish-path)
      (data-info-client/create-dirs (config/irods-user) [publish-path]))))

(defn- create-staging-dir
  "Creates the Permanent ID Requests staging directory, if it doesn't already exist."
  []
  (let [staging-path (config/permanent-id-staging-dir)
        curators     (config/permanent-id-curators-group)]
    (when-not (data-info/path-exists? (config/irods-user) staging-path)
      (log/warn "creating" staging-path "for:" curators)
      (data-info-client/create-dirs (config/irods-user) [staging-path])
      (data-info/share (config/irods-user) [curators] [staging-path] "own"))))

(defn- stage-data-item
  [user {:keys [id path] :as data-item}]
  (let [staged-path (ft/path-join (config/permanent-id-staging-dir) (ft/basename path))
        curators    (config/permanent-id-curators-group)]
    (data-info-client/move-single (config/irods-user) id (config/permanent-id-staging-dir))
    (data-info/share (config/irods-user) [user] [staged-path] "write")
    (data-info/share (config/irods-user) [curators] [staged-path] "own")))

(defn- request-type->shoulder
  [type]
  (case type
    "ARK" (config/ezid-shoulders-ark)
    "DOI" (config/ezid-shoulders-doi)
    (throw+ {:type :clojure-commons.exception/bad-request-field
             :error "No EZID shoulder defined for this Permanent ID Request type."
             :request-type type})))

(defn- parse-ezid-metadata
  [irods-avus metadata]
  (let [metadata (mapcat :avus (:templates metadata))
        format-avus #(vector (:attr %) (:value %))]
    (into {} (concat (map format-avus irods-avus) (map format-avus metadata)))))

(defn- ezid-response->avus
  [response]
  (map (fn [[k v]] {:attr (name k) :value v :unit ""}) response))

(defn- format-perm-id-req-response
  [user {:keys [target_id] :as response}]
  (-> response
      (dissoc :target_id :target_type :original_path)
      (assoc :folder (data-info/stat-by-uuid user (uuidify target_id)))))

(defn- format-perm-id-req-list
  [requests]
  (map
    (partial format-perm-id-req-response (:shortUsername current-user))
    requests))

(defn list-permanent-id-requests
  [params]
  (-> (metadata/list-permanent-id-requests params)
      parse-service-json
      (update-in [:requests] format-perm-id-req-list)))

(defn create-permanent-id-request
  [params body]
  (create-staging-dir)
  (let [{type :type folder-id :folder} (service/decode-json body)
        folder-id                      (uuidify folder-id)
        user                           (:shortUsername current-user)
        {:keys [path] :as folder}      (get-requested-data-item user folder-id)
        target-type                    (validate-request-target-type folder)
        response                       (submit-permanent-id-request type folder-id target-type path)]
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
  (->> (metadata/get-permanent-id-request request-id)
       parse-service-json
       (format-perm-id-req-response (:shortUsername current-user))))

(defn admin-list-permanent-id-requests
  [params]
  (-> (metadata/admin-list-permanent-id-requests params)
      parse-service-json
      (update-in [:requests] format-perm-id-req-list)))

(defn admin-get-permanent-id-request
  [request-id params]
  (->> (metadata/admin-get-permanent-id-request request-id)
       parse-service-json
       (format-perm-id-req-response (:shortUsername current-user))))

(defn update-permanent-id-request
  [request-id params body]
  (->> (metadata/update-permanent-id-request request-id body)
       parse-service-json
       (format-perm-id-req-response (:shortUsername current-user))))

(defn create-permanent-id
  [request-id params body]
  (try+
    (create-publish-dir)
    (let [user                              (:shortUsername current-user)
          {:keys [folder type] :as request} (admin-get-permanent-id-request request-id nil)
          shoulder                          (request-type->shoulder type)
          folder                            (validate-publish-dest folder)
          folder-id                         (uuidify (:id folder))
          {:keys [irods-avus metadata]}     (metadata-get user folder-id)
          template-id                       (-> metadata :templates first :template_id)
          ezid-metadata                     (parse-ezid-metadata irods-avus metadata)
          response                          (ezid/mint-id shoulder ezid-metadata)]
      (data-info-client/admin-add-avus user folder-id (ezid-response->avus response))
      (data-info-client/move-single (config/irods-user) folder-id (config/permanent-id-publish-dir)))
    (catch Object e
      (log/error e)
      (update-permanent-id-request request-id nil (json/encode {:status status-code-failed}))
      (throw+ e)))
  (update-permanent-id-request request-id nil (json/encode {:status status-code-completion})))
