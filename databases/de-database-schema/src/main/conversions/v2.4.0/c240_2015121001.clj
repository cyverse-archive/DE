(ns facepalm.c240-2015121001
  (:use [korma.core]
        [kameleon.sql-reader :only [load-sql-file]]))

(def ^:private version
  "The destination database version."
  "2.4.0:20151210.01")

(defn- add-ezid-request-tables
  []
  (println "\t* Adding permanent_id_request_status_codes table...")
  (load-sql-file "tables/76_permanent_id_request_status_codes.sql")

  (println "\t* Adding permanent_id_request_types table...")
  (load-sql-file "tables/77_permanent_id_request_types.sql")

  (println "\t* Adding permanent_id_requests table...")
  (load-sql-file "tables/78_permanent_id_requests.sql")

  (println "\t* Adding permanent_id_request_statuses table...")
  (load-sql-file "tables/79_permanent_id_request_statuses.sql")

  (println "\t* Adding permanent_id_request_status_codes constraints...")
  (load-sql-file "constraints/76_permanent_id_request_status_codes.sql")
  (load-sql-file "constraints/00_76_permanent_id_request_status_codes_pkey.sql")

  (println "\t* Adding permanent_id_request_types constraints...")
  (load-sql-file "constraints/00_77_permanent_id_request_types_pkey.sql")
  (load-sql-file "constraints/77_permanent_id_request_types.sql")

  (println "\t* Adding permanent_id_requests constraints...")
  (load-sql-file "constraints/00_78_permanent_id_requests_pkey.sql")
  (load-sql-file "constraints/78_permanent_id_requests.sql")

  (println "\t* Adding permanent_id_request_statuses constraints...")
  (load-sql-file "constraints/00_79_permanent_id_request_statuses_pkey.sql")
  (load-sql-file "constraints/79_permanent_id_request_statuses.sql")

  (println "\t* Adding permanent_id_request_status_codes data...")
  (load-sql-file "data/19_permanent_id_request_status_codes.sql")

  (println "\t* Adding permanent_id_request_types data...")
  (load-sql-file "data/20_permanent_id_request_types.sql"))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-ezid-request-tables))
