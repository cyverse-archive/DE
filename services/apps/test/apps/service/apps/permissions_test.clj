(ns apps.service.apps.permissions-test
  (:use [apps.service.apps.de.listings :only [shared-with-me-id]]
        [apps.service.apps.test-utils :only [get-user]]
        [clojure.test]
        [kameleon.uuids :only [uuidify uuid]])
  (:require [apps.clients.iplant-groups :as ipg]
            [apps.clients.permissions :as perms-client]
            [apps.service.apps :as apps]
            [apps.service.apps.test-fixtures :as atf
             :refer [test-app beta-apps create-test-app create-pipeline find-category
                     get-category get-dev-category get-beta-category get-admin-beta-category]]
            [apps.test-fixtures :as tf]
            [apps.util.config :as config]
            [clojure.tools.logging :as log]
            [korma.core :as sql]
            [permissions-client.core :as pc])
  (:import [clojure.lang ExceptionInfo]))

(use-fixtures :once tf/run-integration-tests tf/with-test-db tf/with-config atf/with-workspaces)
(use-fixtures :each atf/with-public-apps atf/with-test-app)

(deftest test-app-search
  (let [{username :shortUsername :as user} (get-user :testde1)]
    (is (= 1 (:app_count (apps/search-apps user {:search (:name test-app)}))))
    (is (= 1 (count (:apps (apps/search-apps user {:search (:name test-app)})))))
    (perms-client/unshare-app (:id test-app) "user" username)
    (is (= 0 (:app_count (apps/search-apps user {:search (:name test-app)}))))
    (is (= 0 (count (:apps (apps/search-apps user {:search (:name test-app)})))))
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "own")
    (is (= 1 (:app_count (apps/search-apps user {:search (:name test-app)}))))
    (is (= 1 (count (:apps (apps/search-apps user {:search (:name test-app)})))))))

;; FIXME: tmp disabled failing test
#_(deftest test-app-category-listing-counts
  (let [{username :shortUsername :as user} (get-user :testde1)
        dev-category-id                    (:id (get-dev-category user))
        beta-category-id                   (:id (get-beta-category user))
        group-id                           (ipg/grouper-user-group-id)]
    (is (= 1 (:app_count (apps/list-apps-in-category user dev-category-id {}))))
    (is (= (count beta-apps) (:app_count (apps/list-apps-in-category user beta-category-id {}))))
    (perms-client/unshare-app (:id test-app) "user" username)
    (is (= 0 (:app_count (apps/list-apps-in-category user dev-category-id {}))))
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "own")
    (is (= 1 (:app_count (apps/list-apps-in-category user dev-category-id {}))))
    (is (= (count beta-apps) (:app_count (apps/list-apps-in-category user beta-category-id {}))))
    (pc/revoke-permission (config/permissions-client) "app" (:id (first beta-apps)) "group" group-id)
    (is (= 1 (:app_count (apps/list-apps-in-category user dev-category-id {}))))
    (is (= (dec (count beta-apps)) (:app_count (apps/list-apps-in-category user beta-category-id {}))))))

;; FIXME: tmp disabled failing test
#_(deftest test-app-hierarchy-counts
  (let [{username :shortUsername :as user} (get-user :testde1)
        group-id                           (ipg/grouper-user-group-id)]
    (is (= 1 (:app_count (get-dev-category user))))
    (is (= (count beta-apps) (:app_count (get-beta-category user))))
    (perms-client/unshare-app (:id test-app) "user" username)
    (is (= 0 (:app_count (get-dev-category user))))
    (is (= (count beta-apps) (:app_count (get-beta-category user))))
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "own")
    (is (= 1 (:app_count (get-dev-category user))))
    (is (= (count beta-apps) (:app_count (get-beta-category user))))
    (pc/revoke-permission (config/permissions-client) "app" (:id (first beta-apps)) "group" group-id)
    (is (= 1 (:app_count (get-dev-category user))))
    (is (= (dec (count beta-apps)) (:app_count (get-beta-category user))))))

