(ns metadata.util.ontology-test
  (:use [clojure.test]
        [metadata.util.ontology]))

(def hierarchy {:iri 1
                :subclasses [{:iri 2
                              :subclasses [{:iri 3
                                            :subclasses [{:iri 4
                                                          :subclasses [{:iri 5}]}]}]}
                             {:iri 3
                              :subclasses [{:iri 4
                                            :subclasses [{:iri 5}]}]}
                             {:iri 4
                              :subclasses [{:iri 5}]}
                             {:iri 6}]})

(deftest hierarchy->class-set-test
  (testing "Test hierarchy->class-set."
    (is (= (hierarchy->class-set hierarchy) #{{:iri 1}
                                              {:iri 2}
                                              {:iri 3}
                                              {:iri 4}
                                              {:iri 5}
                                              {:iri 6}}))))

(deftest hierarchy->class-subclass-pairs-test
  (testing "Test hierarchy->class-subclass-pairs."
    (is (= (hierarchy->class-subclass-pairs hierarchy) #{{:class_iri 1 :subclass_iri 2}
                                                         {:class_iri 1 :subclass_iri 3}
                                                         {:class_iri 1 :subclass_iri 4}
                                                         {:class_iri 1 :subclass_iri 6}
                                                         {:class_iri 2 :subclass_iri 3}
                                                         {:class_iri 3 :subclass_iri 4}
                                                         {:class_iri 4 :subclass_iri 5}}))))

(deftest filter-hierarchy-test
  (testing "Test filter-hierarchy where no nodes should be filtered."
    (is (= (filter-hierarchy #{5} hierarchy) hierarchy))))

(deftest filter-hierarchy-test
  (testing "Test filter-hierarchy where only leaf-nodes with :iri 3 and 6 should remain."
    (is (= (filter-hierarchy #{1 2 3 6} hierarchy) {:iri 1
                                                    :subclasses [{:iri 2
                                                                  :subclasses [{:iri 3}]}
                                                                 {:iri 3}
                                                                 {:iri 6}]}))))

(deftest filter-hierarchy-test
  (testing "Test filter-hierarchy where all nodes are filtered."
    (is (= (filter-hierarchy #{999} hierarchy) nil))))
