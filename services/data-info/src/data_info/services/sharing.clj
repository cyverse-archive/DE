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

(defn- share-path*
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

(defn share-path
  [cm user share-with fpath perm]
  (cond (= user share-with)                (skip-share share-with fpath :share-with-self)
        (paths/in-trash? user fpath)       (skip-share share-with fpath :share-from-trash)
        (shared? cm share-with fpath perm) (skip-share share-with fpath :already-shared)
        :else                              (share-path* cm user share-with perm fpath)))

(defn- share-paths
  [cm user share-withs fpaths perm]
  (for [share-with share-withs
        fpath      fpaths]
    (share-path cm user share-with fpath perm)))

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

(defn- remove-inherit-bit?
  [cm user fpath]
  (empty? (remove (comp (conj (set (cfg/irods-admins)) user) :user)
                  (list-user-perms cm fpath))))

(defn- unshare-dir
  "Removes the inherit bit from a directory if the directory is no longer shared with any accounts
   other than iRODS administrative accounts."
  [cm user unshare-with fpath]
  (when (remove-inherit-bit? cm user fpath)
    (log/warn "Removing inherit bit on" fpath)
    (remove-inherits cm fpath)))

(defn- unshare-path*
  "Removes permissions for a user to access a path.  This consists of several steps:

       1. Remove the access permissions for the user.  This is done recursively in case the path
          being unshared is a directory.

       2. If the item being unshared is a directory, perform any directory-specific unsharing
          steps that are required.

       3. Remove the user's read permissions for parent directories in which the user no longer has
          access to any other files or subdirectories."
  [cm user unshare-with fpath]
  (let [trash-base (trash-base-dir (:zone cm) user)
        path-base  (share-path-home fpath)
        base-dirs #{path-base trash-base}]
    (log/warn "Removing permissions on" fpath "from" unshare-with "by" user)
    (remove-permissions cm unshare-with fpath)

    (when (is-dir? cm fpath)
      (log/warn "Unsharing directory" fpath "from" unshare-with "by" user)
      (unshare-dir cm user unshare-with fpath))

    (log/warn "Removing read perms on parents of" fpath "from" unshare-with "by" user)
    (process-parent-dirs
     (partial set-readable cm unshare-with false)
     #(not (or (base-dirs %) (contains-accessible-obj? cm unshare-with %)))
     fpath)
    {:user unshare-with :path fpath}))

(defn unshare-path
  [cm user unshare-with fpath]
  (cond (= user unshare-with)           (skip-share unshare-with fpath :unshare-with-self)
        (shared? cm unshare-with fpath) (unshare-path* cm user unshare-with fpath)
        :else                           (skip-share unshare-with fpath :not-shared)))

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