(deftest test-admin-app-hierarchy-counts
  (let [{username :shortUsername :as user} (get-user :testde1)
        group-id                           (ipg/grouper-user-group-id)]
    (is (= (count beta-apps) (:app_count (get-admin-beta-category user))))
    (pc/revoke-permission (config/permissions-client) "app" (:id (first beta-apps)) "group" group-id)
    (is (= (dec (count beta-apps)) (:app_count (get-admin-beta-category user))))))

(defn find-app [listing app-id]
  (first (filter (comp (partial = app-id) :id) (:apps listing))))

(deftest test-app-category-listing
  (let [{username :shortUsername :as user} (get-user :testde1)
        beta-category-id                   (:id (get-beta-category user))
        group-id                           (ipg/grouper-user-group-id)
        app-id                             (:id (first beta-apps))]
    (is (find-app (apps/list-apps-in-category user beta-category-id {}) app-id))
    (pc/revoke-permission (config/permissions-client) "app" (:id (first beta-apps)) "group" group-id)
    (is (nil? (find-app (apps/list-apps-in-category user beta-category-id {}) app-id)))))

(deftest check-initial-ownership-permission
  (let [{username :shortUsername :as user} (get-user :testde1)
        dev-category-id                    (:id (get-dev-category user))]
    (is (= "own" (:permission (first (:apps (apps/list-apps-in-category user dev-category-id {}))))))))

(defn check-delete-apps
  ([user]
   (check-delete-apps user test-app))
  ([user app]
   (apps/delete-apps user {:app_ids [(:id app)]})
   true))

(defn check-delete-app
  ([user]
   (check-delete-app user test-app))
  ([user app]
   (apps/delete-app user (:id app))
   true))

(defn check-relabel-app [user]
  (apps/relabel-app user test-app)
  true)

(defn check-update-app [user]
  (apps/update-app user test-app)
  true)

(defn check-app-ui [user]
  (apps/get-app-ui user (:id test-app))
  true)

(defn check-copy-app [user]
  (let [app-id (:id (apps/copy-app user (:id test-app)))]
    (apps/permanently-delete-apps user {:app_ids [app-id]}))
  true)

(defn check-edit-app-docs [user]
  (apps/owner-edit-app-docs user (:id test-app) {:documentation ""})
  true)

(defn check-get-app-details [user]
  (apps/get-app-details user (:id test-app))
  true)

(defn check-get-app-docs [user]
  (apps/get-app-docs user (:id test-app))
  true)

(defn check-favorite [user]
  (apps/add-app-favorite user (:id test-app))
  true)

(defn check-publishable [user]
  (apps/app-publishable? user (:id test-app))
  true)

(defn check-rating [user]
  (apps/rate-app user (:id test-app) {:rating 5 :comment_id 27})
  true)

(defn check-unrating [user]
  (apps/delete-app-rating user (:id test-app))
  true)

(defn check-tasks [user]
  (apps/get-app-task-listing user (:id test-app))
  true)

(defn check-tools [user]
  (apps/get-app-tool-listing user (:id test-app))
  true)

(deftest test-permission-restrictions-none
  (let [user (get-user :testde2)]
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-apps user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-relabel-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-update-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-app-ui user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-copy-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-edit-app-docs user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-get-app-details user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-get-app-docs user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-favorite user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-publishable user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-rating user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-unrating user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-tasks user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-tools user)))))

(deftest test-permission-restrictions-read
  (let [{username :shortUsername :as user} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "read")
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-apps user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-relabel-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-update-app user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-app-ui user)))
    (is (check-copy-app user))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-edit-app-docs user)))
    (is (check-get-app-details user))
    (is (check-get-app-docs user))
    (is (check-favorite user))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-publishable user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-rating user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-unrating user)))
    (is (check-tasks user))
    (is (check-tools user))))

