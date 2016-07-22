(ns facepalm.c270-2016061401
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version"
  "2.7.0:20160614.01")

(defn- load-python-app
  []
  (println "\t* Adding Python 2.7 tool and app to the database")
  (load-sql-file "data/22_python_app.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (load-python-app))
