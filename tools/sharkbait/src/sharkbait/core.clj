(ns sharkbait.core
  (:gen-class)
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [common-cli.core :as cli]
            [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.folders :as folders]
            [sharkbait.sessions :as sessions]
            [sharkbait.subjects :as subjects]
            [sharkbait.users :as users]))

(def tool-info
  {:desc "Utility for initializing Grouper for use with the DE."
   :app-name "sharkbait"
   :group-id "org.iplantc"
   :art-id "sharkbait"})

(def cli-options
  [["-?" "--help" "Show help." :default false]
   ["-h" "--host HOST" "The database hostname." :default "localhost"]
   ["-p" "--port PORT" "The database port number." :default 5432 :parse-fn #(Integer/parseInt %)]
   ["-d" "--database DATABASE" "The database name." :default "de"]
   ["-U" "--user USER" "The database username." :default "de"]
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

(defn- load-de-subjects
  "Loads all subjects with entries in the DE database."
  [db-spec session]
  (println "Loading DE subjects...")
  (->> (db/list-de-users db-spec)
       (mapv #(string/replace (:username %) #"@.*$" ""))
       (subjects/find-subjects)))

(defn- register-de-users
  "Adds all DE users to the de-users group."
  [session subjects]
  (println "Registering DE users...")
  (users/register-de-users session subjects))

(defn- register-de-entities
  "Registers DE entities in Grouper."
  [db-spec session subjects]
  (time (register-de-users session subjects)))

(defn- perform-de-user-actions
  "Performs the actions that do not require superuser privileges."
  [db-spec]
  (let [session (sessions/create-grouper-session consts/de-username)]
    (try
      (dorun (map (partial folders/find-folder session) default-folder-names))
      (register-de-entities db-spec session (time (load-de-subjects db-spec session)))
      (finally (sessions/stop-grouper-session session)))))

(defn -main
  [& args]
  (let [{:keys [options]} (cli/handle-args tool-info args (constantly cli-options))]
    (let [db-spec (db/build-spec options)]
      (with-open [db-conn (jdbc/get-connection db-spec)]
        (perform-root-actions)
        (perform-de-user-actions (jdbc/add-connection db-spec db-conn))))))
