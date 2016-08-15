(ns data-info.services.write
  (:use [clj-jargon.init :only [with-jargon]])
  (:require [clojure-commons.file-utils :as ft]
            [clojure-commons.error-codes :as ce]
            [clj-jargon.item-info :as info]
            [clj-jargon.item-ops :as ops]
            [ring.middleware.multipart-params :as multipart]
            [data-info.services.stat :as stat]
            [data-info.services.uuids :as uuids]
            [data-info.util.config :as cfg]
            [data-info.util.irods :as irods]
            [data-info.util.validators :as validators]))

(defn- save-file-contents
  "Save an istream to a destination. Relies on upstream functions to validate."
  [cm istream user dest-path set-owner?]
  (ops/copy-stream cm istream user dest-path :set-owner? set-owner?)
  dest-path)

(defn- create-at-path
  "Create a new file at dest-path from istream.

   Error if the path exists or if the destination directory does not exist or is not writeable."
  [cm istream user dest-path]
  (let [dest-dir (ft/dirname dest-path)]
    (validators/user-exists cm user)
    (validators/path-not-exists cm dest-path)
    (validators/path-exists cm dest-dir)
    (validators/path-writeable cm user dest-dir)
    (save-file-contents cm istream user dest-path true)))

(defn- overwrite-path
  "Save new contents for the file at dest-path from istream.

   Error if there is no file at that path or the user lacks write permissions thereupon."
  [cm istream user dest-path]
  (validators/user-exists cm user)
  (validators/path-exists cm dest-path)
  (validators/path-is-file cm dest-path)
  (validators/path-writeable cm user dest-path)
  (save-file-contents cm istream user dest-path false))

(defn- multipart-create-handler
  "When partially applied, creates a storage handler for
   ring.middleware.multipart-params/multipart-params-request which stores the file in iRODS."
  [user dest-dir {istream :stream filename :filename}]
  (validators/good-pathname filename)
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      (let [dest-path (ft/path-join dest-dir filename)]
        (create-at-path cm istream user dest-path)))))

(defn wrap-multipart-create
  "Middleware which saves a new file from a multipart request."
  [handler]
  (fn [{{:keys [user dest]} :params :as request}]
    (handler (multipart/multipart-params-request request {:store (partial multipart-create-handler user dest)}))))

(defn- multipart-overwrite-handler
  "When partially applied, creates a storage handler for
   ring.middleware.multipart-params/multipart-params-request which overwrites the file in iRODS."
  [user data-id {istream :stream}]
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      (let [path (ft/rm-last-slash (uuids/path-for-uuid cm user data-id))]
        (overwrite-path cm istream user path)))))

(defn wrap-multipart-overwrite
  "Middleware which overwrites a file's contents from a multipart request."
  [handler]
  (fn [{{:keys [user data-id]} :params :as request}]
    (handler (multipart/multipart-params-request request {:store (partial multipart-overwrite-handler user data-id)}))))

(defn do-upload
  "Returns a path stat after a file has been uploaded. Intended to only be used with wrap-multipart-* middlewares."
  [{:keys [user]} file]
  (irods/catch-jargon-io-exceptions
    (with-jargon (cfg/jargon-cfg) [cm]
      {:file (stat/path-stat cm user file)})))
