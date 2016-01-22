(ns terrain.services.filesystem.validators
  (:use [clj-jargon.item-info]
        [clj-jargon.permissions]
        [clj-jargon.tickets]
        [clj-jargon.users]
        [clj-icat-direct.icat :as icat]
        [clojure-commons.error-codes]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [terrain.services.filesystem.common-paths :as cp]
            [terrain.util.config :as cfg]))

(defn not-superuser
  [user]
  (when (cp/super-user? user)
    (throw+ {:type :clojure-commons.exception/not-authorized
             :user user})))

(defn num-paths-okay?
  [path-count]
  (<= path-count (cfg/fs-max-paths-in-request)))

(defn- validate-path-count
  [count]
  (if-not (num-paths-okay? count)
    (throw+ {:error_code "ERR_TOO_MANY_PATHS"
             :count count
             :limit (cfg/fs-max-paths-in-request)})))

(defn validate-num-paths
  [paths]
  (validate-path-count (count paths)))

(defn validate-num-paths-under-folder
  [user folder]
  (let [total (icat/number-of-all-items-under-folder user (cfg/irods-zone) folder)]
    (validate-path-count total)))

(defn validate-num-paths-under-paths
  [user paths]
  (let [sum-fn #(+ %1 (icat/number-of-all-items-under-folder user (cfg/irods-zone) %2))
        total (reduce sum-fn 0 paths)]
    (validate-path-count total)))

(defn user-exists
  [cm user]
  (when-not (user-exists? cm user)
    (throw+ {:error_code ERR_NOT_A_USER
             :user user})))

(defn all-users-exist
  [cm users]
  (when-not (every? #(user-exists? cm %) users)
    (throw+ {:error_code ERR_NOT_A_USER
             :users (filterv #(not (user-exists? cm %1)) users)})))

(defn path-exists
  [cm path]
  (when-not (exists? cm path)
    (throw+ {:error_code ERR_DOES_NOT_EXIST
             :path path})))

(defn all-paths-exist
  [cm paths]
  (when-not (every? #(exists? cm %) paths)
    (throw+ {:error_code ERR_DOES_NOT_EXIST
             :paths (filterv #(not (exists? cm  %1)) paths)})))

(defn no-paths-exist
  [cm paths]
  (when (some #(exists? cm %) paths)
    (throw+ {:error_code ERR_EXISTS
             :paths (filterv #(exists? cm %) paths)})))

(defn path-readable
  [cm user path]
  (when-not (is-readable? cm user path)
    (throw+ {:error_code ERR_NOT_READABLE
             :path path
             :user user})))

(defn all-paths-readable
  [cm user paths]
  (when-not (every? #(is-readable? cm user %) paths)
    (throw+ {:error_code ERR_NOT_READABLE
             :path (filterv #(not (is-readable? cm user %)) paths)})))

(defn path-writeable
  [cm user path]
  (when-not (is-writeable? cm user path)
    (throw+ {:error_code ERR_NOT_WRITEABLE
             :path path})))

(defn all-paths-writeable
  [cm user paths]
  (when-not (paths-writeable? cm user paths)
    (throw+ {:paths (filterv #(not (is-writeable? cm user %)) paths)
             :error_code ERR_NOT_WRITEABLE})))

(defn path-not-exists
  [cm path]
  (when (exists? cm path)
    (throw+ {:path path
             :error_code ERR_EXISTS})))

(defn path-is-dir
  [cm path]
  (when-not (is-dir? cm path)
    (throw+ {:error_code ERR_NOT_A_FOLDER
             :path path})))

(defn path-is-file
  [cm path]
  (when-not (is-file? cm path)
    (throw+ {:error_code ERR_NOT_A_FILE
             :path path})))

(defn paths-are-files
  [cm paths]
  (when-not (every? #(is-file? cm %) paths)
    (throw+ {:error_code ERR_NOT_A_FILE
             :path (filterv #(not (is-file? cm %)) paths)})))

(defn path-satisfies-predicate
  [cm path pred-func? pred-err]
  (when-not (pred-func? cm  path)
    (throw+ {:paths path
             :error_code pred-err})))

(defn paths-satisfy-predicate
  [cm paths pred-func? pred-err]
  (when-not  (every? true? (mapv #(pred-func? cm %) paths))
    (throw+ {:error_code pred-err
             :paths (filterv #(not (pred-func? cm %)) paths)})))

(defn ownage?
  [cm user path]
  (owns? cm user path))

(defn user-owns-path
  [cm user path]
  (when-not (owns? cm user path)
    (throw+ {:error_code ERR_NOT_OWNER
             :user user
             :path path})))

(defn user-owns-paths
  [cm user paths]
  (let [belongs-to? (partial ownage? cm user)]
    (when-not (every? belongs-to? paths)
      (throw+ {:error_code ERR_NOT_OWNER
               :user user
               :paths (filterv #(not (belongs-to? %)) paths)}))))

(defn ticket-exists
  [cm user ticket-id]
  (when-not (ticket? cm (:username cm) ticket-id)
    (throw+ {:error_code ERR_TICKET_DOES_NOT_EXIST
             :user user
             :ticket-id ticket-id})))

(defn ticket-does-not-exist
  [cm user ticket-id]
  (when (ticket? cm (:username cm) ticket-id)
    (throw+ {:error_code ERR_TICKET_EXISTS
             :user user
             :ticket-id ticket-id})))

(defn all-tickets-exist
  [cm user ticket-ids]
  (when-not (every? #(ticket? cm (:username cm) %) ticket-ids)
    (throw+ {:ticket-ids (filterv #(not (ticket? cm (:username cm) %)) ticket-ids)
             :error_code ERR_TICKET_DOES_NOT_EXIST})))

(defn all-tickets-nonexistant
  [cm user ticket-ids]
  (when (some #(ticket? cm (:username cm) %) ticket-ids)
    (throw+ {:ticket-ids (filterv #(ticket? cm (:username cm) %) ticket-ids)
             :error_code ERR_TICKET_EXISTS})))

(defn duplicate-attrs-error
  "Throws an ERR_NOT_UNIQUE error with the given duplicates list."
  [duplicates]
  (throw+ {:error_code ERR_NOT_UNIQUE
           :message    "Some paths already have metadata with some of the given attributes."
           :duplicates duplicates}))
