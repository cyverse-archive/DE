(ns permissions-client.integration-test
  (:require [permissions-client.core :as pc])
  (:use [clojure.test]))

(def ^:dynamic base-uri "http://permissions:60000/")

(defn create-permissions-client []
  (pc/new-permissions-client base-uri))

(defn run-integration-tests [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (f)))

(defn with-base-uri [f]
  (if-let [uri (System/getenv "PERMISSIONS_BASE_URI")]
    (binding [base-uri uri]
      (f))
    (f)))

(defn add-test-subjects []
  (let [client (create-permissions-client)]
    (pc/add-subject client "ipcdev" "user")
    (pc/add-subject client "ipctest" "user")
    (pc/add-subject client "ipcusers" "group")))

(defn remove-test-subjects []
  (let [client (create-permissions-client)]
    (dorun (map (comp (partial pc/delete-subject client) :id)
                (:subjects (pc/list-subjects client))))))

(defn add-test-resources []
  (let [client (create-permissions-client)]
    (pc/add-resource client "a" "app")
    (pc/add-resource client "b" "app")
    (pc/add-resource client "C" "analysis")
    (pc/add-resource client "D" "analysis")))

(defn remove-test-resources []
  (let [client (create-permissions-client)]
    (dorun (map (comp (partial pc/delete-resource client) :id)
                (:resources (pc/list-resources client))))))

