(ns mescal.agave-de-v2
  (:require [mescal.agave-de-v2.apps :as apps]
            [mescal.agave-de-v2.app-listings :as app-listings]
            [mescal.agave-de-v2.job-params :as params]
            [mescal.agave-de-v2.jobs :as jobs]
            [clojure.tools.logging :as log]))

(defn hpc-app-group
  []
  (app-listings/hpc-app-group))

(defn- get-system-statuses
  [agave]
  (into {} (map (juxt :id :status) (.listSystems agave))))

(defn list-apps
  ([agave jobs-enabled?]
     (app-listings/list-apps agave (get-system-statuses agave) jobs-enabled?))
  ([agave jobs-enabled? app-ids]
     (app-listings/list-apps agave (get-system-statuses agave) jobs-enabled? app-ids)))

(defn list-apps-with-ontology
  [agave jobs-enabled? term]
  (app-listings/list-apps-with-ontology agave (get-system-statuses agave) jobs-enabled? term))

(defn- app-matches?
  [search-term app]
  (some (fn [s] (re-find (re-pattern (str "(?i)\\Q" search-term)) s))
        ((juxt :name :description) app)))

(defn- find-matching-apps
  [agave jobs-enabled? search-term]
  (filter (partial app-matches? search-term)
          (:apps (list-apps agave jobs-enabled?))))

(defn search-apps
  [agave jobs-enabled? search-term]
  (let [matching-apps (find-matching-apps agave jobs-enabled? search-term)]
    {:app_count (count matching-apps)
     :apps      matching-apps}))

(defn get-app
  [agave app-id]
  (apps/format-app agave (.getApp agave app-id)))

(defn get-app-details
  [agave app-id]
  (apps/format-app-details agave (.getApp agave app-id)))

(defn list-app-tasks
  [agave app-id]
  (apps/format-app-tasks (.getApp agave app-id)))

(defn get-app-tool-listing
  [agave app-id]
  {:tools [(apps/format-tool-for-app (.getApp agave app-id))]})

(defn get-app-input-ids
  [agave app-id]
  (mapv :id (:inputs (.getApp agave app-id))))

(defn prepare-job-submission
  [agave submission]
  (jobs/prepare-submission agave (.getApp agave (:app_id submission)) submission))

(defn send-job-submission
  [agave submission]
  (let [app-info (apps/load-app-info agave [:appId submission])]
    (jobs/format-job-submisison-response agave submission (.submitJob agave submission))))

(defn- format-jobs
  [agave jobs-enabled? jobs]
  (let [app-info (apps/load-app-info agave (mapv :appId jobs))
        statuses (get-system-statuses agave)]
    (mapv (partial jobs/format-job agave jobs-enabled? statuses app-info) jobs)))

(defn list-jobs
  ([agave jobs-enabled?]
     (format-jobs agave jobs-enabled? (.listJobs agave)))
  ([agave jobs-enabled? job-ids]
     (format-jobs agave jobs-enabled? (.listJobs agave job-ids))))

(defn list-job
  [agave jobs-enabled? job-id]
  (let [job      (.listJob agave job-id)
        statuses (get-system-statuses agave)
        app-info (apps/load-app-info agave [(:appId job)])]
    (jobs/format-job agave jobs-enabled? statuses app-info job)))

(defn get-job-params
  [agave job-id]
  (when-let [job (.listJob agave job-id)]
    (params/format-params agave job (:appId job) (.getApp agave (:appId job)))))

(defn get-app-rerun-info
  [agave job-id]
  (when-let [job (.listJob agave job-id)]
    (apps/format-app-rerun-info agave (.getApp agave (:appId job)) job)))

(defn translate-job-status
  [status]
  (jobs/translate-job-status status))

(defn regenerate-job-submission
  [agave job-id]
  (when-let [job (.listJob agave job-id)]
    (jobs/regenerate-job-submission agave job)))

(defn get-default-output-name
  [agave app-id output-id]
  (some->> (.getApp agave app-id)
           (:outputs)
           (filter (comp (partial = output-id) :id))
           (first)
           (:value)
           (:default)))
