(ns terrain.services.metadata.internal-jobs
  (:use [slingshot.slingshot :only [throw+]]
        [terrain.auth.user-attributes :only [current-user]])
  (:require [clojure-commons.error-codes :as ce]
            [terrain.clients.apps :as apps]
            [terrain.clients.user-prefs :as prefs]
            [terrain.util.config :as config]))

(defn- get-url-import-notify-pref
  "Looks up the current user's enableImportEmailNotification preference.
   Returns true by default if the preference is not set or could not be retrieved."
  []
  (:enableImportEmailNotification (prefs/get-prefs-safe (:username current-user)) true))

(defn- load-param-map
  [app-id]
  (->> (apps/get-app app-id)
       (:groups)
       (mapcat :parameters)
       (map (juxt :label :id))
       (into {})))

;; TODO: find a better way to associate values with parameters.
(defn- build-config
  "Builds the configuration for an internal job submission. The first argument, param-map, is
   the parameter map returned by load-param-map above. It maps the parameter label to the
   parameter ID. The second argument, label-map, is another map that maps the parameter keyword
   used internally to the parameter label found in the app. The last argument, params, maps the
   keyword used internally to the parameter value."
  [param-map label-map params]
  (into {} (map (juxt (comp param-map label-map key) val) params)))

(defn- build-app-config
  [app-id label-map params]
  (build-config (load-param-map app-id) label-map params))

(def ^:private url-import-label-map
  {:address  "Source URL"
   :filename "Output Filename"})

(defn- avu
  [attr value unit]
  {:attr attr :value value :unit unit})

(defn- build-url-import-config
  [address filename]
  (->> {:address address :filename filename}
       (build-app-config (config/fileio-url-import-app) url-import-label-map)))

(defn- build-url-import-job-submission
  [address filename dest-path]
  {:config               (build-url-import-config address filename)
   :description          (str "URL Import of " filename " from " address)
   :name                 (str "url_import_" filename)
   :app_id               (str (config/fileio-url-import-app))
   :debug                false
   :create_output_subdir false
   :output_dir           dest-path
   :notify               (get-url-import-notify-pref)
   :skip-parent-meta     true
   :file-metadata        [(avu "ipc-url-import" address "Import URL")]
   :archive_logs         false})

(defn- launch-url-import-job
  [address filename dest-path]
  (->> (build-url-import-job-submission address filename dest-path)
       (apps/submit-job)))

(defn- unknown-job-type
  [job-type]
  (throw+ {:error_code ce/ERR_CONFIG_INVALID
           :reason     (str "unknown job type: " job-type)}))

(defn- get-submitter
  [job-type]
  (cond (= job-type :url-import) launch-url-import-job
        :else                    (unknown-job-type job-type)))

(defn submit
  [job-type params]
  (apply (get-submitter job-type) params))
