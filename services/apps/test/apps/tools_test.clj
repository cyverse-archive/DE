(ns apps.tools-test
  (:use [clojure.test]
        [apps.tools]
        [korma.db]
        [korma.core]
        [kameleon.entities])
  (:require [korma.core :as sql]))

(defdb testdb
  (postgres {:db "de"
             :user "de"
             :password "notprod"
             :host (System/getenv "POSTGRES_PORT_5432_TCP_ADDR")
             :port (System/getenv "POSTGRES_PORT_5432_TCP_PORT")
             :delimiters ""}))

(deftest test-get-tool
  (let [tool-map (first (select tools (where {:name "notreal"})))]
    (testing "(get-tool) returns the right tool"
      (let [tool-id        (:id tool-map)
            retrieved-tool (get-tool tool-id)]
        (is (= tool-id (:id retrieved-tool)))
        (is (contains? retrieved-tool :restricted))
        (is (contains? retrieved-tool :time_limit_seconds))
        (is (= (:restricted retrieved-tool) false))
        (is (= (:time_limit_seconds retrieved-tool) 0))))))

(deftest test-update-tool
  (let [tool-map (first (select tools (where {:name "notreal"})))]
    (testing "(update-tool) actually updates the tool"
      (let [tool-id      (:id tool-map)
            updated-tool (update-tool false (-> tool-map
                                               (assoc :restricted true)
                                               (assoc :time_limit_seconds 10)))]
        (is (= tool-id (:id updated-tool)))
        (is (contains? updated-tool :restricted))
        (is (contains? updated-tool :time_limit_seconds))
        (is (:restricted updated-tool))
        (is (= (:time_limit_seconds updated-tool) 10))
        (update-tool false tool-map)))))
