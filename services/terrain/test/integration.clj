(ns integration
  (:use [clojure.test])
  (:require [terrain.clients.data-info.raw :as data-info-client]
            [terrain.util.config :as config]))

(defn- integration-test-setup
  []
  (config/load-config-from-file (System/getenv "TERRAIN_CONFIG_PATH")))

(defn run-integration-tests
  [f]
  (when (System/getenv "RUN_INTEGRATION_TESTS")
    (integration-test-setup)
    (f)))

(def test-user "ipctest")
(def ^:dynamic test-data-item nil)

(defn- create-test-file
  [user]
  (let [dest-path (str "/iplant/home/" user)
        filename  (str "integration-test-file-" (java.util.UUID/randomUUID) ".txt")
        istream   (java.io.ByteArrayInputStream. (.getBytes "testing" "UTF-8"))]
    (-> (data-info-client/upload-file user dest-path filename "text/plain" istream :as :json)
        :body
        :file)))

(defn with-test-data-item
  [f]
  (binding [test-data-item (create-test-file test-user)]
    (f)
    ;; Send the delete request twice, so that it's deleted from the Trash
    (data-info-client/delete-data-item test-user (:id test-data-item))
    (data-info-client/delete-data-item test-user (:id test-data-item))))
