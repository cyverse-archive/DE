(ns terrain.services.filesystem.metadata
  (:use [clojure-commons.error-codes]
        [clojure-commons.validators]
        [terrain.services.filesystem.common-paths]
        [terrain.services.filesystem.validators]
        [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.string :as string]
            [clojure-commons.file-utils :as ft]
            [cheshire.core :as json]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [terrain.clients.metadata :as metadata]
            [terrain.clients.data-info :as data]
            [terrain.clients.data-info.raw :as data-raw]
            [terrain.services.filesystem.validators :as validators]
            [terrain.util.service :as service]))

(defn do-metadata-get
  "Entrypoint for the API."
  [{user :user} data-id]
  (data-raw/get-avus user (uuidify data-id)))

(with-pre-hook! #'do-metadata-get
  (fn [params data-id]
    (log-call "do-metadata-get" data-id params)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-get (log-func "do-metadata-get"))

(defn do-metadata-set
  "Entrypoint for the API that calls (metadata-set).
   Body is a map with :irods-avus and :metadata keys."
  [data-id {user :user} body]
  (data-raw/set-avus user (uuidify data-id) body))

(with-pre-hook! #'do-metadata-set
  (fn [data-id params body]
    (log-call "do-metadata-set" data-id params body)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-set (log-func "do-metadata-set"))

(defn do-metadata-copy
  "Entrypoint for the API that calls (metadata-copy)."
  [{:keys [user]} data-id body]
  (data-raw/metadata-copy user data-id body))

(with-pre-hook! #'do-metadata-copy
  (fn [params data-id body]
    (log-call "do-metadata-copy" params data-id body)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-copy (log-func "do-metadata-copy"))

(defn do-metadata-save
  "Forwards request to data-info service."
  [data-id params body]
  (data-raw/save-metadata (:user params) data-id (:dest body) (:recursive body)))

(with-pre-hook! #'do-metadata-save
  (fn [data-id params body]
    (log-call "do-metadata-save" data-id params body)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-save (log-func "do-metadata-save"))

(defn parse-metadata-csv-file
  "Forwards request to data-info service by looking up the target data-id from the `dest` param."
  [{:keys [user]} {:keys [dest] :as params}]
  (let [dest-id (data/uuid-for-path user dest)]
    (data-raw/metadata-csv-parser user dest-id params)))

(with-pre-hook! #'parse-metadata-csv-file
  (fn [user-info params]
    (log-call "parse-metadata-csv-file" user-info params)
    (validate-map user-info {:user string?})
    (validate-map params {:dest string?})))

(with-post-hook! #'parse-metadata-csv-file (log-func "parse-metadata-csv-file"))
