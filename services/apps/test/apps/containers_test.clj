(ns apps.containers-test
  (:use [clojure.test]
        [apps.containers]
        [apps.test-fixtures :only [run-integration-tests with-test-db]]
        [korma.core :exclude [update]]
        [korma.db]
        [kameleon.entities])
  (:require [clojure.tools.logging :as log]
            [korma.core :as sql]))

(def ^:dynamic image-info-map nil)
(def ^:dynamic data-container-map nil)
(def ^:dynamic tool-map nil)
(def ^:dynamic settings-map nil)
(def ^:dynamic devices-map nil)
(def ^:dynamic volume-map nil)
(def ^:dynamic volumes-from-map nil)

(defn- add-image-info-map []
  (add-image-info {:name "discoenv/de-db" :tag "latest" :url "https://www.google.com"}))

(defn- add-data-container-map []
  (add-data-container {:name        "discoenv/foo"
                       :tag         "latest"
                       :url         "https://www.google.com"
                       :name_prefix "foo"
                       :read_only   true}))

(defn- get-tool-map []
  (first (select tools (where {:name "notreal"}))))

(defn- add-settings-map [tool-id]
  (add-settings {:name              "test"
                 :cpu_shares        1024
                 :memory_limit      2048
                 :network_mode      "bridge"
                 :working_directory "/work"
                 :tools_id          tool-id}))

(defn- add-devices-map [settings-id]
  (add-device settings-id {:host_path "/dev/null" :container_path "/dev/yay"}))

(defn- add-volume-map [settings-id]
  (add-volume settings-id {:host_path "/tmp" :container_path "/foo"}))

(defn- add-volumes-from-map [settings-id data-container-id]
  (add-volumes-from settings-id data-container-id))

(defn- add-test-data []
  (let [image-info     (add-image-info-map)
        data-container (add-data-container-map)
        tool           (get-tool-map)
        settings       (add-settings-map (:id tool))
        device         (add-devices-map (:id settings))
        volume         (add-volume-map (:id settings))
        volumes-from   (add-volumes-from-map (:id settings) (:id data-container))]
    (vector image-info data-container tool settings device volume volumes-from)))

(defn- remove-container-image-references []
  (sql/update tools
              (set-fields {:container_images_id nil})
              (where {:container_images_id (:id image-info-map)})))

(defn- remove-test-data []
  (delete container-volumes-from (where {:id (:id volumes-from-map)}))
  (delete container-volumes (where {:id (:id volume-map)}))
  (delete container-devices (where {:id (:id devices-map)}))
  (delete container-settings (where {:id (:id settings-map)}))
  (delete data-containers (where {:id (:id data-container-map)}))
  (remove-container-image-references)
  (delete container-images (where {:id (:id image-info-map)})))

(defn- with-test-data [f]
  (let [[image-info data-container tool settings device volume volumes-from] (add-test-data)]
    (binding [image-info-map     image-info
              data-container-map data-container
              tool-map           tool
              settings-map       settings
              devices-map        device
              volume-map         volume
              volumes-from-map   volumes-from]
      (f)
      (remove-test-data))))

(use-fixtures :once with-test-db run-integration-tests)
(use-fixtures :each with-test-data)

(deftest image-tests
  (is (not (image? {:name "test" :tag "test"})))

  (is (image? {:name "discoenv/de-db" :tag "latest"}))

  (is (not (nil? (image-id {:name "discoenv/de-db" :tag "latest"}))))

  (is (= {:name "discoenv/de-db" :tag "latest" :url "https://www.google.com"}
         (dissoc (image-info (image-id {:name "discoenv/de-db" :tag "latest"})) :id))))


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

(deftest devices-tests
  (is (not (nil? (:id devices-map))))

  (is (= {:host_path "/dev/null" :container_path "/dev/yay" :container_settings_id (:id settings-map)}
         (dissoc (device (:id devices-map)) :id)))

  (is (device? (:id devices-map)))

  (is (device-mapping? (:id settings-map) "/dev/null" "/dev/yay"))

  (is (settings-has-device? (:id settings-map) (:id devices-map))))

(deftest volumes-tests
  (is (not (nil? (:id volume-map))))

  (is (= {:host_path "/tmp" :container_path "/foo" :container_settings_id (:id settings-map)}
         (dissoc (volume (:id volume-map)) :id)))

  (is (volume? (:id volume-map)))

  (is (volume-mapping? (:id settings-map) "/tmp" "/foo"))

  (is (settings-has-volume? (:id settings-map) (:id volume-map))))

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
