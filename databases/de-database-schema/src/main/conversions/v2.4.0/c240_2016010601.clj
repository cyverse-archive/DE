(ns facepalm.c240-2016010601
  (:use [korma.core]
        [kameleon.sql-reader :only [exec-sql-statement load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.4.0:20160106.01")

(defn- update-app-count-function
  "Updates the app_count function to allow the count to be restricted to a set of app IDs."
  []
  (println "\t* Updating the app_count function.")
  (load-sql-file "functions/02_app_count.sql"))

(defn- update-app-category-hierarchy-function
  "Updates the app_count function to allow the counts to be restricted to a set of app IDs."
  []
  (println "\t* Updating the app_category_hierarchy function.")
  (load-sql-file "functions/03_app_category_hierarchy.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (update-app-count-function)
  (update-app-category-hierarchy-function))
