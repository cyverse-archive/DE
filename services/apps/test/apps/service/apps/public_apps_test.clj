(ns apps.service.apps.public-apps-test
  (:use [apps.service.apps.test-utils :only [get-user]]
        [apps.service.apps.de.listings :only [my-public-apps-id trash-category-id]]
        [clojure.test])
  (:require [apps.service.apps :as apps]
            [apps.service.apps.test-fixtures :as atf]
            [apps.test-fixtures :as tf]
            [apps.util.config :as config]
            [apps.validation :as v]
            [clojure.tools.logging :as log]
            [korma.core :as sql]
            [permissions-client.core :as pc])
  (:import [clojure.lang ExceptionInfo]))

(use-fixtures :once tf/run-integration-tests tf/with-test-db tf/with-config atf/with-workspaces)
(use-fixtures :each atf/with-public-apps atf/with-test-app atf/with-test-tool)

;; FIXME the Beta category is obsolete
(deftest test-marked-as-public
  (let [user        (get-user :testde1)
        app         (atf/create-test-app user "To be published")
        _           (sql/delete :app_documentation (sql/where {:app_id (:id app)}))
        _           (apps/make-app-public user app)
        ;FIXME listed      (first (:apps (apps/search-apps user {:search (:name app)})))
        beta-id     (:id (atf/get-beta-category user))
        apps        (:apps (apps/list-apps-in-category user beta-id {}))
        public-apps (:apps (apps/list-apps-in-category user my-public-apps-id {}))
        listed      (first (filter (comp (partial = (:id app)) :id) public-apps))]
    (is (not (nil? listed)))
    (is (:is_public listed))
    (is (empty? (filter (comp (partial = (:id app)) :id) apps)))
    (is (seq (filter (comp (partial = (:id app)) :id) public-apps)))
    (apps/permanently-delete-apps user {:app_ids [(:id app)] :root_deletion_request true})))

(deftest test-publishable
  (let [user (get-user :testde1)
        app  (atf/create-test-app user "To be published")]
    (sql/delete :app_documentation (sql/where {:app_id (:id app)}))
    (apps/make-app-public user app)
    (pc/grant-permission (config/permissions-client) "app" (:id app) "user" (:shortUsername user) "own")
    (let [publishable? (:publishable (apps/app-publishable? user (:id app)))]
      (is (not (nil? publishable?)))
      (is (not publishable?)))
    (apps/permanently-delete-apps user {:app_ids [(:id app)]})))

(deftest validate-tool-not-public
  (let [user (get-user :testde1)
        app  (atf/create-test-tool-app user "To be published")]
    (is (nil? (v/validate-tool-not-public atf/test-tool-id)))
    (sql/delete :app_documentation (sql/where {:app_id (:id app)}))
    (apps/make-app-public user app)
    (is (thrown-with-msg? ExceptionInfo #"in use by public apps" (v/validate-tool-not-public atf/test-tool-id)))
    (apps/permanently-delete-apps user {:app_ids [(:id app)] :root_deletion_request true})))

(deftest validate-app-trash
  (let [user  (get-user :testde1)
        app   (atf/create-test-app user "To be published")
        _     (sql/delete :app_documentation (sql/where {:app_id (:id app)}))
        _     (apps/make-app-public user app)
        _     (apps/admin-delete-app user (:id app))
        trash (:apps (apps/list-apps-in-category user trash-category-id {}))]
    (is (seq (filter (comp (partial = (:id app)) :id) trash)))
    (apps/permanently-delete-apps user {:app_ids [(:id app)] :root_deletion_request true})))
