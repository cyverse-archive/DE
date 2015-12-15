(ns facepalm.c240-2015121001
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.4.0:20151210.01")

(defn- add-ezid-request-tables
  []
  (println "\t* Adding first and last functions...")
  (load-sql-file "functions/02_first.sql")
  (load-sql-file "functions/03_last.sql")

  (println "\t* Adding permanent_id_request_status_codes table...")
  (load-sql-file "tables/permanent_id_request_status_codes.sql")

  (println "\t* Adding permanent_id_request_types table...")
  (load-sql-file "tables/permanent_id_request_types.sql")

  (println "\t* Adding permanent_id_requests table...")
  (load-sql-file "tables/permanent_id_requests.sql")

  (println "\t* Adding permanent_id_request_statuses table...")
  (load-sql-file "tables/permanent_id_request_statuses.sql")

  (println "\t* Adding permanent_id_request_status_codes constraints...")
  (load-sql-file "constraints/11_permanent_id_request_status_codes.sql")

  (println "\t* Adding permanent_id_request_types constraints...")
  (load-sql-file "constraints/12_permanent_id_request_types.sql")

  (println "\t* Adding permanent_id_requests constraints...")
  (load-sql-file "constraints/13_permanent_id_requests.sql")

  (println "\t* Adding permanent_id_request_statuses constraints...")
  (load-sql-file "constraints/14_permanent_id_request_statuses.sql")

  (println "\t* Adding permanent_id_request_status_codes data...")
  (load-sql-file "data/01_permanent_id_request_status_codes.sql")

  (println "\t* Adding permanent_id_request_types data...")
  (load-sql-file "data/02_permanent_id_request_types.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-ezid-request-tables))
