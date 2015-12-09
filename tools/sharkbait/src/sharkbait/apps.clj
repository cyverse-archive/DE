(ns sharkbait.apps
  (:require [clojure.string :as string]
            [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.permissions :as perms]
            [sharkbait.roles :as roles]))

(defn- extract-username
  "Extracts the username of an owner from an app."
  [app]
  (->> (into [] (.getArray (:users app)))
       (filter (partial re-find #"@iplantcollaborative.org$"))
       (first)))

(defn- find-app-owner-subject
  "Finds the subject corresponding to the owner of an app."
  [subjects app]
  (when-let [username (extract-username app)]
    (subjects (string/replace username #"@iplantcollaborative.org$" ""))))

(defn- find-app-owner-membership
  [session subjects de-users-role app]
  (when-let [subject (find-app-owner-subject subjects app)]
    (println "DEBUG: searching for an effective membership")
    (roles/find-effective-membership session de-users-role subject)))

(defn- grant-owner-permission
  "Grants ownership permission to an app."
  [session subjects de-users-role app-resource app]
  (when-let [membership (find-app-owner-membership session subjects de-users-role app)]
    (println "DEBUG: membership =" membership)
    (perms/grant-permission membership perms/own app-resource)))

(defn- register-app
  "Registers an app in Grouper."
  [session subjects de-users-role permission-def folder-name app]
  (let [app-resource (perms/create-permission-resource session permission-def folder-name (:id app))]
    (if (:is_public app)
      (perms/grant-permission de-users-role perms/read app-resource)
      (grant-owner-permission session subjects de-users-role app-resource app))))

(defn register-de-apps
  "Registers all DE apps in Grouper."
  [database session subjects folder-name permission-def-name]
  (let [permission-def       (perms/find-permission-def folder-name permission-def-name)
        de-users-role        (roles/find-role session consts/full-de-users-role-name)]
    (dorun (map (partial register-app session subjects de-users-role permission-def folder-name)
                (db/list-de-apps database)))))
