(ns integration-tests.metadata.services.avus-test
  (:use [clojure.test]
        [integration-tests]
        [kameleon.uuids :only [uuid]]
        [metadata.services.avus])
  (:require [metadata.persistence.avu :as persistence]))

(def test-user-id "test_user")
(def test-target-type "app")
(def test-target-id (uuid))
(def test-avu-a {:id (uuid) :attr "a" :value "b" :unit "c"})
(def test-avu-b {:id (uuid) :attr "d" :value "e" :unit "f"})
(def test-metadata-avus {:avus [test-avu-a test-avu-b]})

;; Remove test AVUs after all tests have run
(defn- remove-test-data [f]
  (f)
  (set-avus test-user-id test-target-type test-target-id {:avus []}))


;; Only run these tests in an integration test environment
(use-fixtures :once run-integration-tests remove-test-data)


;; Test helper functions
(defn- list-avus->set
  ([]
   (list-avus->set test-target-type test-target-id))
  ([target-type target-id]
   (let [avu-list (:avus (list-avus target-type target-id))]
     (set (map #(select-keys % [:id :attr :value :unit]) avu-list)))))

(defn- deleted-avus?
  [avus]
  (empty? (persistence/get-avus-by-ids (map :id avus))))


;; Test AVUs setup, for the following AVU tests
(defn- with-test-data [f]
  (set-avus test-user-id test-target-type test-target-id test-metadata-avus)
  (f))

(use-fixtures :each with-test-data)


(deftest simple-set-get-avus-test
  (testing "Set AVUs"
    (is (= (list-avus->set) (set (:avus test-metadata-avus))))

    (set-avus test-user-id test-target-type test-target-id {:avus []})
    (is (= (list-avus->set) #{}))
    (is (deleted-avus? [test-avu-a test-avu-b]))

    (set-avus test-user-id test-target-type test-target-id test-metadata-avus)
    (is (= (list-avus->set) (set (:avus test-metadata-avus))))))


(deftest update-avus-test
    (let [test-avu {:id (uuid) :attr "x" :value "y" :unit "z"}
          modified-avu (assoc test-avu :value "w")]

      (testing "Add an AVU"
        (update-avus test-user-id test-target-type test-target-id {:avus [test-avu]})
        (is (= (list-avus->set) (set (conj (:avus test-metadata-avus) test-avu)))))

      (testing "Update existing AVU without changes (noop)"
        (update-avus test-user-id test-target-type test-target-id {:avus [(dissoc test-avu :id)]})
        (is (= (list-avus->set) (set (conj (:avus test-metadata-avus) test-avu)))))

      (testing "Exception from adding duplicate AVU"
        (is (thrown-with-msg? org.postgresql.util.PSQLException
                              #"duplicate key value violates unique constraint"
                              (update-avus test-user-id test-target-type test-target-id
                                           {:avus [(assoc test-avu :id (uuid))]}))))

      (testing "Update existing AVU with changes"
        (update-avus test-user-id test-target-type test-target-id {:avus [modified-avu]})
        (is (= (list-avus->set) (set (conj (:avus test-metadata-avus) modified-avu)))))))


(deftest set-avus-test
  (testing "Set AVUs"
    (let [test-avu-a (assoc (select-keys test-avu-b [:attr :value :unit])
                            :id (:id test-avu-a))
          test-avu {:id (uuid) :attr "x" :value "y" :unit "z"}]
      (testing "- remove test-avu-b, set test-avu-a with test-avu-b's values"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu-a]})
        (is (= (list-avus->set) #{test-avu-a}))
        (is (deleted-avus? [test-avu-b])))

      (testing "- remove existing, add one new"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu]})
        (is (= (list-avus->set) #{test-avu}))
        (is (deleted-avus? [test-avu-a test-avu-b]))))))


(deftest avus-attached-to-avus-test
  (testing "Attach AVUs to AVUs"
    (let [test-avu-1 {:id (uuid) :attr "x" :value "a" :unit "z"}
          test-avu-2 {:id (uuid) :attr "x" :value "b" :unit "z"}
          test-avu-3 {:id (uuid) :attr "x" :value "c" :unit "z"}
          test-nested-avus (assoc test-avu-a
                             :avus [(assoc test-avu-1
                                      :avus [(assoc test-avu-2
                                               :avus [test-avu-3])])])
          modified-avu-1 (assoc (select-keys test-avu-2 [:attr :value :unit])
                           :id (:id test-avu-1))]

      (testing "- attach test-avu-1 to test-avu-a"
        (update-avus test-user-id test-target-type test-target-id {:avus [(assoc test-avu-a
                                                                            :avus [test-avu-1])]})
        (is (= (list-avus->set) (set (:avus test-metadata-avus))))
        (is (= (list-avus->set "avu" (:id test-avu-a)) #{test-avu-1})))

      (testing "- remove test-avu-a, add test-avu-b"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu-b]})
        (is (= (list-avus->set) #{test-avu-b}))
        (is (empty? (persistence/get-avus-for-target "avu" (:id test-avu-a))))
        (is (deleted-avus? [test-avu-a test-avu-1])))

      (testing "- attach nested test-avu-1, test-avu-2, and test-avu-3 to test-avu-a"
        (update-avus test-user-id test-target-type test-target-id {:avus [test-nested-avus]})
        (is (= (list-avus->set) (set (:avus test-metadata-avus))))
        (is (= (list-avus->set "avu" (:id test-avu-a)) #{test-avu-1}))
        (is (= (list-avus->set "avu" (:id test-avu-1)) #{test-avu-2}))
        (is (= (list-avus->set "avu" (:id test-avu-2)) #{test-avu-3})))

      (testing "- remove test-avu-a, test-avu-1, test-avu-2, and test-avu-3 - add test-avu-b"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu-b]})
        (is (= (list-avus->set) #{test-avu-b}))
        (is (empty? (persistence/get-avus-for-target "avu" (:id test-avu-a))))
        (is (empty? (persistence/get-avus-for-target "avu" (:id test-avu-1))))
        (is (empty? (persistence/get-avus-for-target "avu" (:id test-avu-2))))
        (is (deleted-avus? [test-avu-a
                            test-avu-1
                            test-avu-2
                            test-avu-3])))

      (testing "- attach test-avu-1, test-avu-2, and test-avu-3 to test-avu-a - not nested"
        (update-avus test-user-id test-target-type test-target-id {:avus [(assoc test-avu-a
                                                                            :avus [test-avu-1
                                                                                   test-avu-2
                                                                                   test-avu-3])]})
        (is (= (list-avus->set) (set (:avus test-metadata-avus))))
        (is (= (list-avus->set "avu" (:id test-avu-a))
               #{test-avu-1 test-avu-2 test-avu-3})))

      (testing "- remove test-avu-2, set test-avu-1 with test-avu-2's values"
        (set-avus test-user-id test-target-type test-target-id {:avus [(assoc test-avu-a
                                                                         :avus [modified-avu-1
                                                                                test-avu-3])]})
        (is (= (list-avus->set) #{test-avu-a}))
        (is (= (list-avus->set "avu" (:id test-avu-a))
               #{modified-avu-1 test-avu-3}))
        (is (deleted-avus? [test-avu-2]))))))
