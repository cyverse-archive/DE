(ns data-info.services.write
  (:use [clj-jargon.init :only [with-jargon]])
  (:require [clojure-commons.file-utils :as ft]
            [clojure-commons.error-codes :as ce]
            [clj-jargon.item-info :as info]
            [clj-jargon.item-ops :as ops]
            [ring.middleware.multipart-params :as multipart]
            [data-info.services.stat :as stat]
            [data-info.util.config :as cfg]
            [data-info.util.validators :as validators]))

(defn- save-file-contents
  "Save an istream to a destination. Relies on upstream functions to validate."
  [cm istream user dest-path]
  (ops/copy-stream cm istream user dest-path)
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
    (save-file-contents cm istream user dest-path)))

(defn- overwrite-path
  "Save new contents for the file at dest-path from istream.

   Error if there is no file at that path or the user lacks write permissions thereupon."
  [cm istream user dest-path]
  (validators/user-exists cm user)
  (validators/path-exists cm dest-path)
  (validators/path-writeable cm user dest-path)
  (save-file-contents cm istream user dest-path))

(defn- multipart-handler
  "When partially applied, creates a storage handler for
   ring.middleware.multipart-params/multipart-params-request which stores the file in iRODS."
  [user dest-dir {istream :stream filename :filename}]
  (validators/good-pathname filename)
  (with-jargon (cfg/jargon-cfg) [cm]
    (let [dest-path (ft/path-join dest-dir filename)]
      (create-at-path cm istream user dest-path))))

(defn wrap-multipart
  "Middleware which saves a file from a multipart request."
  [handler]
  (fn [{{:keys [user dest]} :params :as request}]
    (handler (multipart/multipart-params-request request {:store (partial multipart-handler user dest)}))))

(defn do-upload
  "Returns a path stat after a file has been uploaded. Intended to only be used with wrap-multipart."
  [{:keys [user]} file]
  (with-jargon (cfg/jargon-cfg) [cm]
    {:file (stat/path-stat cm user file)}))
