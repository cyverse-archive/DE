(ns sharkbait.apps
  (:require [clojure.string :as string]
            [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.members :as members]
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

(defn- find-app-owner-member
  [session subjects app]
  (when-let [subject (find-app-owner-subject subjects app)]
    (members/find-subject-member session subject true)))

(defn- grant-owner-permission
  "Grants ownership permission to an app."
  [session subjects de-users-role app-resource app]
  (when-let [member (find-app-owner-member session subjects app)]
    (perms/grant-role-membership-permission de-users-role member perms/own app-resource)))

(defn- register-app
  "Registers an app in Grouper."
  [session subjects de-users-role permission-def folder-name app]
  (let [app-resource (perms/create-permission-resource session permission-def folder-name (:id app))]
    (if (:is_public app)
      (perms/grant-role-permission de-users-role perms/read app-resource)
      (grant-owner-permission session subjects de-users-role app-resource app))))

(defn register-de-apps
  "Registers all DE apps in Grouper."
  [database session subjects folder-names permission-def-name]
  (let [apps-folder-name   (:de-apps folder-names)
        permission-def     (perms/find-permission-def apps-folder-name permission-def-name)
        de-users-role-name (format "%s:%s" (:de-users folder-names) consts/de-users-role-name)
        de-users-role      (roles/find-role session de-users-role-name)]
    (dorun (map (partial register-app session subjects de-users-role permission-def apps-folder-name)
                (db/list-de-apps database)))))
