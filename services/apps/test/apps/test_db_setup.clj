(ns apps.test-db-setup
  (:use [korma.db]))

;; Intended for use with the test db docker images setup by test.sh
(defdb testdb (postgres {:db "de"
                         :user "de"
                         :password "notprod"
                         :host (System/getenv "POSTGRES_PORT_5432_TCP_ADDR")
                         :port (System/getenv "POSTGRES_PORT_5432_TCP_PORT")
                         :delimiters ""}))
