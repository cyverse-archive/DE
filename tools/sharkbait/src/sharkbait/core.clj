(ns sharkbait.core
  (:gen-class)
  (:require [sharkbait.folders :as folders]
            [sharkbait.permissions :as permissions]
            [sharkbait.sessions :as sessions]
            [sharkbait.subjects :as subjects]))

(def ^:private de-username        "de_grouper")
(def ^:private de-folder          "iplant:de")
(def ^:private de-users-folder    "iplant:de:users")
(def ^:private de-apps-folder     "iplant:de:apps")
(def ^:private de-analyses-folder "iplant:de:analyses")

(def ^:private default-folder-names
  [de-users-folder
   de-apps-folder
   de-analyses-folder])

(defn- perform-root-actions
  "Performs the actions that require superuser privileges."
  []
  (let [session (sessions/create-grouper-session)]
    (try
      (-> (folders/find-folder session de-folder)
          (folders/grant-privs (subjects/find-subject de-username) #{:stem}))
      (finally (sessions/stop-grouper-session session)))))

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  []
  (let [session (sessions/create-grouper-session de-username)]
    (try
      (dorun (map (partial folders/find-folder session) default-folder-names))
      (permissions/create-permission-def session "appPermissionDef" de-apps-folder)
      (permissions/create-permission-def session "analysisPermissionDef" de-analyses-folder)
      (finally (sessions/stop-grouper-session session)))))

(defn -main
  [& args]
  (perform-root-actions)
  (perform-de-user-actions))
