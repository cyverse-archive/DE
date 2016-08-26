(ns apps.metadata.reference-genomes-test
  (:use [apps.test-fixtures]
        [apps.metadata.reference-genomes]
        [clojure.test]
        [korma.core :exclude [update]]
        [korma.db :only [rollback transaction]])
  (:import [clojure.lang ExceptionInfo]))

(use-fixtures :once run-integration-tests with-test-db with-config with-test-user)

(def ^:dynamic test-reference-genome-1 nil)
(def ^:dynamic test-reference-genome-2 nil)

(defn- with-test-reference-genome
  [f]
  (transaction
    (binding [test-reference-genome-1 (add-reference-genome {:name "foo reference" :path "/path/to/foo"})
              test-reference-genome-2 (add-reference-genome {:name "bar reference" :path "/path/to/bar"})]
      (f))
    (rollback)))

(use-fixtures :each with-test-reference-genome)

(deftest test-add-reference-genome
  (testing "Adding a reference genome with name and path validations"
    (let [new-ref-genome-name "foo-unique name"
          new-ref-genome-path "/foo/unique/path"]
      (is (thrown-with-msg? ExceptionInfo #"reference genome with the given name already exists"
                            (add-reference-genome (merge test-reference-genome-1
                                                         {:path new-ref-genome-path}))))
      (is (thrown-with-msg? ExceptionInfo #"reference genome with the given path already exists"
                            (add-reference-genome (merge test-reference-genome-1
                                                         {:name new-ref-genome-name})))))

    (let [new-ref-genome-name "foo-unique name"
          new-ref-genome-path "/foo/unique/path"
          new-ref-genome (add-reference-genome {:name new-ref-genome-name :path new-ref-genome-path})]
      (is (= new-ref-genome-name (:name new-ref-genome)))
      (is (= new-ref-genome-path (:path new-ref-genome)))
      (is (not (empty? (get-reference-genome (:id new-ref-genome)))))
      (is (not (empty? (filter #(= (:id new-ref-genome) (:id %))
                               (:genomes (list-reference-genomes)))))))))

(deftest test-update-reference-genome
  (testing "Updating a reference genome with name and path validations"
    (is (thrown-with-msg? ExceptionInfo #"reference genome with the given name already exists"
                          (update-reference-genome (merge test-reference-genome-1
                                                          (select-keys test-reference-genome-2 [:name])))))
    (is (thrown-with-msg? ExceptionInfo #"reference genome with the given path already exists"
                          (update-reference-genome (merge test-reference-genome-1
                                                          (select-keys test-reference-genome-2 [:path])))))

    (let [new-ref-genome-name "foo-unique name"
          new-ref-genome-path "/foo/unique/path"]
      (update-reference-genome (merge test-reference-genome-1
                                      {:name new-ref-genome-name
                                       :path new-ref-genome-path}))
      (is (= new-ref-genome-name (:name (get-reference-genome (:id test-reference-genome-1)))))
      (is (= new-ref-genome-path (:path (get-reference-genome (:id test-reference-genome-1))))))))
