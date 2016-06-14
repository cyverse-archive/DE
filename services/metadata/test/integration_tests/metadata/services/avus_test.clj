(ns integration-tests.metadata.services.avus-test
  (:use [clojure.test]
        [integration-tests]
        [kameleon.uuids :only [uuid]]
        [metadata.services.avus]))

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
  []
  (let [avu-list (list-avus test-target-type test-target-id)]
    (set (map #(select-keys % [:id :attr :value :unit]) (:avus avu-list)))))

(defn- is-avu-set?
  [expected-avu-set]
  (is (= (list-avus->set) expected-avu-set)))


;; Test AVUs setup, for the following AVU tests
(defn- with-test-data [f]
  (set-avus test-user-id test-target-type test-target-id test-metadata-avus)
  (f))

(use-fixtures :each with-test-data)


(deftest simple-set-get-avus-test
  (testing "Set AVUs"
    (is-avu-set? (set (:avus test-metadata-avus)))

    (set-avus test-user-id test-target-type test-target-id {:avus []})
    (is-avu-set? #{})

    (set-avus test-user-id test-target-type test-target-id test-metadata-avus)
    (is-avu-set? (set (:avus test-metadata-avus)))))


(deftest update-avus-test
    (let [test-avu {:id (uuid) :attr "x" :value "y" :unit "z"}
          modified-avu (assoc test-avu :value "w")]

      (testing "Add an AVU"
        (update-avus test-user-id test-target-type test-target-id {:avus [test-avu]})
        (is-avu-set? (set (conj (:avus test-metadata-avus) test-avu))))

      (testing "Update existing AVU without changes (noop)"
        (update-avus test-user-id test-target-type test-target-id {:avus [(dissoc test-avu :id)]})
        (is-avu-set? (set (conj (:avus test-metadata-avus) test-avu))))

      (testing "Exception from adding duplicate AVU"
        (is (thrown-with-msg? org.postgresql.util.PSQLException
                              #"duplicate key value violates unique constraint"
                              (update-avus test-user-id test-target-type test-target-id
                                           {:avus [(assoc test-avu :id (uuid))]}))))

      (testing "Update existing AVU with changes"
        (update-avus test-user-id test-target-type test-target-id {:avus [modified-avu]})
        (is-avu-set? (set (conj (:avus test-metadata-avus) modified-avu))))))


(deftest set-avus-test
  (testing "Set AVUs"
    (let [test-avu-a (assoc (select-keys test-avu-b [:attr :value :unit])
                            :id (:id test-avu-a))
          test-avu {:id (uuid) :attr "x" :value "y" :unit "z"}]
      (testing "- remove test-avu-b, set test-avu-a with test-avu-b's values"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu-a]})
        (is-avu-set? #{test-avu-a}))

      (testing "- remove existing, add one new"
        (set-avus test-user-id test-target-type test-target-id {:avus [test-avu]})
        (is-avu-set? #{test-avu})))))
