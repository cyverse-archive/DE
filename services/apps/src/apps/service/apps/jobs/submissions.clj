(ns apps.service.apps.jobs.submissions
  (:use [clojure-commons.core :only [remove-nil-values]]
        [slingshot.slingshot :only [try+ throw+]]
        [kameleon.uuids :only [uuid]])
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure-commons.error-codes :as ce]
            [clojure-commons.file-utils :as ft]
            [kameleon.db :as db]
            [apps.clients.data-info :as data-info]
            [apps.clients.iplant-groups :as iplant-groups]
            [apps.persistence.app-metadata :as ap]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.job-listings :as job-listings]
            [apps.util.config :as config]
            [apps.util.service :as service]))

(defn- get-app-params
  [app type-set]
  (->> (:groups app)
       (mapcat :parameters)
       (filter (comp type-set :type))
       (map (juxt (comp keyword :id) identity))
       (into {})))

(defn- get-file-stats
  [user paths]
  (try+
   (data-info/get-file-stats user paths)
   (catch Object _
     (log/error (:throwable &throw-context)
                "job submission failed: Could not lookup info types of inputs.")
     (throw+))))

(defn- get-paths-exist
  [user paths]
  (try+
    (:paths (data-info/get-paths-exist user paths))
    (catch Object _
      (log/error (:throwable &throw-context)
                 "job submission failed: Could not lookup existence of HT paths.")
      (throw+))))

(defn- load-path-list-stats
  [user input-paths-by-id]
  (->> (flatten (vals input-paths-by-id))
       (remove string/blank?)
       (get-file-stats user)
       (:paths)
       (map val)
       (filter (comp (partial = (config/path-list-info-type)) :infoType))))

(defn- param-value-contains-paths?
  [paths [_ v]]
  (if (sequential? v)
    (some (set paths) v)
    ((set paths) v)))

(defn- extract-ht-param-ids
  [path-list-stats input-paths-by-id]
  (let [ht-paths (set (map :path path-list-stats))]
    (map key (filter (partial param-value-contains-paths? ht-paths) input-paths-by-id))))

(defn- max-path-list-size-exceeded
  [max-size path actual-size]
  (throw+
    {:type      :clojure-commons.exception/illegal-argument
     :error     (str "HT Analysis Path List file exceeds maximum size of " max-size " bytes.")
     :path      path
     :file-size actual-size}))

(defn- max-batch-paths-exceeded
  [max-paths first-list-path first-list-count]
  (throw+
    {:type       :clojure-commons.exception/illegal-argument
     :error      (str "The HT Analysis Path List exceeds the maximum of "
                      max-paths
                      " allowed paths.")
     :path       first-list-path
     :path-count first-list-count}))

(defn- validate-path-list-stats
  [{path :path actual-size :file-size}]
  (when (> actual-size (config/path-list-max-size))
    (max-path-list-size-exceeded (config/path-list-max-size) path actual-size)))

(defn- validate-ht-params
  [ht-params]
  (when (some (comp (partial = ap/param-multi-input-type) :type) ht-params)
    (throw+ {:type  :clojure-commons.exception/illegal-argument
             :error "HT Analysis Path List files are not supported in multi-file inputs."})))

(defn- path-exists?
  [[path exists?]]
  exists?)

