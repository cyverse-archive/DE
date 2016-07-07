(ns sharkbait.db
  (:use [honeysql.helpers])
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [kameleon.pgpass :as pgpass]))

(defn- prompt-for-password
  "Prompts the user for a password."
  [user]
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
        (binding [*out* *err*]
          (println "No password supplied.")
          (System/exit 1))
        (prompt-for-password user))
      password)))

(defn build-spec
  [{:keys [host port database user]}]
  (let [password (get-password host port database user)]
    {:subprotocol "postgresql"
     :subname     (str "//" host ":" port "/" database)
     :user        user
     :password    (apply str password)}))

(defn- list-de-users-query
  "Formats an SQL query to list all users in the DE database."
  []
  (-> (select :id :username)
      (from :users)
      (where [:like :username "%@iplantcollaborative.org"])
      sql/format))

(defn list-de-users
  "Lists all of the users in the DE."
  [db-spec]
  (jdbc/query db-spec (list-de-users-query)))
