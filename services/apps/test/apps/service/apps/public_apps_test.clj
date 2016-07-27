(ns apps.service.apps.public-apps-test
  (:use [apps.service.apps.test-utils :only [get-user]]
        [clojure.test])
  (:require [apps.service.apps :as apps]
            [apps.service.apps.test-fixtures :as atf]
            [apps.test-fixtures :as tf]
            [apps.util.config :as config]
            [clojure.tools.logging :as log]
            [korma.core :as sql]))

(use-fixtures :once tf/run-integration-tests tf/with-test-db tf/with-config atf/with-workspaces)
(use-fixtures :each atf/with-public-apps atf/with-test-app)

(deftest test-marked-as-public
  (let [user    (get-user :testde1)
        app     (atf/create-test-app user "To be published")
        _       (sql/delete :app_documentation (sql/where {:app_id (:id app)}))
        _       (apps/make-app-public user app)
        listed  (first (:apps (apps/search-apps user {:search (:name app)})))
        beta-id (:id (atf/get-beta-category user))
        apps    (:apps (apps/list-apps-in-category user beta-id {}))]
    (is (not (nil? listed)))
    (is (:is_public listed))
    (is (empty? (filter (comp (partial = (:id app)) :id) apps)))))
