(ns apps.service.apps.permissions-test
  (:use [apps.user :only [user-from-attributes]]
        [clojure.test])
  (:require [apps.service.apps :as apps]
            [apps.service.workspace :as workspace]
            [apps.test-fixtures :as tf]))

(defn- create-user [i]
  (let [username (str "testde" i)]
    {:user       username
     :first-name username
     :last-name  username
     :email      (str username "@mail.org")}))

(defn- create-user-map []
  (->> (take 10 (iterate inc 1))
       (mapv (comp (juxt (comp keyword :user) identity) create-user))
       (into {})))

(def users (create-user-map))

(defn- get-user [k]
  (user-from-attributes (users k)))

(def app-definition
  {:description "Testing"
   :groups      [{:label      "Parameters"
                  :name       "Parameters"
                  :parameters [{:description     "Select an input file."
                                :file_parameters {:data_source        "file"
                                                  :file_info_type     "File"
                                                  :format             "Unspecified"}
                                :isVisible       true
                                :label           "Input file"
                                :name            ""
                                :omit_if_blank   false
                                :order           0
                                :required        true
                                :type            "FileInput"
                                :validators      []}
                               {:defaultValue    "out.txt"
                                :description     ""
                                :file_parameters {:data_source    "stdout"
                                                  :file_info_type "File"
                                                  :format         "Unspecified"
                                                  :retain         true}
                                :isVisible       true
                                :label           "Output file name"
                                :name            ""
                                :omit_if_blank   false
                                :order           1
                                :required        false
                                :type            "FileOutput"
                                :valicators      []
                                :value           "out.txt"}]}]
   :name  "Test App"
   :tools [{:attribution ""
            :description "Word Count"
            :id          "85cf7a33-386b-46fe-87c7-8c9d59972624"
            :location    ""
            :name        "wc"
            :type        "executable"
            :version     "0.0.1"}]})

(def ^:dynamic test-app nil)

(defn- with-test-app [f]
  (binding [test-app (apps/add-app (get-user :testde1) app-definition)]
    (f)
    (apps/permanently-delete-apps (get-user :testde1) {:app-ids [(:id test-app)]})))

(defn with-workspaces [f]
  (dorun (comp workspace/get-workspace get-user) (keys users))
  (f))

(use-fixtures :once tf/with-test-db tf/with-config tf/run-integration-tests with-workspaces)
(use-fixtures :each with-test-app)

(deftest test-app-search
  (is (= 1 (:app_count (apps/search-apps (get-user :testde1) {:search "Test App"})))))
