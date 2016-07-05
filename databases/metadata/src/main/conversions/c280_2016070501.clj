(ns facepalm.c280-2016070501
  (:use [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.8.0:20160705.01")

(defn- add-templates-description
  []
  (println "\t* Adding 'description' column to templates table...")
  (exec-sql-statement "ALTER TABLE templates ADD COLUMN description TEXT"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-templates-description))