(defn remove-test-resource-types []
  (let [client (create-permissions-client)]
    (dorun (map (comp (partial pc/delete-resource-type client) :id)
                (remove (comp #{"app" "analysis"} :name)
                        (:resource_types (pc/list-resource-types client)))))))

(defn add-test-permissions []
  (let [client (create-permissions-client)]
    (pc/grant-permission client "app" "a" "user" "ipcdev" "own")
    (pc/grant-permission client "app" "b" "user" "ipctest" "write")
    (pc/grant-permission client "app" "a" "group" "ipcusers" "read")
    (pc/grant-permission client "app" "b" "group" "ipcusers" "admin")
    (pc/grant-permission client "analysis" "C" "user" "ipcdev" "own")
    (pc/grant-permission client "analysis" "D" "user" "ipctest" "own")
    (pc/grant-permission client "analysis" "C" "group" "ipcusers" "read")))

(defn permission-index
  [perm]
  (mapv (partial get-in perm)
        [[:resource :resource_type] [:resource :name] [:subject :subject_type] [:subject :subject_id]]))

(defn remove-test-permissions []
  (let [client (create-permissions-client)]
    (dorun (map (comp (partial apply pc/revoke-permission client) permission-index)
                (:permissions (pc/list-permissions client))))))

(defn with-test-data [f]
  (add-test-subjects)
  (add-test-resources)
  (add-test-permissions)
  (f)
  (remove-test-subjects)
  (remove-test-resources)
  (remove-test-resource-types)
  (remove-test-permissions))

(use-fixtures :each run-integration-tests with-base-uri with-test-data)

(deftest test-get-status
  (let [status-info (pc/get-status (create-permissions-client))]
    (is (:version status-info))
    (is (:service status-info))
    (is (:description status-info))))

(defn get-subject-map
  ([]
   (get-subject-map (pc/list-subjects (create-permissions-client))))
  ([listing]
   (into {} (map (juxt :subject_id identity) (:subjects listing)))))

(defn subject-correct? [subject subject-id subject-type]
  (and (:id subject)
       (= (:subject_id subject) subject-id)
       (= (:subject_type subject) subject-type)))

(deftest test-list-subjects
  (let [subjects (get-subject-map)]
    (is (subject-correct? (subjects "ipcdev") "ipcdev" "user"))
    (is (subject-correct? (subjects "ipctest") "ipctest" "user"))
    (is (subject-correct? (subjects "ipcusers") "ipcusers" "group"))))

(deftest test-list-subjects-by-type
  (let [subjects (get-subject-map (pc/list-subjects (create-permissions-client) {:subject_type "user"}))]
    (is (= (count subjects) 2))
    (is (subject-correct? (subjects "ipcdev") "ipcdev" "user"))
    (is (subject-correct? (subjects "ipctest") "ipctest" "user"))))

(deftest test-list-subjects-by-id
  (let [subjects (get-subject-map (pc/list-subjects (create-permissions-client) {:subject_id "ipcdev"}))]
    (is (= (count subjects) 1))
    (is (subject-correct? (subjects "ipcdev") "ipcdev" "user"))))

(deftest test-list-subjects-by-id-and-type
  (let [opts     {:subject_id "ipcdev" :subject_type "user"}
        subjects (get-subject-map (pc/list-subjects (create-permissions-client) opts))]
    (is (= (count subjects) 1))
    (is (subject-correct? (subjects "ipcdev") "ipcdev" "user")))
  (let [opts     {:subject_id "ipcdev" :subject_type "group"}
        subjects (get-subject-map (pc/list-subjects (create-permissions-client) opts))]
    (is (= (count subjects) 0))))

(deftest test-add-subject
  (let [client (create-permissions-client)]
    (is (subject-correct? (pc/add-subject client "dark-helmet" "user") "dark-helmet" "user"))
    (let [subjects (get-subject-map)]
      (is (subject-correct? (subjects "dark-helmet") "dark-helmet" "user")))))

(deftest test-delete-subject
  (let [client (create-permissions-client)
        ipcdev ((get-subject-map) "ipcdev")]
    (is (subject-correct? ipcdev "ipcdev" "user"))
    (pc/delete-subject client (:id ipcdev))
    (is (nil? ((get-subject-map) "ipcdev")))))

(deftest test-delete-subject-by-external-id
  (let [client (create-permissions-client)]
    (pc/delete-subject client "ipcdev" "user")
    (is (nil? ((get-subject-map) "ipcdev")))))

(deftest test-update-subject
  (let [client   (create-permissions-client)
        lonestar (pc/add-subject client "lonestar" "user")
        _        (is (subject-correct? lonestar "lonestar" "user"))
        lonestar (pc/update-subject client (:id lonestar) "LONEstarrrr" "user")]
    (is (subject-correct? lonestar "LONEstarrrr" "user"))
    (is (subject-correct? ((get-subject-map) "LONEstarrrr") "LONEstarrrr" "user"))))

(defn get-resource-map
  ([]
   (get-resource-map (pc/list-resources (create-permissions-client))))
  ([listing]
   (into {} (map (juxt :name identity) (:resources listing)))))

(defn resource-correct? [resource name resource-type]
  (and (:id resource)
       (= (:name resource) name)
       (= (:resource_type resource) resource-type)))

(deftest test-list-resources
  (let [resources (get-resource-map)]
    (is (resource-correct? (resources "a") "a" "app"))
    (is (resource-correct? (resources "b") "b" "app"))
    (is (resource-correct? (resources "C") "C" "analysis"))
    (is (resource-correct? (resources "D") "D" "analysis"))))

(deftest test-add-resource
  (let [client (create-permissions-client)]
    (is (resource-correct? (pc/add-resource client "e" "app") "e" "app"))
    (is (resource-correct? ((get-resource-map) "e") "e" "app"))))

(deftest test-delete-resource
  (let [client (create-permissions-client)
        a      ((get-resource-map) "a")]
    (is (resource-correct? a "a" "app"))
    (pc/delete-resource client (:id a))
    (is (nil? ((get-resource-map) "a")))))

(deftest test-update-resource
  (let [client   (create-permissions-client)
        mr-radar (pc/add-resource client "mr-radar" "app")
        _        (is (resource-correct? mr-radar "mr-radar" "app"))
        mr-radar (pc/update-resource client (:id mr-radar) "bleeps-sweeps-creeps")]
    (is (resource-correct? mr-radar "bleeps-sweeps-creeps" "app"))
    (is (resource-correct? ((get-resource-map) "bleeps-sweeps-creeps") "bleeps-sweeps-creeps" "app"))))

(defn get-resource-type-map
  ([]
   (get-resource-type-map (pc/list-resource-types (create-permissions-client))))
  ([listing]
   (into {} (map (juxt :name identity) (:resource_types listing)))))

(defn resource-type-correct? [resource-type name & [description]]
  (and (:id resource-type)
       (= (:name resource-type) name)
       (if description (= (:description resource-type) description) true)))

(deftest test-list-resource-types
  (let [rts (get-resource-type-map)]
    (is (resource-type-correct? (rts "app") "app"))
    (is (resource-type-correct? (rts "analysis") "analysis"))))

(deftest test-add-resource-type
  (let [client (create-permissions-client)]
    (is (resource-type-correct? (pc/add-resource-type client "mog" "half-man-half-dog") "mog" "half-man-half-dog"))
    (is (resource-type-correct? ((get-resource-type-map) "mog") "mog" "half-man-half-dog"))))

(deftest test-delete-resource-type
  (let [client (create-permissions-client)
        mog    (pc/add-resource-type client "mog" "half-man-half-dog")]
    (is (resource-type-correct? mog "mog" "half-man-half-dog"))
    (pc/delete-resource-type client (:id mog))
    (is (nil? ((get-resource-type-map) "mog")))))

(deftest test-update-resource-type
  (let [client (create-permissions-client)
        mog    (pc/add-resource-type client "mog" "half-man-half-dog")
        _      (is (resource-type-correct? mog "mog" "half-man-half-dog"))
        mog    (pc/update-resource-type client (:id mog) "mog" "own-best-friend")]
    (is (resource-type-correct? mog "mog" "own-best-friend"))
    (is (resource-type-correct? ((get-resource-type-map) "mog") "mog" "own-best-friend"))))

(defn get-permission-map
  ([]
   (get-permission-map (pc/list-permissions (create-permissions-client))))
  ([listing]
   (into {} (map (juxt permission-index identity) (:permissions listing)))))

(defn get-perm [permissions-map rt rn st sn]
  (permissions-map [rt rn st sn]))

(defn permission-correct? [perm rt rn st sn l]
  (and (:id perm)
       (resource-correct? (:resource perm) rn rt)
       (subject-correct? (:subject perm) sn st)
       (= (:permission_level perm) l)))

(defn looked-up-permission-correct? [perms rt rn st sn l]
  (permission-correct? (get-perm perms rt rn st sn) rt rn st sn l))

(deftest test-list-perms
  (let [perms (get-permission-map)]
    (is (looked-up-permission-correct? perms "app" "a" "user" "ipcdev" "own"))
    (is (looked-up-permission-correct? perms "app" "b" "user" "ipctest" "write"))
    (is (looked-up-permission-correct? perms "app" "a" "group" "ipcusers" "read"))
    (is (looked-up-permission-correct? perms "app" "b" "group" "ipcusers" "admin"))
    (is (looked-up-permission-correct? perms "analysis" "C" "user" "ipcdev" "own"))
    (is (looked-up-permission-correct? perms "analysis" "D" "user" "ipctest" "own"))
    (is (looked-up-permission-correct? perms "analysis" "C" "group" "ipcusers" "read"))))

(deftest test-grant-perm
  (let [client (create-permissions-client)
        perm   (pc/grant-permission client "app" "mr-radar" "user" "dark-helmet" "read")]
    (is (permission-correct? perm "app" "mr-radar" "user" "dark-helmet" "read"))
    (is (looked-up-permission-correct? (get-permission-map) "app" "mr-radar" "user" "dark-helmet" "read"))))

(deftest test-revoke-perm
  (let [client (create-permissions-client)
        perm   (pc/grant-permission client "app" "mr-radar" "user" "vespa" "read")]
    (looked-up-permission-correct? (get-permission-map) "app" "mr-radar" "user" "vespa" "read")
    (pc/revoke-permission client "app" "mr-radar" "user" "vespa")
    (is (nil? (get-perm (get-permission-map) "app" "mr-radar" "user" "vespa")))))

(deftest test-list-resource-perms
  (let [client (create-permissions-client)
        perms  (get-permission-map (pc/list-resource-permissions client "app" "a"))]
    (is (= (count perms) 2))
    (is (looked-up-permission-correct? perms "app" "a" "user" "ipcdev" "own"))
    (is (looked-up-permission-correct? perms "app" "a" "group" "ipcusers" "read"))))

(deftest test-get-subject-permissions
  (let [client (create-permissions-client)
        perms  (get-permission-map (pc/get-subject-permissions client "user" "ipcdev" false))]
    (is (= (count perms) 2))
    (is (looked-up-permission-correct? perms "app" "a" "user" "ipcdev" "own"))
    (is (looked-up-permission-correct? perms "analysis" "C" "user" "ipcdev" "own"))))

(deftest test-get-subject-permissions-min-level
  (let [client (create-permissions-client)
        perms  (get-permission-map (pc/get-subject-permissions client "group" "ipcusers" false "admin"))]
    (is (= (count perms) 1))
    (is (looked-up-permission-correct? perms "app" "b" "group" "ipcusers" "admin"))))

(deftest test-get-subject-permissions-for-resource-type
  (let [client (create-permissions-client)
        resp   (pc/get-subject-permissions-for-resource-type client "group" "ipcusers" "app" true)
        perms  (get-permission-map resp)]
    (is (= (count perms) 2))
    (is (looked-up-permission-correct? perms "app" "a" "group" "ipcusers" "read"))
    (is (looked-up-permission-correct? perms "app" "b" "group" "ipcusers" "admin"))))

(deftest test-get-subject-permissions-for-resource-type-min-level
  (let [client (create-permissions-client)
        resp   (pc/get-subject-permissions-for-resource-type client "group" "ipcusers" "app" true "admin")
        perms  (get-permission-map resp)]
    (is (= (count perms) 1))
    (is (looked-up-permission-correct? perms "app" "b" "group" "ipcusers" "admin"))))

(deftest test-get-subject-permissions-for-resource
  (let [client (create-permissions-client)
        resp   (pc/get-subject-permissions-for-resource client "group" "ipcusers" "app" "a" true)
        perms  (get-permission-map resp)]
    (is (= (count perms) 1))
    (is (looked-up-permission-correct? perms "app" "a" "group" "ipcusers" "read"))))

(deftest test-get-subject-permissions-for-resource-min-level
  (let [client (create-permissions-client)
        resp   (pc/get-subject-permissions-for-resource client "group" "ipcusers" "app" "a" true "admin")
        perms  (get-permission-map resp)]
    (is (= (count perms) 0)))
  (let [client (create-permissions-client)
        resp   (pc/get-subject-permissions-for-resource client "group" "ipcusers" "app" "b" true "admin")
        perms  (get-permission-map resp)]
    (is (= (count perms) 1))
    (is (looked-up-permission-correct? perms "app" "b" "group" "ipcusers" "admin"))))
