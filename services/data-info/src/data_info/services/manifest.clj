(ns data-info.services.manifest
  (:use [data-info.services.sharing :only [anon-file-url anon-readable?]]
        [clj-jargon.metadata :only [get-attribute attribute?]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.logging :as log]
            [clojure-commons.file-utils :as ft]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [data-info.util.validators :as validators]
            [data-info.services.stat :as stat]
            [data-info.services.uuids :as uuids]
            [data-info.util.irods :as irods]
            [data-info.util.logging :as dul]
            [tree-urls-client.core :as tree]
            [data-info.util.config :as cfg]))

(def ^:private coge-attr "ipc-coge-link")

;; this is usually going to be the vector case, but the previous behavior is the map case so it's preserved
(defn- postprocess-tree-urls
  [tree-urls]
  (if (map? tree-urls)
      (:tree-urls tree-urls)
      (vec tree-urls)))

(defn- extract-tree-urls
  [cm fpath]
  (if (attribute? cm fpath (cfg/tree-urls-attr))
    (-> (get-attribute cm fpath (cfg/tree-urls-attr))
      first
      :value
      ft/basename
      tree/get-tree-urls
      postprocess-tree-urls)
    []))

(defn- extract-coge-view
  [cm fpath]
  (if (attribute? cm fpath coge-attr)
    (mapv (fn [{url :value} idx] {:label (str "gene_" idx) :url url})
          (get-attribute cm fpath coge-attr) (range))
    []))

(defn- format-anon-files-url
  [fpath]
  {:label "anonymous" :url (anon-file-url fpath)})

(defn- extract-urls
  [cm fpath]
  (let [urls (concat (extract-tree-urls cm fpath) (extract-coge-view cm fpath))]
    (vec (if (anon-readable? cm fpath)
           (conj urls (format-anon-files-url fpath))
           urls))))

(defn- manifest-map
  [cm user {:keys [path] :as file}]
  (-> (select-keys file [:content-type :infoType])
      (assoc :urls (extract-urls cm path))))

(defn- manifest
  [cm user file]
  (let [path (ft/rm-last-slash (:path file))]
    (validators/user-exists cm user)
    (validators/path-exists cm path)
    (validators/path-is-file cm path)
    (validators/path-readable cm user path)
    (manifest-map cm user file)))

(defn do-manifest-uuid
  [user data-id]
  (irods/with-jargon-exceptions [cm]
    (let [file (uuids/path-stat-for-uuid cm user data-id)]
      (manifest cm user file))))

(with-pre-hook! #'do-manifest-uuid
  (fn [user data-id]
    (dul/log-call "do-manifest-uuid" user data-id)))

(with-post-hook! #'do-manifest-uuid (dul/log-func "do-manifest-uuid"))
