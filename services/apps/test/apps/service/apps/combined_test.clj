(ns apps.service.apps.combined-test
  (:use [clojure.test]
        [apps.service.apps.combined]))

(deftype TestClient1 []
  apps.protocols.Apps

  (listAppsWithMetadata [_ attr value params]
    {:app_count 5, :apps [{:id 1} {:id 2} {:id 3} {:id 4} {:id 5}]}))

(deftype TestClient2 []
  apps.protocols.Apps

  (listAppsWithMetadata [_ attr value params]
    {:app_count 3, :apps [{:id "a"} {:id "b"} {:id "c"}]}))

(def combined-client (apps.service.apps.combined.CombinedApps.
                       [(TestClient1.) (TestClient2.)]
                       "someuser"))

(deftest CombinedApps-listAppsWithMetadata-test
  (let [expected-app-set #{{:id 1} {:id 2} {:id 3} {:id 4} {:id 5} {:id "a"} {:id "b"} {:id "c"}}
        {:keys [app_count apps]} (.listAppsWithMetadata combined-client
                                                        "attr"
                                                        "value"
                                                        {:sort-field "id"})]
    (testing "Test CombinedApps.listAppsWithMetadata app count and apps list."
      (is (= app_count 8))
      (is (= (set apps) expected-app-set)))))
