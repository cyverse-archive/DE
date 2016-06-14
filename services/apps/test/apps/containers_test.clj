(ns apps.containers-test
  (:use [clojure.test]
        [apps.containers]
        [apps.test-fixtures :only [run-integration-tests with-test-db]]
        [korma.core :exclude [update]]
        [korma.db]
        [kameleon.entities])
  (:require [korma.core :as sql]))

;;; TODO: Modify these tests so that we can run them multiple times on the same database without them failing.

;;; These tests assume that you have a clean instance of the de
;;; database running locally on port 5432. It's recommended that you
;;; use the de-db and de-db-loader images to get a database running
;;; with docker.

(use-fixtures :once run-integration-tests with-test-db)

(def image-info-map (add-image-info {:name "discoenv/de-db" :tag "latest" :url "https://www.google.com"}))

(def data-container-map
  (add-data-container {:name        "discoenv/foo"
                       :tag         "latest"
                       :url         "https://www.google.com"
                       :name_prefix "foo"
                       :read_only   true}))

(deftest image-tests
  (is (not (image? {:name "test" :tag "test"})))

  (is (image? {:name "discoenv/de-db" :tag "latest"}))

  (is (not (nil? (image-id {:name "discoenv/de-db" :tag "latest"}))))

  (is (= {:name "discoenv/de-db" :tag "latest" :url "https://www.google.com"}
         (dissoc (image-info (image-id {:name "discoenv/de-db" :tag "latest"})) :id))))


(def tool-map (first (select tools (where {:name "notreal"}))))

(def settings-map  (add-settings {:name "test"
                                  :cpu_shares 1024
                                  :memory_limit 2048
                                  :network_mode "bridge"
                                  :working_directory "/work"
                                  :tools_id (:id tool-map)}))

(deftest settings-tests
  (is (not (nil? (:id settings-map))))

  (is (= {:name "test"
          :cpu_shares 1024
          :memory_limit 2048
          :network_mode "bridge"
          :working_directory "/work"
          :tools_id (:id tool-map)
          :entrypoint nil}
         (dissoc (settings (:id settings-map)) :id)))

  (is (settings? (:id settings-map)))

  (is (tool-has-settings? (:id tool-map))))

(def devices-map (add-device (:id settings-map) {:host_path "/dev/null" :container_path "/dev/yay"}))

(deftest devices-tests
  (is (not (nil? (:id devices-map))))

  (is (= {:host_path "/dev/null" :container_path "/dev/yay" :container_settings_id (:id settings-map)}
         (dissoc (device (:id devices-map)) :id)))

  (is (device? (:id devices-map)))

  (is (device-mapping? (:id settings-map) "/dev/null" "/dev/yay"))

  (is (settings-has-device? (:id settings-map) (:id devices-map))))

(def volume-map (add-volume (:id settings-map) {:host_path "/tmp" :container_path "/foo"}))

(deftest volumes-tests
  (is (not (nil? (:id volume-map))))

  (is (= {:host_path "/tmp" :container_path "/foo" :container_settings_id (:id settings-map)}
         (dissoc (volume (:id volume-map)) :id)))

  (is (volume? (:id volume-map)))

  (is (volume-mapping? (:id settings-map) "/tmp" "/foo"))

  (is (settings-has-volume? (:id settings-map) (:id volume-map))))

(def volumes-from-map (add-volumes-from (:id settings-map) (:id data-container-map)))

(deftest volumes-from-test
  (is (not (nil? (:id volumes-from-map))))

  (is (= {:container_settings_id (:id settings-map)
          :data_containers_id    (:id data-container-map)}
         (dissoc (volumes-from (:id volumes-from-map)) :id)))

  (is (volumes-from? (:id volumes-from-map)))

  (is (volumes-from-mapping? (:id settings-map) (:id data-container-map)))

  (is (settings-has-volumes-from? (:id settings-map) (:id volumes-from-map))))


(deftest updated-tool-tests
  (sql/update tools
              (set-fields {:container_images_id (:id image-info-map)})
              (where {:id (:id tool-map)}))
  (let [updated-tool (first (select tools (where {:id (:id tool-map)})))]
    (is (not (nil? (:id updated-tool))))

    (is (= image-info-map (tool-image-info (:id updated-tool))))))
