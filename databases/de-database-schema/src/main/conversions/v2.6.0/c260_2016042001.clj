(ns facepalm.c260-2016042001
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.6.0:20160420.01")

(defn- add-app-hierarchy-version-table
  []
  (println "\t* Adding app_hierarchy_version table...")
  (load-sql-file "tables/34_app_hierarchy_version.sql")
  (load-sql-file "constraints/34_app_hierarchy_version.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-app-hierarchy-version-table))
