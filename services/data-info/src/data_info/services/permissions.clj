(ns data-info.services.permissions
  (:use [clj-jargon.init :only [with-jargon]])
  (:require [clojure.tools.logging :as log]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [clj-jargon.permissions :as perm]
            [clojure-commons.file-utils :as ft]
            [data-info.services.sharing :as sharing]
            [data-info.services.uuids :as uuids]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as dul]
            [data-info.util.validators :as validators]))

(defn filtered-user-perms
  [cm user abspath]
  (let [filtered-users (set (conj (cfg/perms-filter) user (cfg/irods-user)))]
    (filter
     #(not (contains? filtered-users (:user %1)))
     (perm/list-user-perm cm abspath))))

(defn list-permissions*
  [cm user path]
  {:user-permissions (filtered-user-perms cm user path)})

(defn list-permissions
  [{:keys [user]} data-id]
  (with-jargon (cfg/jargon-cfg) [cm]
    (let [path (ft/rm-last-slash (:path (uuids/path-for-uuid user data-id)))]
      (validators/user-exists cm user)
      (validators/path-readable cm user path)
      (list-permissions* cm user path))))

(with-pre-hook! #'list-permissions
  (fn [params data-id]
    (dul/log-call "list-permissions" params data-id)))

(with-post-hook! #'list-permissions (dul/log-func "list-permissions"))

(defn add-permission
  [{:keys [user]} data-id share-with permission]
  (with-jargon (cfg/jargon-cfg) [cm]
    (let [path (ft/rm-last-slash (:path (uuids/path-for-uuid user data-id)))]
      (validators/user-exists cm user)
      (validators/user-owns-path cm user path)
      (validators/user-exists cm share-with)
      (sharing/share-path cm user share-with path permission)
      (list-permissions* cm user path))))

(with-pre-hook! #'add-permission
  (fn [params data-id share-with permission]
    (dul/log-call "add-permission" params data-id share-with permission)))

(with-post-hook! #'add-permission (dul/log-func "add-permission"))

(defn remove-permission
  [{:keys [user]} data-id unshare-with]
  (with-jargon (cfg/jargon-cfg) [cm]
    (let [path (ft/rm-last-slash (:path (uuids/path-for-uuid user data-id)))]
      (validators/user-exists cm user)
      (validators/user-owns-path cm user path)
      (validators/user-exists cm unshare-with)
      (sharing/unshare-path cm user unshare-with path)
      (list-permissions* cm user path))))

(with-pre-hook! #'remove-permission
  (fn [params data-id unshare-with]
    (dul/log-call "remove-permission" params data-id unshare-with)))

(with-post-hook! #'remove-permission (dul/log-func "remove-permission"))
