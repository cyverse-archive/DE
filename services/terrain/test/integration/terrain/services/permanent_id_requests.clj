(ns integration.terrain.services.permanent-id-requests
  (:use [clojure.test]
        [terrain.services.permanent-id-requests])
  (:require [terrain.clients.data-info :as data-info]
            [terrain.clients.data-info.raw :as data-info-client]))

(use-fixtures :once integration/run-integration-tests integration/with-test-data-item)

;; Re-def private functions so they can be tested in this namespace.
(def parse-valid-ezid-metadata #'terrain.services.permanent-id-requests/parse-valid-ezid-metadata)
(def ezid-target-attr #'terrain.services.permanent-id-requests/ezid-target-attr)


(deftest test-parse-valid-ezid-metadata
  (testing "parse-valid-ezid-metadata"
    (data-info-client/set-avus integration/test-user
                               (:id integration/test-data-item)
                               {:avus [{:attr  "test-attr-1"
                                        :value "test-value-1"
                                        :unit  ""}]
                                :irods-avus []})

    (let [data-id       (:id integration/test-data-item)
          metadata      (data-info/get-metadata-json integration/test-user data-id)
          ezid-metadata (parse-valid-ezid-metadata integration/test-data-item metadata)]
      (is (contains? ezid-metadata @ezid-target-attr)))

    (data-info-client/set-avus integration/test-user (:id integration/test-data-item) {:irods-avus []})))
