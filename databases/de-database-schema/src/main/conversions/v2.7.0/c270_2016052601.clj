(ns facepalm.c270-2016052601
  (:use [korma.core]
         [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.7.0:20160526.01")

(defn- add-restricted-column
  []
  (println "\t* Adding restricted column to the tools table.")
  (exec-sql-statement "ALTER TABLE tools ADD restricted BOOLEAN NOT NULL DEFAULT FALSE"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-restricted-column))
