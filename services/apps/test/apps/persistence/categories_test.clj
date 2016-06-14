(ns apps.persistence.categories-test
  (:use [clojure.test]
        [apps.persistence.categories]
        [apps.test-fixtures :only [run-integration-tests with-test-db]]))

;;; TODO: Modify these tests so that we can run them multiple times on the same database without them failing.

(use-fixtures :once run-integration-tests with-test-db)

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
