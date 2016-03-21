(ns monkey.core-test
  (:use clojure.test
        monkey.core))

(deftest silly-test
  (testing "This test is totally fake, but it'll pass!"
    (is (= 1 1))))
