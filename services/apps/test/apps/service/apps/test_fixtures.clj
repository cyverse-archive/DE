(ns apps.service.apps.test-fixtures
  (:use [apps.service.apps.test-utils :only [users get-user]]
        [kameleon.uuids :only [uuidify uuid]])
  (:require [apps.clients.iplant-groups :as ipg]
            [apps.service.apps :as apps]
            [apps.service.workspace :as workspace]
            [apps.tools :as tools]
            [apps.util.config :as config]
            [korma.core :as sql]
            [permissions-client.core :as pc]))

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
   :name  "Permissions Test App"
   :tools [{:attribution ""
            :description "Word Count"
            :id          (uuidify "85cf7a33-386b-46fe-87c7-8c9d59972624")
            :location    ""
            :name        "wc"
            :type        "executable"
            :version     "0.0.1"}]})

(def pipeline-definition
  {:mappings    [{:map         {(uuidify "13914010-89cd-406d-99c3-9c4ff8b023c3")
                                (uuidify "13914010-89cd-406d-99c3-9c4ff8b023c3")}
                  :source_step 0
                  :target_step 1}]
   :steps       [{:name        "DE Word Count"
                  :description "Counts the number of words in a file."
                  :app_type    "DE"
                  :task_id     (uuidify "1ac31629-231a-4090-b3b4-63ee078a0c37")}
                 {:name        "DE Word Count"
                  :description "Counts the number of words in a file."
                  :app_type    "DE"
                  :task_id     (uuidify "1ac31629-231a-4090-b3b4-63ee078a0c37")}]
   :name        "Word Count Inception"
   :description "Counts the number of words in a word count."})

(def tool-definition
  {:description "A test tool."
   :name        "test-tool"
   :type        "executable"
   :container   {:image {:name "dwr71/print-args"}}
   :location    ""})

(def ^:dynamic test-app-user nil)
(def ^:dynamic test-app nil)
(def ^:dynamic test-tool-user nil)
(def ^:dynamic test-tool-id nil)
(def ^:dynamic public-apps nil)
(def ^:dynamic beta-apps nil)

(defn create-test-app
  ([user]
   (create-test-app user (:name app-definition)))
  ([user name]
   (sql/delete :apps (sql/where {:name name}))
   (let [app (apps/add-app user (assoc app-definition :name name))]
     (apps/owner-add-app-docs user (:id app) {:documentation "This is a test."})
     app)))

(defn create-pipeline
  [user]
  (sql/delete :apps (sql/where {:name (:name pipeline-definition)}))
  (apps/add-pipeline user pipeline-definition))

(defn with-test-app [f]
  (binding [test-app-user (get-user :testde1)
            test-app      (create-test-app (get-user :testde1))]
    (f)
    (apps/permanently-delete-apps (get-user :testde1) {:app_ids [(:id test-app)] :root_deletion_request true})))

(defn with-workspaces [f]
  (dorun (map (comp workspace/get-workspace get-user) (keys users)))
  (f))

(defn register-public-apps []
  (for [app (sql/select :app_listing (sql/where {:is_public true :deleted false}))]
    (do (pc/grant-permission (config/permissions-client) "app" (:id app) "group" (ipg/grouper-user-group-id) "read")
        app)))

(defn category-name-subselect [category-name]
  (sql/subselect [:app_category_app :aca]
                 (sql/join [:app_categories :c] {:aca.app_category_id :c.id})
                 (sql/fields :aca.app_id)))

(defn load-beta-apps []
  (sql/select [:app_listing :a]
              (sql/where {:a.id        [in (category-name-subselect "Beta")]
                          :a.is_public true
                          :a.deleted   false})))

(defn with-public-apps [f]
  (binding [public-apps (into [] (register-public-apps))
            beta-apps   (into [] (load-beta-apps))]
    (f)))

(defn implementation-for-user [user]
  {:implementor       (str (:first-name user) " " (:last-name user))
   :implementor_email (:email user)
   :test              {:input_files  []
                       :output_files []}})

(defn create-tool
  ([implementor]
   (create-tool implementor (:name tool-definition)))
  ([implementor name]
   (sql/delete :tools (sql/where {:name name}))
   (tools/add-tools {:tools [(assoc tool-definition
                                    :name           name
                                    :implementation (implementation-for-user implementor))]})))

(defn delete-tool [tool-id]
  (sql/delete :tools (sql/where {:id tool-id})))

(defn with-test-tool [f]
  (binding [test-tool-user (get-user :testde10)
            test-tool-id   (first (:tool_ids (create-tool (get-user :testde10))))]
    (f)
    (delete-tool test-tool-id)))
