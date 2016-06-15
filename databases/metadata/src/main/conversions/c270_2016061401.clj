(ns facepalm.c270-2016061401
  (:use [korma.core])
  (:require [clojure.java.jdbc :as jdbc]
            [facepalm.core :as migrator]
            [korma.db :as db]))

(def ^:private version
  "The destination database version."
  "2.7.0:20160614.01")

;; This statement can't be run inside a transaction. That's why jdbc is directly used.
(defn- add-target-enum-avu
  []
  (println "\t* Add 'avu' enum to target_enum type...")
  (jdbc/db-do-prepared (db/get-connection @migrator/admin-db-spec)
                       false
                       "ALTER TYPE target_enum ADD VALUE 'avu' AFTER 'app'"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-target-enum-avu))
