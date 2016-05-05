(ns data-info.services.stat
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [slingshot.slingshot :refer [throw+]]
            [clj-icat-direct.icat :as icat]
            [clj-jargon.by-uuid :as uuid]
            [clj-jargon.init :refer [with-jargon]]
            [clj-jargon.item-info :as info]
            [clj-jargon.metadata :as meta]
            [clj-jargon.permissions :as perm]
            [clj-jargon.users :as users]
            [clojure-commons.file-utils :as ft]
            [clojure-commons.validators :as cv]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as dul]
            [data-info.util.irods :as irods]
            [data-info.util.paths :as paths]
            [data-info.util.validators :as validators])
  (:import [clojure.lang IPersistentMap]))


(defn- get-types
  "Gets all of the filetypes associated with path."
  [cm user path]
  (validators/path-exists cm path)
  (validators/user-exists cm user)
  (validators/path-readable cm user path)
  (let [path-types (meta/get-attribute cm path (cfg/type-detect-type-attribute))]
    (log/info "Retrieved types" path-types "from" path "for" (str user "."))
    (or (:value (first path-types) ""))))


(defn- count-shares
  [cm user path]
  (let [filter-users (set (conj (cfg/perms-filter) user (cfg/irods-user)))
        other-perm?  (fn [perm] (not (contains? filter-users (:user perm))))]
    (count (filterv other-perm? (perm/list-user-perms cm path)))))


(defn- merge-counts
  [stat-map cm user path]
  (if (info/is-dir? cm path)
    (assoc stat-map
      :file-count (icat/number-of-files-in-folder user (cfg/irods-zone) path)
      :dir-count  (icat/number-of-folders-in-folder user (cfg/irods-zone) path))
    stat-map))


(defn- merge-shares
  [stat-map cm user path]
  (if (perm/owns? cm user path)
    (assoc stat-map :share-count (count-shares cm user path))
    stat-map))


(defn- merge-label
  [stat-map user path]
  (assoc stat-map
         :label (paths/path->label user path)))


(defn- merge-type-info
  [stat-map cm user path]
  (if-not (info/is-dir? cm path)
    (assoc stat-map
      :infoType     (get-types cm user path)
      :content-type (irods/detect-media-type cm path))
    stat-map))


(defn ^IPersistentMap decorate-stat
  [^IPersistentMap cm ^String user ^IPersistentMap stat]
  (let [path (:path stat)]
    (-> stat
      (assoc :id         (-> (meta/get-attribute cm path uuid/uuid-attr) first :value)
             :permission (perm/permission-for cm user path))
      (merge-label user path)
      (merge-type-info cm user path)
      (merge-shares cm user path)
      (merge-counts cm user path))))


(defn ^IPersistentMap path-stat
  [^IPersistentMap cm ^String user ^String path]
  (let [path (ft/rm-last-slash path)]
    (log/debug "[path-stat] user:" user "path:" path)
    (validators/path-exists cm path)
    (decorate-stat cm user (info/stat cm path))))

(defn ^IPersistentMap uuid-stat
  [^IPersistentMap cm ^String user uuid]
  (log/debug "[uuid-stat] user:" user "uuid:" uuid)
  (let [path (uuid/get-path cm uuid)]
    (path-stat cm user path)))

(defn do-stat
  [{user :user validation :validation-behavior} {paths :paths uuids :ids}]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (validators/all-uuids-exist cm uuids)
    (let [uuid-paths (map (juxt (comp keyword str) (partial uuid/get-path cm)) uuids)
          all-paths (into paths (map second uuid-paths))]
      (validators/all-paths-exist cm all-paths)
      (case (keyword validation)
            :own (validators/user-owns-paths cm user all-paths)
            :write (validators/all-paths-writeable cm user all-paths)
            :read (validators/all-paths-readable cm user all-paths)
            (validators/all-paths-readable cm user all-paths))
      {:paths (into {} (map (juxt keyword (partial path-stat cm user)) paths))
       :ids (into {} (map (juxt first #(path-stat cm user (second %))) uuid-paths))})))

(with-pre-hook! #'do-stat
  (fn [params body]
    (dul/log-call "do-stat" params body)
    (validators/validate-num-paths (:paths body))
    (validators/validate-num-paths (:ids body))))

(with-post-hook! #'do-stat (dul/log-func "do-stat"))
