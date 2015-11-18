(ns sharkbait.apps
  (:require [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.permissions :as perms]))

(defn- create-public-apps-resource
  "Creates the resource that all public apps inherit their permissions from."
  [session permission-def folder-name]
  (perms/create-permission-resource session permission-def folder-name consts/public-apps-resource-name))

(defn- register-app
  [session permission-def folder-name app]
  (perms/create-permission-resource session permission-def folder-name (:id app)))

(defn register-de-apps
  "Registers all DE apps in Grouper."
  [database session folder-name permission-def-name]
  (let [permission-def       (perms/find-permission-def folder-name permission-def-name)
        public-apps-resource (create-public-apps-resource session permission-def folder-name)]
    (dorun (map (partial register-app session permission-def folder-name) (db/list-de-apps database)))))
