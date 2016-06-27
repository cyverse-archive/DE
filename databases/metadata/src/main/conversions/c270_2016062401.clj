(ns facepalm.c270-2016062401
  (:use [korma.core :exclude [update]]
        [kameleon.sql-reader :only [exec-sql-statement]]))

(def ^:private version
  "The destination database version."
  "2.7.0:20160624.01")

(defn- add-ontologies-deleted-flag
  []
  (println "\t* Add 'deleted' flag to ontologies table...")
  (exec-sql-statement "ALTER TABLE ontologies ADD COLUMN deleted BOOLEAN DEFAULT FALSE"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-ontologies-deleted-flag))
