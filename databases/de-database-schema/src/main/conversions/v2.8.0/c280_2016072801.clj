(ns facepalm.c280-2016072801
  (:use [kameleon.sql-reader :only [exec-sql-statement]])
  (:require [korma.core :as sql]))

(def ^:private version
  "The destination database version"
  "2.8.0:20160728.01")

(defn- add-job-status-update-indices
  "Adds a user_id column to the integration_data table."
  []
  (println "\t* adding indices to the job_status_updates table for common queries")
  (->> ["CREATE INDEX job_status_updates_propagated ON job_status_updates (propagated, propagation_attempts)",
        "CREATE INDEX job_status_updates_external_id ON job_status_updates (external_id)"]
       (map exec-sql-statement)
       (dorun)))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-job-status-update-indices))