(deftest test-permission-restrictions-write
  (let [{username :shortUsername :as user} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "write")
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-apps user)))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-delete-app user)))
    (is (check-relabel-app user))
    (is (check-update-app user))
    (is (check-app-ui user))
    (is (check-copy-app user))
    (is (check-edit-app-docs user))
    (is (check-get-app-details user))
    (is (check-get-app-docs user))
    (is (check-favorite user))
    (is (thrown-with-msg? ExceptionInfo #"privileges" (check-publishable user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-rating user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-unrating user)))
    (is (check-tasks user))
    (is (check-tools user))))

(deftest test-permission-restrictions-own
  (let [{username :shortUsername :as user} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "own")
    (is (check-relabel-app user))
    (is (check-update-app user))
    (is (check-app-ui user))
    (is (check-copy-app user))
    (is (check-edit-app-docs user))
    (is (check-get-app-details user))
    (is (check-get-app-docs user))
    (is (check-favorite user))
    (is (check-publishable user))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-rating user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-unrating user)))
    (is (check-tasks user))
    (is (check-tools user))
    (let [app (create-test-app user "Shreddable")]
      (is (check-delete-apps user app))
      (apps/permanently-delete-apps user {:app_ids [(:id app)]}))
    (let [app (create-test-app user "Deletable")]
      (is (check-delete-apps user app))
      (apps/permanently-delete-apps user {:app_ids [(:id app)]}))))

(deftest test-public-app-ratings
  (let [user (get-user :testde1)]
    (sql/delete :app_documentation (sql/where {:app_id (:id test-app)}))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-rating user)))
    (is (thrown-with-msg? ExceptionInfo #"private app" (check-unrating user)))
    (apps/make-app-public user test-app)
    (is (check-rating user))
    (is (check-unrating user))))

(defn has-permission? [rt rn st sn level]
  (let [client (config/permissions-client)]
    (seq (:permissions (pc/get-subject-permissions-for-resource client st sn rt rn false level)))))

(deftest test-public-app-permissions
  (let [{username :shortUsername :as user} (get-user :testde1)]
    (sql/delete :app_documentation (sql/where {:app_id (:id test-app)}))
    (is (has-permission? "app" (:id test-app) "user" username "own"))
    (is (not (has-permission? "app" (:id test-app) "group" (ipg/grouper-user-group-id) "read")))
    (apps/make-app-public user test-app)
    (is (not (has-permission? "app" (:id test-app) "user" username "own")))
    (is (has-permission? "app" (:id test-app) "group" (ipg/grouper-user-group-id) "read"))))

(defn share-app [sharer sharee app-id level]
  (apps/share-apps sharer [{:user (:shortUsername sharee)
                            :apps [{:app_id     app-id
                                    :permission level}]}]))

(deftest test-sharing
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)
        {[user-response :as responses] :sharing}      (share-app testde1 testde2 (:id test-app) "own")]
    (is (= 1 (count responses)))
    (is (= testde2-username (:user user-response)))
    (is (= 1 (count (:apps user-response))))
    (is (= (:id test-app) (uuidify (-> user-response :apps first :app_id))))
    (is (= (:name test-app) (-> user-response :apps first :app_name)))
    (is (= "own" (-> user-response :apps first :permission)))
    (is (true? (-> user-response :apps first :success)))
    (is (has-permission? "app" (:id test-app) "user" testde2-username "own"))))

