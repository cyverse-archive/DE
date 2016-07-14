(ns apps.service.apps.integration-data-test
  (:use [apps.service.apps.test-utils :only [users get-user]]
        [clojure.test])
  (:require [apps.persistence.app-metadata :as amp]
            [apps.service.apps :as apps]
            [apps.service.apps.test-fixtures :as atf]
            [apps.test-fixtures :as tf]
            [clojure.tools.logging :as log]
            [korma.core :as sql]))

(use-fixtures :once tf/run-integration-tests tf/with-test-db tf/with-config atf/with-workspaces)
(use-fixtures :each atf/with-public-apps atf/with-test-app atf/with-test-tool)

(defn- get-integration-data [{:keys [username]}]
  (first (sql/select :integration_data
                     (sql/where {:user_id (sql/subselect :users
                                                         (sql/fields :id)
                                                         (sql/where {:username username}))}))))

(defn- get-integration-data-for-tool [tool-id]
  (first (sql/select :integration_data
                     (sql/where {:id (sql/subselect :tools
                                                    (sql/fields :integration_data_id)
                                                    (sql/where {:id tool-id}))}))))

(defn- get-integration-data-for-app [app-id]
  (first (sql/select :integration_data
                     (sql/where {:id (sql/subselect :apps
                                                    (sql/fields :integration_data_id)
                                                    (sql/where {:id app-id}))}))))

(defn- user-to-integration-data-name [{:keys [first-name last-name]}]
  (str first-name " " last-name))

;; The user ID should be associated with the integration data after the user creates an
;; app.
(deftest test-app-integration-data
  (let [user             atf/test-app-user
        integration-data (get-integration-data user)]
    (is (not (nil? integration-data)))
    (is (not (nil? (:user_id integration-data))))
    (is (= (:integrator_email integration-data) (:email user)))
    (is (= (:integrator_name integration-data) (user-to-integration-data-name user)))))

;; In this case, we expect the user ID associated with the integration data to be nil
;; because the user didn't have integration data before and the tools endpoint has no
;; way to get the username of the integrator.
(deftest test-tool-integration-data
  (let [user             atf/test-tool-user
        integration-data (get-integration-data-for-tool atf/test-tool-id)]
    (is (not (nil? integration-data)))
    (is (nil? (:user_id integration-data)))
    (is (= (:integrator_email integration-data) (:email user)))
    (is (= (:integrator_name integration-data) (user-to-integration-data-name user)))))

;; In this case, the user ID should be associated with the integration data because an
;; app has already been created by the user.
(deftest test-tool-integration-data-after-app-integration
  (let [tool-id          (first (:tool_ids (atf/create-tool atf/test-app-user "YATT")))
        integration-data (get-integration-data-for-tool tool-id)]
    (atf/delete-tool tool-id)
    (is (not (nil? integration-data)))
    (is (not (nil? (:user_id integration-data))))
    (is (= (:integrator_email integration-data) (:email atf/test-app-user)))
    (is (= (:integrator_name integration-data) (user-to-integration-data-name atf/test-app-user)))))

;; In the case of an app being copied, the integration data should have a user ID associated with it.
(deftest test-tool-integration-data-after-app-copy
  (let [user             (get-user :testde9)
        app-id           (:id (apps/copy-app user (:id (first atf/public-apps))))
        integration-data (get-integration-data-for-app app-id)]
    (is (not (nil? integration-data)))
    (is (not (nil? (:user_id integration-data))))
    (is (= (:integrator_email integration-data) (:email user)))
    (is (= (:integrator_name integration-data) (user-to-integration-data-name user)))
    (apps/permanently-delete-apps user {:app_ids [app-id]})))

;; Integration data should automatically be updated if the email address changes.
(deftest test-integration-data-email-address-change
  (let [user (get-user :testde1)
        id1  (amp/get-integration-data (assoc user :email "foo@example.org"))
        id2  (amp/get-integration-data user)]
    (is (not (nil? id1)))
    (is (not (nil? (:user_id id1))))
    (is (= (:integrator_email id1) "foo@example.org"))
    (is (= (:integrator_name id1) (user-to-integration-data-name user)))
    (is (not (nil? id2)))
    (is (not (nil? (:user_id id2))))
    (is (= (:integrator_email id2) (:email user)))
    (is (= (:integrator_name id2) (user-to-integration-data-name user)))
    (is (= (:user_id id1) (:user_id id2)))))

;; Integration data should automatically be updated if the name changes.
(deftest test-integration-data-name-change
  (let [real (get-user :testde1)
        fake (assoc real :first-name "foo" :last-name "bar")
        idf  (amp/get-integration-data fake)
        idr  (amp/get-integration-data real)]
    (is (not (nil? idf)))
    (is (not (nil? (:user_id idf))))
    (is (= (:integrator_email idf) (:email real)))
    (is (= (:integrator_name idf) (user-to-integration-data-name fake)))
    (is (not (nil? idr)))
    (is (not (nil? (:user_id idr))))
    (is (= (:integrator_email idr) (:email real)))
    (is (= (:integrator_name idr) (user-to-integration-data-name real)))
    (is (= (:user_id idf) (:user_id idr)))))

;; Integration data should not be updated if there's a conflict.
(deftest test-integration-data-change-conflict
  (let [u1  (get-user :testde1)
        u2  (get-user :testde2)
        uc  (assoc u1 :username (:username u2))
        id1 (amp/get-integration-data u1)
        id2 (amp/get-integration-data u2)
        idc (amp/get-integration-data uc)]
    (is (not (nil? id1)))
    (is (not (nil? id2)))
    (is (not (nil? idc)))
    (is (= id2 idc))
    (is (= id1 (get-integration-data u1)))))
