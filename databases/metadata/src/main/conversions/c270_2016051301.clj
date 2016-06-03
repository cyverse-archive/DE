(ns facepalm.c270-2016051301
  (:use [korma.core]
        [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.7.0:20160513.01")

(defn- remove-template-instances-table
  []

  (println "\t* Droping template_instances table...")
  (exec-sql-statement "DROP TABLE template_instances") )

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (remove-template-instances-table))
