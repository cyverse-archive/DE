(ns apps-beta-tagger.db
  (:use [korma.db])
  (:require [kameleon.pgpass :as pgpass]))

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

(defn create-db-spec
  "Creates the database connection spec to use when accessing the database using Korma."
  [host port database user]
  (let [password (get-password host port database user)]
    (postgres
      {:host     host
       :port     port
       :db       database
       :user     user
       :password (apply str password)})))
