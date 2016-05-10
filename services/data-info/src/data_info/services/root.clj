(ns data-info.services.root
  (:use [clj-jargon.init :only [with-jargon]]
        [clj-jargon.item-info :only [exists?]]
        [clj-jargon.permissions :only [set-permission owns?]])
  (:require [clojure.tools.logging :as log]
            [clj-jargon.item-info :as item]
            [clj-jargon.item-ops :as ops]
            [clojure-commons.file-utils :as ft]
            [data-info.services.stat :as stat]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as dul]
            [data-info.util.paths :as paths]
            [data-info.util.validators :as validators]
            [dire.core :refer [with-pre-hook! with-post-hook!]]))

(defn- get-root
  [cm user root-path]
  (validators/path-readable cm user root-path) ;; CORE-7638; otherwise a 'nil' permission can pop up and cause issues
  (-> (stat/path-stat cm user root-path)
      (select-keys [:id :label :path :date-created :date-modified :permission])))

(defn- make-root
  [cm user root-path]
  (when-not (exists? cm root-path)
    (log/info "[make-root] Creating" root-path "for" user)
    (ops/mkdirs cm root-path))

  (when-not (owns? cm user root-path)
    (log/info "[make-root] Setting own permissions on" root-path "for" user)
    (set-permission cm user root-path :own))

  (get-root cm user root-path))

(defn root-listing
  [user]
  (let [uhome          (paths/user-home-dir user)
        utrash         (paths/user-trash-path user)
        community-data (ft/rm-last-slash (cfg/community-data))
        irods-home     (ft/rm-last-slash (cfg/irods-home))]
    (log/debug "[root-listing]" "for" user)
    (with-jargon (cfg/jargon-cfg) [cm]
      (validators/user-exists cm user)
      {:roots (remove nil?
                [(get-root cm user uhome)
                 (get-root cm user community-data)
                 (get-root cm user irods-home)
                 (make-root cm user utrash)])})))

(defn do-root-listing
  [user]
  (root-listing user))

(with-pre-hook! #'do-root-listing
  (fn [user]
    (dul/log-call "do-root-listing" user)))

(with-post-hook! #'do-root-listing (dul/log-func "do-root-listing"))
