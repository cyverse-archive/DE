(ns apps.persistence.categories-test
  (:use [clojure.test]
        [apps.persistence.categories]
        [apps.test-fixtures :only [run-integration-tests with-test-db]]
        [korma.core]))

(defn clean-up-hierarchy-versions [f]
  (f)
  (delete :app_hierarchy_version))

(use-fixtures :each with-test-db run-integration-tests clean-up-hierarchy-versions)

(deftest hierarchy-version-test
  (testing "Test setting and fetching app category hierarchy versions."
    (is (nil? (get-active-hierarchy-version)))

    (add-hierarchy-version "test-user" "v1")
    (is (= "v1" (get-active-hierarchy-version)))

    (add-hierarchy-version "test-user" "v2")
    (is (= "v2" (get-active-hierarchy-version)))

    (add-hierarchy-version "test-user" "v1")
    (is (= "v1" (get-active-hierarchy-version)))

    (add-hierarchy-version "test-user" "foo")
    (is (= "foo" (get-active-hierarchy-version)))))