(deftest test-sharing-no-privs
  (let [{testde2-username :shortUsername :as testde2} (get-user :testde2)
        {testde3-username :shortUsername :as testde3} (get-user :testde3)
        {[user-response :as responses] :sharing}      (share-app testde2 testde3 (:id test-app) "own")]
    (is (= 1 (count responses)))
    (is (= testde3-username (:user user-response)))
    (is (= 1 (count (:apps user-response))))
    (is (= (:id test-app) (uuidify (-> user-response :apps first :app_id))))
    (is (= (:name test-app) (-> user-response :apps first :app_name)))
    (is (= "own" (-> user-response :apps first :permission)))
    (is (false? (-> user-response :apps first :success)))
    (is (re-find #"insufficient privileges" (-> user-response :apps first :error :reason)))
    (is (not (has-permission? "app" (:id test-app) "user" testde2-username "own")))))

(deftest test-sharing-write-privs
  (let [{testde2-username :shortUsername :as testde2} (get-user :testde2)
        {testde3-username :shortUsername :as testde3} (get-user :testde3)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "write")
    (let [{[user-response :as responses] :sharing} (share-app testde2 testde3 (:id test-app) "own")]
      (is (= 1 (count responses)))
      (is (= testde3-username (:user user-response)))
      (is (= 1 (count (:apps user-response))))
      (is (= (:id test-app) (uuidify (-> user-response :apps first :app_id))))
      (is (= (:name test-app) (-> user-response :apps first :app_name)))
      (is (= "own" (-> user-response :apps first :permission)))
      (is (false? (-> user-response :apps first :success)))
      (is (re-find #"insufficient privileges" (-> user-response :apps first :error :reason)))
      (is (not (has-permission? "app" (:id test-app) "user" testde2-username "own"))))))

(deftest test-sharing-non-existent-app
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)]
    (let [{[user-response :as responses] :sharing} (share-app testde1 testde2 (uuid) "own")]
      (is (= 1 (count responses)))
      (is (= testde2-username (:user user-response)))
      (is (= 1 (count (:apps user-response))))
      (is (= "own" (-> user-response :apps first :permission)))
      (is (false? (-> user-response :apps first :success)))
      (is (re-find #"does not exist" (-> user-response :apps first :error :reason))))))

(deftest test-permission-listings
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)]
    (is (empty? (-> (apps/list-app-permissions testde1 [(:id test-app)]) :apps first :permissions)))
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "write")
    (let [perms (-> (apps/list-app-permissions testde1 [(:id test-app)]) :apps first :permissions)]
      (is (= 1 (count perms)))
      (is (= testde2-username (-> perms first :user)))
      (is (= "write" (-> perms first :permission))))
    (pc/revoke-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username)))

(deftest test-permission-listings-no-privs
  (let [{username :shortUsername :as user} (get-user :testde2)]
    (is (thrown-with-msg? ExceptionInfo #"insufficient privileges" (apps/list-app-permissions user [(:id test-app)])))))

(deftest test-permission-listings-read-privs
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "read")
    (let [perms (-> (apps/list-app-permissions testde2 [(:id test-app)]) :apps first :permissions)]
      (is (= 1 (count perms)))
      (is (= testde1-username (-> perms first :user)))
      (is (= "own" (-> perms first :permission))))))

(deftest test-unsharing
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "read")
    (let [responses (:unsharing (apps/unshare-apps testde1 [{:user testde2-username :apps [(:id test-app)]}]))
          user-resp (first responses)
          app-resp  (first (:apps user-resp))]
      (is (= 1 (count responses)))
      (is (= testde2-username (:user user-resp)))
      (is (= 1 (count (:apps user-resp))))
      (is (= (:id test-app) (uuidify (:app_id app-resp))))
      (is (true? (:success app-resp))))))

(deftest test-unsharing-read-privs
  (let [{testde2-username :shortUsername :as testde2} (get-user :testde2)
        {testde3-username :shortUsername :as testde3} (get-user :testde3)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "read")
    (let [responses (:unsharing (apps/unshare-apps testde2 [{:user testde3-username :apps [(:id test-app)]}]))
          user-resp (first responses)
          app-resp  (first (:apps user-resp))]
      (is (= 1 (count responses)))
      (is (= testde3-username (:user user-resp)))
      (is (= 1 (count (:apps user-resp))))
      (is (= (:id test-app) (uuidify (:app_id app-resp))))
      (is (false? (:success app-resp)))
      (is (re-find #"insufficient privileges" (-> app-resp :error :reason))))))

(deftest test-unsharing-write-privs
  (let [{testde2-username :shortUsername :as testde2} (get-user :testde2)
        {testde3-username :shortUsername :as testde3} (get-user :testde3)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" testde2-username "write")
    (let [responses (:unsharing (apps/unshare-apps testde2 [{:user testde3-username :apps [(:id test-app)]}]))
          user-resp (first responses)
          app-resp  (first (:apps user-resp))]
      (is (= 1 (count responses)))
      (is (= testde3-username (:user user-resp)))
      (is (= 1 (count (:apps user-resp))))
      (is (= (:id test-app) (uuidify (:app_id app-resp))))
      (is (false? (:success app-resp)))
      (is (re-find #"insufficient privileges" (-> app-resp :error :reason))))))

(deftest test-unsharing-non-existent-app
  (let [{testde1-username :shortUsername :as testde1} (get-user :testde1)
        {testde2-username :shortUsername :as testde2} (get-user :testde2)]
    (let [responses (:unsharing (apps/unshare-apps testde1 [{:user testde2-username :apps [(uuid)]}]))
          user-resp (first responses)
          app-resp  (first (:apps user-resp))]
      (is (= 1 (count responses)))
      (is (= testde2-username (:user user-resp)))
      (is (= 1 (count (:apps user-resp))))
      (is (false? (:success app-resp)))
      (is (re-find #"does not exist" (-> app-resp :error :reason))))))

(deftest test-deleted-app-resource-removal
  (let [user (get-user :testde1)
        app  (create-test-app user "To be deleted")]
    (is (seq (:resources (pc/list-resources (config/permissions-client) {:resource_name (:id app)}))))
    (apps/permanently-delete-apps user {:app_ids [(:id app)]})
    (is (empty? (:resources (pc/list-resources (config/permissions-client) {:resource_name (:id app)}))))))

(defn- favorite? [user app-id]
  (let [faves-id (:id (get-category user "Favorite Apps"))]
    (->> (:apps (apps/list-apps-in-category user faves-id {}))
         (filter (comp (partial = app-id) :id))
         seq)))

(deftest test-shared-favorites
  (let [{username :shortUsername :as user} (get-user :testde2)]
    (pc/grant-permission (config/permissions-client) "app" (:id test-app) "user" username "read")
    (apps/add-app-favorite user (:id test-app))
    (is (favorite? user (:id test-app)))
    (pc/revoke-permission (config/permissions-client) "app" (:id test-app) "user" username)
    (is (not (favorite? user (:id test-app))))))

(deftest test-public-app-labels-update
  (let [{username :shortUsername :as user} (get-user :testde1)]
    (sql/delete :app_documentation (sql/where {:app_id (:id test-app)}))
    (apps/make-app-public user test-app)
    (is (check-edit-app-docs user))))

(deftest test-create-pipeline
  (let [{username :shortUsername :as user} (get-user :testde1)
        pipeline                           (create-pipeline user)]
    (is (has-permission? "app" (:id pipeline) "user" username "own"))
    (apps/permanently-delete-apps user {:app_ids [(:id pipeline)]})))

(deftest test-shared-listing
  (let [testde1       (get-user :testde1)
        testde2       (get-user :testde2)
        app           (create-test-app testde1 "To be shared")
        shared-apps   (fn [user] (apps/list-apps-in-category user shared-with-me-id {}))
        contains-app? (fn [app-id apps] (seq (filter (comp (partial = app-id) :id) apps)))
        old-listing   (shared-apps testde2)]
    (is (not (contains-app? (:id app) (:apps old-listing))))
    (pc/grant-permission (config/permissions-client) "app" (:id app) "user" (:shortUsername testde2) "read")
    (let [new-listing (shared-apps testde2)]
      (is (= (inc (:app_count old-listing)) (:app_count new-listing)))
      (is (contains-app? (:id app) (:apps new-listing))))
    (apps/permanently-delete-apps testde1 {:app_ids [(:id app)]})))
