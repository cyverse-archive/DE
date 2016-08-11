(ns apps.service.apps.groups-test
  (:use [clojure.test])
  (:require [apps.service.apps.test-fixtures :as atf]
            [apps.service.groups :as groups]
            [apps.test-fixtures :as tf]))

(use-fixtures :once tf/run-integration-tests tf/with-test-db tf/with-config atf/with-workspaces)
(use-fixtures :each atf/with-public-apps atf/with-test-app atf/with-test-tool)

(defn contains-member? [members member-id]
  (first (filter (comp (partial = member-id) :id) members)))

;; We should be able to list information about the workshop group.
(deftest test-workshop-group
  (let [group (groups/get-workshop-group)]
    (is (re-find #"workshop-users$" (:name group)))
    (is (= "role" (:type group)))))

;; We should be able to list the workshop group members.
(deftest test-workshop-group-member-listing
  (is (sequential? (:members (groups/get-workshop-group-members)))))

;; We should be able to update the list of workshop group members.
(deftest test-workshop-group-member-update
  (let [results (:results (groups/update-workshop-group-members ["testde1", "testde2", "testde3"]))]
    (is (= (count results) 3))
    (is (every? :success results)))
  (let [members          (:members (groups/get-workshop-group-members))
        contains-member? (fn [members member-id] (first (filter #(= (:id %) member-id) members)))]
    (is (= (count members) 3))
    (is (contains-member? members "testde1"))
    (is (contains-member? members "testde2"))
    (is (contains-member? members "testde3"))))
