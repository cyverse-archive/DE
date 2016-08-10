(ns integration.terrain.services.permanent-id-requests
  (:use [clojure.test]
        [terrain.services.permanent-id-requests])
  (:require [terrain.clients.data-info :as data-info]
            [terrain.clients.data-info.raw :as data-info-client]
            [terrain.util.config :as config]))

(use-fixtures :once integration/run-integration-tests integration/with-test-data-item)

;; Re-def private functions so they can be tested in this namespace.
(def parse-valid-ezid-metadata #'terrain.services.permanent-id-requests/parse-valid-ezid-metadata)
(def ezid-target-attr #'terrain.services.permanent-id-requests/ezid-target-attr)

(defn- get-test-item-metadata
  []
  (data-info/get-metadata-json integration/test-user (:id integration/test-data-item)))

(defn- set-test-item-metadata
  [avus]
  (data-info-client/set-avus integration/test-user
                             (:id integration/test-data-item)
                             {:avus avus
                              :irods-avus []}))

(defn- add-test-item-metadata
  [avus]
  (data-info-client/add-avus integration/test-user
                             (:id integration/test-data-item)
                             {:avus avus
                              :irods-avus []}))

(defn- remove-test-item-metadata
  []
  (data-info-client/set-avus integration/test-user
                             (:id integration/test-data-item)
                             {:irods-avus []}))

(deftest test-parse-valid-ezid-metadata
  (testing "Test parse-valid-ezid-metadata return values and exceptions"
    (set-test-item-metadata [{:attr  "test-attr-1"
                              :value "test-value-1"
                              :unit  "test-unit-1"}])

    (let [ezid-metadata (parse-valid-ezid-metadata integration/test-data-item (get-test-item-metadata))]
      (is (contains? ezid-metadata @ezid-target-attr))
      (is (contains? ezid-metadata (config/permanent-id-date-attr))))

    (add-test-item-metadata [{:attr  (config/permanent-id-identifier-attr)
                              :value "test-ID"
                              :unit  "DOI"}])

    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"already contains a Permanent Identifier"
                          (parse-valid-ezid-metadata integration/test-data-item
                                                     (get-test-item-metadata))))

    (remove-test-item-metadata)

    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"No metadata found"
                          (parse-valid-ezid-metadata integration/test-data-item
                                                     (get-test-item-metadata))))))
