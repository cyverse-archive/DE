(ns facepalm.c270-2016052501
  (:use [korma.core]
         [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.7.0:20160525.01")

(defn- add-time-limit-column
  []
  (println "\t* Adding time_limit_seconds column to the tools table.")
  (exec-sql-statement "ALTER TABLE tools ADD time_limit_seconds INTEGER DEFAULT 0"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-time-limit-column))
