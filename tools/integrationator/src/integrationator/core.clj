(ns integrationator.core
  (:require [clj-ldap.client :as ldap]
            [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kameleon.pgpass :as pgpass]
            [korma.core :as sql]
            [korma.db :as db])
  (:gen-class))

(def cli-options
  [["-l" "--ldap-host HOST"   "LDAP host name or IP address, optionally with a colon and a port number"]
   ["-b" "--ldap-base BASE"   "LDAP search base"     :default "ou=People,dc=iplantcollaborative,dc=org"]
   ["-D" "--user-domain NAME" "user domain name"     :default "iplantcollaborative.org"]
   ["-h" "--db-host HOST"     "database host name"   :default "localhost"]
   ["-p" "--db-port PORT"     "database port number" :default 5432 :parse-fn #(Integer/parseInt %)]
   ["-d" "--db-name NAME"     "database name"        :default "de"]
   ["-U" "--db-user USER"     "database user"        :default "de"]
   ["-P" "--db-password PASS" "database password"]
   ["-?" "--help"             "display the help message"]])

(def required-options [:ldap-host])

(defn- usage [summary]
  (->> ["This program adds the user ID to the integration_data table when the user's email address"
        "matches the email address stored in LDAP. Direct access to both the DE apps database and"
        "the LDAP server is required to run this utility."
        ""
        "Usage: java -jar integrationator-standalone.jar [options]"
        ""
        "Options:"
        summary]
       (string/join \newline)))

(defn- error-msg [errors summary]
  (str (string/join \newline (concat ["Errors:"] (map (partial str "  ") errors)))
       \newline
       \newline
       (usage summary)))

(defn- exit [status msg]
  (println msg)
  (System/exit status))

(defn- missing-option-errors [options]
  (let [missing-opt (fn [k] (str "Missing required option: --" (name k)))
        opt-to-msg  (fn [k] (when (nil? (options k)) (missing-opt k)))]
    (remove nil? (map opt-to-msg required-options))))

(defn- prompt-for-password [user]
  (print user "password: ")
  (flush)
  (.. System console readPassword))

(defn- get-password
  "Attempts to obtain the database password from the user's .pgpass file.  If
   the password can't be obtained from .pgpass, prompts the user for the
   password"
  [host port database user]
  (let [password (pgpass/get-password host port database user)]
    (if (nil? password)
      (if (nil? (System/console))
        (exit 1 "No database password provided.")
        (prompt-for-password user))
      password)))

(defn- connect-to-db
  "Defines the database connection settings."
  [{:keys [db-host db-port db-name db-user db-password]}]
  (let [db-pass (or db-password (get-password db-host db-port db-name db-user))
        db-spec {:classname   "org.postgresql.Driver"
                 :subprotocol "postgresql"
                 :subname     (str "//" db-host ":" db-port "/" db-name)
                 :user        db-user
                 :password    db-pass}]
    (db/create-db db-spec)))

(def ^:private user-lookup
  (memoize (fn [conn search-base email]
             (first (ldap/search conn search-base {:filter (str "(mail=" email ")")})))))

(defn- user-lookup-fn [{:keys [ldap-host ldap-base]}]
  (let [conn (ldap/connect {:host ldap-host})]
    (partial user-lookup conn ldap-base)))

(defn- list-integration-data []
  (sql/select :integration_data
              (sql/where {:user_id nil})))

(defn- user-subselect [username]
  (sql/subselect :users
                 (sql/fields :id)
                 (sql/where {:username username})))

(defn- run-conversion [{:keys [user-domain] :as options}]
  (let [lookup-user (user-lookup-fn options)]
    (db/with-db(connect-to-db options)
      (doseq [i (list-integration-data)]
        (when-let [username (:uid (lookup-user (:integrator_email i)))]
          (println "Updating the integration_data table for" username)
          (sql/update :integration_data
                      (sql/set-fields {:user_id (user-subselect (str username "@" user-domain))})
                      (sql/where {:id (:id i)})))))))

(defn -main
  "Runs the utility"
  [& args]
  (let [{:keys [options errors summary]} (parse-opts args cli-options)
        errors                           (or errors (missing-option-errors options))]
    (cond (:help options) (exit 0 (usage summary))
          (seq errors)    (exit 1 (error-msg errors summary)))
    (run-conversion options)))
