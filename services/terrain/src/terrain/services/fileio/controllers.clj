(ns terrain.services.fileio.controllers
  (:use [clj-jargon.init :only [with-jargon]]
        [clojure-commons.error-codes]
        [terrain.util.service :only [success-response]]
        [terrain.util.transformers :only [add-current-user-to-map]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [terrain.services.fileio.actions :as actions]
            [clojure-commons.file-utils :as ft]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [cemerick.url :as url-parser]
            [ring.middleware.multipart-params :as multipart]
            [clj-jargon.item-info :as info]
            [clj-jargon.permissions :as perm]
            [clojure-commons.validators :as ccv]
            [terrain.clients.data-info :as data]
            [terrain.util.config :as cfg]
            [terrain.util.validators :as valid]
            [terrain.services.filesystem.icat :as icat])
  (:import [clojure.lang IPersistentMap]
           [java.io IOException]))


(defn download
  [req-params]
  (let [params (add-current-user-to-map req-params)]
    (ccv/validate-map params {:user string? :path string?})
    (actions/download (:user params) (:path params))))


(defn- store-from-form
  [user dest-dir {istream :stream filename :filename}]
  (when-not (valid/good-string? filename)
    (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD :path filename}))
  (let [dest-path (ft/path-join dest-dir filename)]
    (actions/upload (icat/jargon-cfg) user dest-path istream)
    dest-path))


(defn upload
  "This is the business logic of behind the POST /secured/fileio/upload endpoint.

   Params:
     user - the who will own the data object being uploaded
     dest - the value of the dest query parameter
     req  - the ring request map"
  [^String user ^String dest ^IPersistentMap req]
  (ccv/validate-field "dest" dest)
  (let [store                   (partial store-from-form user dest)
        {{file "file"} :params} (multipart/multipart-params-request req {:store store})]
    (when-not file
      (throw+ {:error_code ERR_MISSING_FORM_FIELD :field "file"}))
    (success-response {:file (data/path-stat user file)})))


(defn- url-filename
  [address]
  (let [parsed-url (url-parser/url address)]
    (when-not (:protocol parsed-url)
      (throw+ {:error_code ERR_INVALID_URL
                :url address}))

    (when-not (:host parsed-url)
      (throw+ {:error_code ERR_INVALID_URL
               :url address}))

    (if-not (string/blank? (:path parsed-url))
      (ft/basename (:path parsed-url))
      (:host parsed-url))))

(defn urlupload
  [req-params req-body]
  (let [params (add-current-user-to-map req-params)
        body   (valid/parse-body (slurp req-body))]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :address string?})
    (let [user    (:user params)
          dest    (string/trim (:dest body))
          addr    (string/trim (:address body))
          fname   (url-filename addr)]
      (log/warn (str "User: " user))
      (log/warn (str "Dest: " dest))
      (log/warn (str "Fname: " fname))
      (log/warn (str "Addr: " addr))
      (actions/urlimport user addr fname dest))))

(defn save
  [req-params req-body]
  (log/info "Detected params: " req-params)
  (let [params (add-current-user-to-map req-params)
        body   (valid/parse-body (slurp req-body))]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :content string?})
    (let [user      (:user params)
          dest      (string/trim (:dest body))
          content   (:content body)
          file-size (count (.getBytes content "UTF-8"))]
      (with-jargon (icat/jargon-cfg) :client-user user [cm]
        (when-not (info/exists? cm dest)
          (throw+ {:error_code ERR_DOES_NOT_EXIST :path dest}))
        (when-not (perm/is-writeable? cm user dest)
          (throw+ {:error_code ERR_NOT_WRITEABLE :path dest}))
        (when (> file-size (cfg/fileio-max-edit-file-size))
          (throw+ {:error_code "ERR_FILE_SIZE_TOO_LARGE"
                   :path       dest
                   :size       file-size}))
        (try+
          (with-in-str content
            (actions/save cm *in* user dest))
          (catch Object e
            (log/warn e)
            (throw+))))
      (success-response {:file (data/path-stat user dest)}))))


(defn saveas
  [req-params req-body]
  (let [params (add-current-user-to-map req-params)
        body   (valid/parse-body (slurp req-body))]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :content string?})
    (let [user (:user params)
          dest (string/trim (:dest body))
          cont (:content body)]
      (with-jargon (icat/jargon-cfg) :client-user user [cm]
        (when-not (info/exists? cm (ft/dirname dest))
          (throw+ {:error_code ERR_DOES_NOT_EXIST :path (ft/dirname dest)}))
        (when (info/exists? cm dest)
          (throw+ {:error_code ERR_EXISTS :path dest}))
        (with-in-str cont
          (actions/store cm *in* user dest)))
      (success-response {:file (data/path-stat user dest)}))))
