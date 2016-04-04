(ns kameleon.jobs
  (:use [kameleon.entities]
        [korma.core :exclude [update]]
        [korma.db :only [transaction]]
        [kameleon.uuids :only [uuidify]]
        [kameleon.db :only [now-str]]
        [slingshot.slingshot :only [throw+]]))

(defn get-job-type-id
  "Fetches the primary key for the job type with the given name."
  [job-type]
  ((comp :id first) (select :job_types (where {:name job-type}))))

(defn- save-job-submission
  "Associated a job submission with a saved job in the database."
  [job-id submission]
  (exec-raw ["UPDATE jobs SET submission = CAST ( ? AS json ) WHERE id = ?"
             [(cast Object submission) job-id]]))

(defn- save-job*
  "Saves information about a job in the database."
  [job-info]
  (insert :jobs
    (values (select-keys job-info [:id
                                   :parent_id
                                   :job_name
                                   :job_description
                                   :app_id
                                   :app_name
                                   :app_description
                                   :app_wiki_url
                                   :result_folder_path
                                   :start_date
                                   :end_date
                                   :status
                                   :deleted
                                   :notify
                                   :user_id]))))

(defn save-job
  "Saves information about a job in the database."
  [job-info submission]
  (let [job-info (save-job* job-info)]
    (save-job-submission (:id job-info) submission)
    job-info))

(defn save-job-step
  "Saves a single job step in the database."
  [job-step]
  (insert :job_steps
    (values (select-keys job-step [:job_id
                                   :step_number
                                   :external_id
                                   :start_date
                                   :end_date
                                   :status
                                   :job_type_id
                                   :app_step_number]))))

(defn job-updates
  "Returns a list of all of the job status updates received for a job id"
  [job-id]
  (select job-status-updates
          (where {:external_id [in (subselect :job_steps
                                           (fields :external_id)
                                           (where {:job_id (uuidify job-id)})
                                           (modifier "DISTINCT"))]})))

(defn job-step-updates
  "Returns a list of all of the job update received for the job step"
  [external-id]
  (select job-status-updates
          (where {:external_id external-id})
          (order :sent_on :DESC)))

(defn- update->date-completed
  [update]
  (let [status (clojure.string/lower-case (:status update))]
    (cond
      (= status "submitted") ""
      (= status "running")   ""
      (= status "completed") (str (:sent_on update))
      (= status "failed")    (str (:sent_on update))
      :else                  (str (:sent_on update)))))

(defn get-job-state
  "Returns a map in the following format:
     {:status \"state\"
      :enddate \"enddate\"}"
  [external-id]
  (let [state (first (job-step-updates external-id))]
    (if state
      {:status  (:state update)
       :enddate (update->date-completed update)}
      {:status "Failed"
       :enddate (now-str)})))
