(ns facepalm.c280-2016072501
  (:use [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.8.0:20160725.01")

(defn- add-version-table
  "Adds a version table to the database."
  []
  (println "\t* adding a version table to the database.")
  (load-sql-file "tables/version.sql"))

(defn convert
  "Performs the conversion for this database version."
  []
  (println "Performing the conversion for" version)
  (add-version-table))
