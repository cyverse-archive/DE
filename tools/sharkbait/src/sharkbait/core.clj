(ns sharkbait.core
  (:gen-class)
  (:require [common-cli.core :as cli]
            [sharkbait.consts :as consts]
            [sharkbait.folders :as folders]
            [sharkbait.roles :as roles]
            [sharkbait.sessions :as sessions]
            [sharkbait.subjects :as subjects]))

(def tool-info
  {:desc "Utility for initializing Grouper for use with the DE."
   :app-name "sharkbait"
   :group-id "org.iplantc"
   :art-id "sharkbait"})

(def cli-options
  [["-h" "--help" "Show help." :default false]
   ["-v" "--version" "Show the sharkbait version." :default false]])

(def ^:private default-folder-names
  [consts/de-users-folder])

(defn- perform-root-actions
  "Performs the actions that require superuser privileges."
  []
  (let [session (sessions/create-grouper-session)]
    (try
      (-> (folders/find-folder session consts/de-folder)
          (folders/grant-privs (subjects/find-subject consts/de-username true) #{:stem}))
      (finally (sessions/stop-grouper-session session)))))

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  []
  (let [session (sessions/create-grouper-session consts/de-username)]
    (try
      (dorun (map (partial folders/find-folder session) default-folder-names))
      (roles/create-role session consts/de-users-folder consts/de-users-role-name)
      (finally (sessions/stop-grouper-session session)))))

(defn -main
  [& args]
  (let [{:keys [options]} (cli/handle-args tool-info args (constantly cli-options))]
    (perform-root-actions)
    (perform-de-user-actions)))