(defn- every-input-exists?
  [paths-exist-map & job-inputs]
  (every? #(get paths-exist-map (keyword %)) job-inputs))

(defn- extract-missing-paths
  [paths-exist]
  (map name (keys (remove path-exists? paths-exist))))

(defn- validate-path-lists
  [user path-lists]
  (let [[first-list-path first-list] (first path-lists)
        first-list-count             (count first-list)
        path-lists-vals              (vals path-lists)
        paths-exist-list             (map (partial get-paths-exist user) path-lists-vals)
        paths-exist                  (apply merge paths-exist-list)]
    (when (> first-list-count (config/path-list-max-paths))
      (max-batch-paths-exceeded (config/path-list-max-paths) first-list-path first-list-count))
    (when-not (every? (comp (partial = first-list-count) count second) path-lists)
      (throw+ {:type  :clojure-commons.exception/illegal-argument
               :error "All HT Analysis Path Lists must have the same number of paths."}))
    (when-not (every? (partial some path-exists?) paths-exist-list)
      (throw+ {:type  :clojure-commons.exception/not-found
               :error "One or more HT Analysis Path List inputs contain paths that no longer exist."
               :missing-paths (extract-missing-paths paths-exist)}))
    (when-not (some true? (apply map (partial every-input-exists? paths-exist) path-lists-vals))
      (throw+ {:type  :clojure-commons.exception/not-found
               :error "No jobs could be submitted for existing inputs in given HT Analysis Path Lists."
               :missing-paths (extract-missing-paths paths-exist)}))
    paths-exist))

(defn- get-path-list-contents
  [user path]
  (try+
   (when (seq path) (data-info/get-path-list-contents user path))
   (catch Object _
     (log/error (:throwable &throw-context)
                "job submission failed: Could not get file contents of path list input.")
     (throw+))))

(defn- get-path-list-contents-map
  [user paths]
  (into {} (map (juxt identity (partial get-path-list-contents user)) paths)))

(defn- get-batch-output-dir
  [user submission]
  (let [output-dir (ft/build-result-folder-path submission)]
    (try+
     (data-info/get-file-stats user [output-dir])
     ; FIXME Update this when data-info's exception handling is updated
     (catch [:status 500] {:keys [body]}
       ;; The caught error can't be rethrown since we parse the body to examine its error code.
       ;; So we must throw the parsed body, but also clear out the `cause` in our `throw+` call,
       ;; since the transaction wrapping these functions will try to only rethrow this caught error.
       (let [error (service/parse-json body)]
         (if (= (:error_code error) ce/ERR_DOES_NOT_EXIST)
           (data-info/create-directory user output-dir)
           (throw+ error nil)))))
    output-dir))

(defn- save-batch*
  [user app submission output-dir]
  (:id (jp/save-job {:job-name           (:name submission)
                     :description        (:description submission)
                     :app-id             (:app_id submission)
                     :app-name           (:name app)
                     :app-description    (:description app)
                     :result-folder-path output-dir
                     :start-date         (db/now)
                     :status             jp/submitted-status
                     :username           (:username user)
                     :notify             (:notify submission)}
                    submission)))

(defn- save-batch-step
  [batch-id job-type]
  (jp/save-job-step {:job-id          batch-id
                     :step-number     1
                     :status          jp/submitted-status
                     :app-step-number 1
                     :job-type        job-type}))

(defn- save-batch
  [user job-types app submission output-dir]
  (let [batch-id (save-batch* user app submission output-dir)]
    (save-batch-step batch-id (first job-types))
    batch-id))

(defn- map-slice
  [m n]
  (->> (map (fn [[k v]] (vector k (nth v n))) m)
       (into {})
       (remove-nil-values)))

(defn- map-slices
  [m]
  (let [max-count (apply max (map (comp count val) m))]
    (mapv (partial map-slice m) (range max-count))))


(defn- substitute-param-values
  [path-map config]
  (->> (map (fn [[k v]] (vector k (get path-map v v))) config)
       (into {})))

(defn- format-submission-in-batch
  [submission job-number path-map]
  (let [job-suffix (str "analysis-" (inc job-number))]
    (assoc (update-in submission [:config] (partial substitute-param-values path-map))
      :name       (str (:name submission) "-" job-suffix)
      :output_dir (ft/path-join (:output_dir submission) job-suffix))))

(defn- submit-job-in-batch
  [apps-client user submission paths-exist job-number path-map]
  (when (every? (partial get paths-exist) (map keyword (vals path-map)))
    (let [job-info (.submitJob apps-client (format-submission-in-batch submission job-number path-map))]
      (iplant-groups/register-analysis (:shortUsername user) (:id job-info))
      job-info)))

(defn- preprocess-batch-submission
  [submission output-dir parent-id]
  (assoc submission
    :output_dir           output-dir
    :parent_id            parent-id
    :create_output_subdir false))

(defn- submit-batch-job
  [apps-client user input-params-by-id input-paths-by-id path-list-stats job-types app submission]
  (dorun (map validate-path-list-stats path-list-stats))
  (let [ht-param-ids (extract-ht-param-ids path-list-stats input-paths-by-id)
        _            (validate-ht-params (vals (select-keys input-params-by-id ht-param-ids)))
        ht-paths     (set (map :path path-list-stats))
        path-lists   (get-path-list-contents-map user ht-paths)
        paths-exist  (validate-path-lists user path-lists)
        path-maps    (map-slices path-lists)
        output-dir   (get-batch-output-dir user submission)
        batch-id     (save-batch user job-types app submission output-dir)
        submission   (preprocess-batch-submission submission output-dir batch-id)]
    (dorun (map-indexed (partial submit-job-in-batch apps-client user submission paths-exist) path-maps))
    (-> (job-listings/list-job apps-client batch-id)
        (assoc :missing-paths (extract-missing-paths paths-exist))
        remove-nil-values)))

(defn submit
  [apps-client user submission]
  (let [[job-types app]    (.getAppSubmissionInfo apps-client (:app_id submission))
        input-params-by-id (get-app-params app ap/param-ds-input-types)
        input-paths-by-id  (select-keys (:config submission) (keys input-params-by-id))]
    (if-let [path-list-stats (seq (load-path-list-stats user input-paths-by-id))]
      (submit-batch-job apps-client user input-params-by-id input-paths-by-id
                        path-list-stats job-types app submission)
      (.submitJob apps-client submission))))
