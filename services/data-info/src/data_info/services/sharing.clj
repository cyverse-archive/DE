(ns data-info.services.sharing
  (:use [clj-jargon.init :only [with-jargon]]
        [clj-jargon.item-info :only [trash-base-dir is-dir?]]
        [clj-jargon.permissions]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [clojure-commons.file-utils :as ft]
            [cemerick.url :as url]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [data-info.util.logging :as dul]
            [data-info.util.paths :as paths]
            [data-info.util.config :as cfg]
            [data-info.util.validators :as validators]))

(defn- shared?
  ([cm share-with fpath]
     (:read (permissions cm share-with fpath)))
  ([cm share-with fpath desired-perm]
     (let [curr-perm (permission-for cm share-with fpath)]
       (= curr-perm desired-perm))))

(defn- skip-share
  [user path reason]
  (log/warn "Skipping share of" path "with" user "because:" reason)
  {:user    user
   :path    path
   :reason  reason
   :skipped true})

(defn- share-path-home
  "Returns the home directory that a shared file is under."
  [share-path]
  (string/join "/" (take 4 (string/split share-path #"\/"))))

(defn- share-path
  "Shares a path with a user. This consists of the following steps:

       1. The parent directories up to the sharer's home directory need to be marked as readable
          by the sharee. Othwerwise, any files that are shared will be orphaned in the UI.

       2. If the shared item is a directory then the inherit bit needs to be set so that files
          that are uploaded into the directory will also be shared.

       3. The permissions are set on the item being shared. This is done recursively in case the
          item being shared is a directory."
  [cm user share-with perm fpath]
  (let [hdir      (share-path-home fpath)
        trash-dir (trash-base-dir (:zone cm) user)
        base-dirs #{hdir trash-dir}]
    (log/warn fpath "is being shared with" share-with "by" user)
    (process-parent-dirs (partial set-readable cm share-with true) #(not (base-dirs %)) fpath)

    (when (is-dir? cm fpath)
      (log/warn fpath "is a directory, setting the inherit bit.")
      (set-inherits cm fpath))

    (when-not (is-readable? cm share-with hdir)
      (log/warn share-with "is being given read permissions on" hdir "by" user)
      (set-permission cm share-with hdir :read false))

    (log/warn share-with "is being given recursive permissions (" perm ") on" fpath)
    (set-permission cm share-with fpath (keyword perm) true)

    {:user share-with :path fpath}))

(defn- share-paths
  [cm user share-withs fpaths perm]
  (for [share-with share-withs
        fpath      fpaths]
    (cond (= user share-with)                (skip-share share-with fpath :share-with-self)
          (paths/in-trash? user fpath)       (skip-share share-with fpath :share-from-trash)
          (shared? cm share-with fpath perm) (skip-share share-with fpath :already-shared)
          :else                              (share-path cm user share-with perm fpath))))

(defn- share
  [cm user share-withs fpaths perm]
  (validators/user-exists cm user)
  (validators/all-users-exist cm share-withs)
  (validators/all-paths-exist cm fpaths)
  (validators/user-owns-paths cm user fpaths)

  (let [keyfn      #(if (:skipped %) :skipped :succeeded)
        share-recs (group-by keyfn (share-paths cm user share-withs fpaths perm))
        sharees    (map :user (:succeeded share-recs))
        home-dir   (paths/user-home-dir user)]
    {:user        sharees
     :path        fpaths
     :skipped     (map #(dissoc % :skipped) (:skipped share-recs))
     :permission  perm}))

(defn- anon-file-url
  [p]
  (let [aurl (url/url (cfg/anon-files-base))]
    (str (-> aurl (assoc :path (ft/path-join (:path aurl) (string/replace p #"^\/" "")))))))

(defn- anon-files-urls
  [paths]
  (into {} (map #(vector %1 (anon-file-url %1)) paths)))

(defn- anon-files
  [user paths]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (validators/all-paths-exist cm paths)
    (validators/paths-are-files cm paths)
    (validators/user-owns-paths cm user paths)
    (log/warn "Giving read access to" (cfg/anon-user) "on:" (string/join " " paths))
    (share cm user [(cfg/anon-user)] paths :read)
    {:user user :paths (anon-files-urls paths)}))

(defn do-anon-files
  [{:keys [user]} {:keys [paths]}]
  (anon-files user (mapv ft/rm-last-slash paths)))

(with-pre-hook! #'do-anon-files
  (fn [params body]
    (dul/log-call "do-anon-files" params body)
    (validators/validate-num-paths (:paths body))))

(with-post-hook! #'do-anon-files (dul/log-func "do-anon-files"))
