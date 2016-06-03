(ns metadata.services.ontology-test
  (:use [clojure.test]
        [metadata.services.ontology]))

;; Re-def private functions so they can be tested in this namespace.
(def add-subclasses #'metadata.services.ontology/add-subclasses)

(deftest add-subclasses-test
  (let [groups [{:iri 1}
                {:parent_iri 1 :iri 2}
                {:parent_iri 2 :iri 3}
                {:parent_iri 1 :iri 4}]
        root   (first (filter #(= 1 (:iri %)) groups))]
    (testing "Test add-subclasses."
      (is (= (add-subclasses root groups)
             {:iri 1
              :subclasses [{:iri 2
                            :subclasses [{:iri 3}]}
                           {:iri 4}]})))))

(deftest add-subclasses-nil-test
  (testing "Test add-subclasses with nil root."
    (is (= nil (add-subclasses nil [])))))

(deftest add-subclasses-malformed-test
  (testing "Test add-subclasses with a root and groups having malformed keys."
    (is (= {:id 1} (add-subclasses {:id 1} [{:id 1}
                                            {:parent_id 1 :id 2}
                                            {:parent_id 2 :id 3}
                                            {:parent_id 1 :id 4}])))))
