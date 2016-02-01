(ns facepalm.c240-2016012701
  (:use [korma.core]
        [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.4.0:20160127.01")

(defn- add-permanent-id-column
  []
  (println "\t* Adding the permanent_id column to the permanent_id_requests table...")
  (exec-sql-statement "ALTER TABLE permanent_id_requests ADD COLUMN permanent_id TEXT"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-permanent-id-column))
