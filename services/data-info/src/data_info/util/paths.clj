(ns data-info.util.paths
  (:require [clojure-commons.file-utils :as ft]
            [clj-jargon.item-info :as item]
            [data-info.util.config :as cfg]))


(def IPCRESERVED "ipc-reserved-unit")
(def IPCSYSTEM "ipc-system-avu")


(defn ^String user-home-dir
  [^String user]
  (ft/path-join (cfg/irods-home) user))


(defn ^String base-trash-path
  []
  (item/trash-base-dir (cfg/irods-zone) (cfg/irods-user)))


(defn ^String user-trash-path
  [^String user]
  (ft/path-join (base-trash-path) user))


(defn ^Boolean in-trash?
  [^String user ^String fpath]
  (.startsWith fpath (base-trash-path)))

(defn- dir-equal?
  [path comparison]
  (apply = (map ft/rm-last-slash [path comparison])))

(defn- user-trash-dir?
  [user abs]
  (dir-equal? abs (user-trash-path user)))
(defn- sharing? [abs] (dir-equal? abs (cfg/irods-home)))
(defn- community? [abs] (dir-equal? abs (cfg/community-data)))

(defn path->label
  "Generates a label given an absolute path in iRODS."
  [user id]
  (cond
    (user-trash-dir? user id)             "Trash"
    (sharing? id)                         "Shared With Me"
    (community? id)                       "Community Data"
    :else                                 (ft/basename id)))
