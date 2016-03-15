(ns apps.service.apps.job-listings
  (:use [kameleon.uuids :only [uuidify]]
        [apps.util.conversions :only [remove-nil-vals]])
  (:require [kameleon.db :as db]
            [apps.clients.iplant-groups :as iplant-groups]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.jobs.permissions :as job-permissions]
            [apps.service.util :as util]))

(defn is-completed?
  [job-status]
  (jp/completed-status-codes job-status))

(defn is-running?
  [job-status]
  (= jp/running-status job-status))

(def not-completed? (complement is-completed?))

(defn- job-timestamp
  [timestamp]
  (str (or (db/millis-from-timestamp timestamp) 0)))

(defn- app-disabled?
  [app-tables app-id]
  (let [disabled-flag (:disabled (first (remove nil? (map #(% app-id) app-tables))))]
    (if (nil? disabled-flag) true disabled-flag)))

(defn- batch-child-status
  [{:keys [status]}]
  (cond (is-completed? status) :completed
        (is-running? status)   :running
        :else                  :submitted))

(def ^:private empty-batch-child-status
  {:total     0
   :completed 0
   :running   0
   :submitted 0})

(defn- format-batch-status
  [batch-id]
  (merge empty-batch-child-status
         (let [children (jp/list-child-jobs batch-id)]
           (assoc (->> (group-by batch-child-status children)
                       (map (fn [[k v]] [k (count v)]))
                       (into {}))
                  :total (count children)))))

(defn format-job
  [apps-client app-tables {:keys [parent-id id] :as job}]
  (remove-nil-vals
   {:app_description (:app-description job)
    :app_id          (:app-id job)
    :app_name        (:app-name job)
    :description     (:description job)
    :enddate         (job-timestamp (:end-date job))
    :id              id
    :name            (:job-name job)
    :resultfolderid  (:result-folder-path job)
    :startdate       (job-timestamp (:start-date job))
    :status          (:status job)
    :username        (:username job)
    :deleted         (:deleted job)
    :notify          (:notify job false)
    :wiki_url        (:app-wiki-url job)
    :app_disabled    (app-disabled? app-tables (:app-id job))
    :parent_id       parent-id
    :batch           (:is-batch job)
    :batch_status    (when (:is-batch job) (format-batch-status id))
    :can_share       (and (nil? parent-id) (job-permissions/supports-job-sharing? apps-client id))}))

(defn- list-jobs*
  [{:keys [username]} search-params types analysis-ids]
  (jp/list-jobs-of-types username search-params types analysis-ids))

(defn- count-jobs
  [{:keys [username]} {:keys [filter include-hidden]} types analysis-ids]
  (jp/count-jobs-of-types username filter include-hidden types analysis-ids))

(defn list-jobs
  [apps-client user {:keys [sort-field] :as params}]
  (let [perms            (iplant-groups/load-analysis-permissions (:shortUsername user))
        analysis-ids     (set (keys perms))
        default-sort-dir (if (nil? sort-field) :desc :asc)
        search-params    (util/default-search-params params :startdate default-sort-dir)
        types            (.getJobTypes apps-client)
        jobs             (list-jobs* user search-params types analysis-ids)
        app-tables       (.loadAppTables apps-client (map :app-id jobs))]
    {:analyses  (map (partial format-job apps-client app-tables) jobs)
     :timestamp (str (System/currentTimeMillis))
     :total     (count-jobs user params types analysis-ids)}))

(defn list-job
  [apps-client job-id]
  (let [job-info   (jp/get-job-by-id job-id)
        app-tables (.loadAppTables apps-client [(:app-id job-info)])]
    (format-job apps-client app-tables job-info)))

(defn- format-job-step
  [step]
  (remove-nil-vals
   {:step_number     (:step-number step)
    :external_id     (:external-id step)
    :startdate       (job-timestamp (:start-date step))
    :enddate         (job-timestamp (:end-date step))
    :status          (:status step)
    :app_step_number (:app-step-number step)
    :step_type       (:job-type step)}))

(defn list-job-steps
  [job-id]
  (let [steps (jp/list-job-steps job-id)]
    {:analysis_id job-id
     :steps       (map format-job-step steps)
     :timestamp   (str (System/currentTimeMillis))
     :total       (count steps)}))
