(ns facepalm.c260-2016030901
  (:use [korma.core]))

(def ^:private version
  "The destination database version."
  "2.6.0:20160309.01")

(defn- add-container-volume-settings
  [settings-id]
  (let [required-volumes #{{:host_path "/usr/local2/" :container_path "/usr/local2/"}
                           {:host_path "/usr/local3/" :container_path "/usr/local3/"}
                           {:host_path "/data2/"      :container_path "/data2/"}}
        existing-volumes (set (select :container_volumes
                                      (fields :host_path :container_path)
                                      (where {:container_settings_id settings-id})))
        volumes-to-add   (clojure.set/difference required-volumes existing-volumes)
        insert-volumes   (map #(assoc % :container_settings_id settings-id) volumes-to-add)]
       (when-not (empty? volumes-to-add)
         (insert :container_volumes (values insert-volumes)))))

(defn- add-backwards-compat-volume-settings
  []
  (println "\t* Adding volume settings to tools using backwards-compat...")
  (let [img-id-subselect (subselect :container_images
                                    (fields :id)
                                    (where {:name [like "%backwards-compat"]}))
        settings-ids (map :id (select [:container_settings :cs]
                                      (fields :cs.id)
                                      (join :inner [:tools :t] {:t.id :cs.tools_id})
                                      (where {:container_images_id img-id-subselect})))]
       (doseq [id settings-ids]
              (add-container-volume-settings id))))

(defn convert
  "Performs the conversion for this database version"
  []
  (println "Performing the conversion for" version)
  (add-backwards-compat-volume-settings))
