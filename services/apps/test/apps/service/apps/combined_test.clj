(ns apps.service.apps.combined-test
  (:use [clojure.test]
        [apps.service.apps.combined]))

(def client1-apps-listing {:app_count 5, :apps [{:id 1} {:id 2} {:id 3} {:id 4} {:id 5}]})
(def client2-apps-listing {:app_count 3, :apps [{:id "a"} {:id "b"} {:id "c"}]})
(def combined-app-set #{{:id 1} {:id 2} {:id 3} {:id 4} {:id 5} {:id "a"} {:id "b"} {:id "c"}})

(deftype TestClient1 []
  apps.protocols.Apps

  (listAppsUnderHierarchy
    [_ root-iri attr params]
    client1-apps-listing)

  (adminListAppsUnderHierarchy
    [_ ontology-version root-iri attr params]
    client1-apps-listing))

(deftype TestClient2 []
  apps.protocols.Apps

  (listAppsUnderHierarchy
    [_ root-iri attr params]
    client2-apps-listing)

  (adminListAppsUnderHierarchy
    [_ ontology-version root-iri attr params]
    client2-apps-listing))

(def combined-client (apps.service.apps.combined.CombinedApps.
                       [(TestClient1.) (TestClient2.)]
                       "someuser"))

(deftest CombinedApps-listAppsUnderHierarchy-test
  (let [{:keys [app_count apps]} (.listAppsUnderHierarchy combined-client
                                                          "root-iri"
                                                          "attr"
                                                          {:sort-field "id"})]
    (testing "Test CombinedApps.listAppsWithMetadata app count and apps list."
      (is (= app_count 8))
      (is (= (set apps) combined-app-set)))))

(deftest CombinedApps-adminListAppsUnderHierarchy-test
  (let [{:keys [app_count apps]} (.adminListAppsUnderHierarchy combined-client
                                                               "ontology-version"
                                                               "root-iri"
                                                               "attr"
                                                               {:sort-field "id"})]
    (testing "Test CombinedApps.adminListAppsUnderHierarchy app count and apps list."
      (is (= app_count 8))
      (is (= (set apps) combined-app-set)))))
