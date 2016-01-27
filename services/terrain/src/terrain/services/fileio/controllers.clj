(ns terrain.services.fileio.controllers
  (:use [clojure-commons.error-codes]
        [terrain.util.service :only [success-response]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [terrain.services.fileio.actions :as actions]
            [clojure-commons.file-utils :as ft]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [cemerick.url :as url-parser]
            [dire.core :refer [with-pre-hook!]]
            [ring.middleware.multipart-params :as multipart]
            [clojure-commons.validators :as ccv]
            [terrain.clients.data-info :as data]
            [terrain.clients.data-info.raw :as data-raw])
  (:import [clojure.lang IPersistentMap]
           [java.io IOException ByteArrayInputStream]))


(defn download
  [params]
  (actions/download (:user params) (:path params)))

(with-pre-hook! #'download
  (fn [params]
    (ccv/validate-map params {:user string? :path string?})))


(defn- store-from-form
  [user dest-dir {istream :stream filename :filename content-type :content-type}]
  (data-raw/upload-file user dest-dir filename content-type istream))

(defn upload
  "This is the business logic of behind the POST /secured/fileio/upload endpoint.

   Params:
     user - the who will own the data object being uploaded
     dest - the value of the dest query parameter
     req  - the ring request map"
  [{:keys [user dest]} ^IPersistentMap req]
  (let [store                   (partial store-from-form user dest)
        {{file-info "file"} :params} (multipart/multipart-params-request req {:store store})]
    (success-response file-info)))

(with-pre-hook! #'upload
  (fn [params req]
    (ccv/validate-map params {:user string? :dest string?})))

(defn saveas
  "Save a file to a location given the content in a (utf-8) string.

   This reuses the upload endpoint logic by converting the string into an input stream to be sent to data-info."
  [{:keys [user]} {:keys [dest content]}]
  (let [dest (string/trim dest)
        dir  (ft/dirname dest)
        file (ft/basename dest)
        istream (ByteArrayInputStream. (.getBytes content "UTF-8"))
        info (data-raw/upload-file user dir file "application/octet-stream" istream)]
    (success-response info)))

(with-pre-hook! #'saveas
  (fn [params body]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :content string?})))

(defn save
  [{:keys [user]} {:keys [dest content]}]
  (let [dest      (string/trim dest)
        istream   (ByteArrayInputStream. (.getBytes content "UTF-8"))
        info      (data/overwrite-file user dest istream)]
    (success-response info)))

(with-pre-hook! #'save
  (fn [params body]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :content string?})))

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
  [params body]
    (let [user    (:user params)
          dest    (string/trim (:dest body))
          addr    (string/trim (:address body))
          fname   (url-filename addr)]
      (log/warn (str "User: " user))
      (log/warn (str "Dest: " dest))
      (log/warn (str "Fname: " fname))
      (log/warn (str "Addr: " addr))
      (actions/urlimport user addr fname dest)))

(with-pre-hook! #'urlupload
  (fn [params body]
    (ccv/validate-map params {:user string?})
    (ccv/validate-map body {:dest string? :address string?})))
