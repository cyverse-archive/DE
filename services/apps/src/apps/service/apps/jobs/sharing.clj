(ns apps.service.apps.jobs.sharing
  (:use [clostache.parser :only [render]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [apps.clients.data-info :as data-info]
            [apps.clients.iplant-groups :as iplant-groups]
            [apps.clients.notifications :as cn]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.jobs.params :as job-params]
            [apps.service.apps.jobs.permissions :as job-permissions]
            [apps.util.service :as service]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure-commons.error-codes :as ce]))

(defn- get-job-name
  [job-id {job-name :job_name}]
  (or job-name (str "analysis ID " job-id)))

(def job-sharing-formats
  {:not-found     "analysis ID {{analysis-id}} does not exist"
   :load-failure  "unable to load permissions for {{analysis-id}}: {{detail}}"
   :not-allowed   "insufficient privileges for analysis ID {{analysis-id}}"
   :is-subjob     "analysis sharing not supported for individual jobs within an HT batch"
   :not-supported "analysis sharing is not supported for jobs of this type"})

(defn- job-sharing-success
  [job-id job level]
  {:analysis_id   job-id
   :analysis_name (get-job-name job-id job)
   :permission    level
   :success       true})

(defn- job-sharing-failure
  [job-id job level reason]
  {:analysis_id   job-id
   :analysis_name (get-job-name job-id job)
   :permission    level
   :success       false
   :error         {:error_code ce/ERR_BAD_REQUEST
                   :reason     reason}})

(defn- job-unsharing-success
  [job-id job]
  {:analysis_id   job-id
   :analysis_name (get-job-name job-id job)
   :success       true})

(defn- job-unsharing-failure
  [job-id job reason]
  {:analysis_id   job-id
   :analysis_name (get-job-name job-id job)
   :success       false
   :error         {:error_code ce/ERR_BAD_REQUEST
                   :reason     reason}})

(defn- job-sharing-msg
  ([reason-code job-id]
   (job-sharing-msg reason-code job-id nil))
  ([reason-code job-id detail]
   (render (job-sharing-formats reason-code)
           {:analysis-id job-id
            :detail (or detail "unexpected error")})))

(defn- load-analysis-permissions
  [user analysis-id]
  (try+
   (iplant-groups/load-analysis-permissions user [analysis-id])
   (catch ce/clj-http-error? {:keys [body]}
     (throw+ {:type   ::permission-load-failure
              :reason (:grouper_result_message (service/parse-json body))}))))

(defn- has-analysis-permission
  [user job-id required-level]
  (-> (iplant-groups/load-analysis-permissions user [job-id])
      (iplant-groups/has-permission-level required-level job-id)))

(defn- verify-accessible
  [sharer job-id]
  (when-not (has-analysis-permission (:shortUsername sharer) job-id "own")
    (job-sharing-msg :not-allowed job-id)))

(defn- verify-not-subjob
  [{:keys [id parent-id]}]
  (when parent-id
    (job-sharing-msg :is-subjob id)))

(defn- verify-support
  [apps-client job-id]
  (when-not (job-permissions/supports-job-sharing? apps-client job-id)
    (job-sharing-msg :not-supported job-id)))

(defn- share-app-for-job
  [apps-client sharer sharee job-id {:keys [app-id]}]
  (when-not (.hasAppPermission apps-client sharee app-id "read")
    (let [response (.shareAppWithUser apps-client {} sharee app-id "read")]
      (when-not (:success response)
        (get-in response [:error :reason] "unable to share app")))))

(defn- share-output-folder
  [sharer sharee {:keys [result-folder-path]}]
  (try+
   (data-info/share-path sharer result-folder-path sharee "read")
   nil
   (catch ce/clj-http-error? {:keys [body]}
     (str "unable to share result folder: " (:error_code (service/parse-json body))))))

(defn- share-input-file
  [sharer sharee path]
  (try+
   (data-info/share-path sharer path sharee "read")
   nil
   (catch ce/clj-http-error? {:keys [body]}
     (str "unable to share input file, " path ": " (:error_code (service/parse-json body))))))

(defn- process-child-jobs
  [f job-id]
  (first (remove nil? (map f (jp/list-child-jobs job-id)))))

(defn- list-job-inputs
  [apps-client job]
  (->> (mapv keyword (.getAppInputIds apps-client (:app-id job)))
       (select-keys (job-params/get-job-config job))
       vals
       (remove string/blank?)))

(defn- process-job-inputs
  [f apps-client job]
  (first (remove nil? (map f (list-job-inputs apps-client job)))))

(defn- share-child-job
  [apps-client sharer sharee level job]
  (or (process-job-inputs (partial share-input-file sharer sharee) apps-client job)
      (iplant-groups/share-analysis (:id job) sharee level)))

(defn- share-job*
  [apps-client sharer sharee job-id job level]
  (or (verify-not-subjob job)
      (verify-accessible sharer job-id)
      (verify-support apps-client job-id)
      (share-app-for-job apps-client sharer sharee job-id job)
      (share-output-folder sharer sharee job)
      (iplant-groups/share-analysis job-id sharee level)
      (process-job-inputs (partial share-input-file sharer sharee) apps-client job)
      (process-child-jobs (partial share-child-job apps-client sharer sharee level) job-id)))

(defn- share-job
  [apps-client sharer sharee {job-id :analysis_id level :permission}]
  (if-let [job (jp/get-job-by-id job-id)]
    (try+
     (if-let [failure-reason (share-job* apps-client sharer sharee job-id job level)]
       (job-sharing-failure job-id job level failure-reason)
       (job-sharing-success job-id job level))
     (catch [:type ::permission-load-failure] {:keys [reason]}
       (job-sharing-failure job-id job level (job-sharing-msg :load-failure job-id reason))))
    (job-sharing-failure job-id nil level (job-sharing-msg :not-found job-id))))

(defn- share-jobs-with-user
  [apps-client sharer {sharee :user :keys [analyses]}]
  (let [responses (mapv (partial share-job apps-client sharer sharee) analyses)]
    (cn/send-analysis-sharing-notifications (:shortUsername sharer) sharee responses)
    {:user     sharee
     :analyses responses}))

(defn share-jobs
  [apps-client user sharing-requests]
  (mapv (partial share-jobs-with-user apps-client user) sharing-requests))

(defn- unshare-output-folder
  [sharer sharee {:keys [result-folder-path]}]
  (try+
   (data-info/unshare-path sharer result-folder-path sharee)
   nil
   (catch ce/clj-http-error? {:keys [body]}
     (str "unable to unshare result folder: " (:error_code (service/parse-json body))))))

(defn- unshare-input-file
  [sharer sharee path]
  (try+
   (data-info/unshare-path sharer path sharee)
   nil
   (catch ce/clj-http-error? {:keys [body]}
     (str "unable to unshare input file: " (:error_code (service/parse-json body))))))

(defn- unshare-child-job
  [apps-client sharer sharee job]
  (or (process-job-inputs (partial unshare-input-file sharer sharee) apps-client job)
      (iplant-groups/unshare-analysis (:id job) sharee)))

(defn- unshare-job*
  [apps-client sharer sharee job-id job]
  (or (verify-not-subjob job)
      (verify-accessible sharer job-id)
      (verify-support apps-client job-id)
      (unshare-output-folder sharer sharee job)
      (process-job-inputs (partial unshare-input-file sharer sharee) apps-client job)
      (iplant-groups/unshare-analysis job-id sharee)
      (process-child-jobs (partial unshare-child-job apps-client sharer sharee) job-id)))

(defn- unshare-job
  [apps-client sharer sharee job-id]
  (if-let [job (jp/get-job-by-id job-id)]
    (try+
     (if-let [failure-reason (unshare-job* apps-client sharer sharee job-id job)]
       (job-unsharing-failure job-id job failure-reason)
       (job-unsharing-success job-id job))
     (catch [:type ::permission-load-failure] {:keys [reason]}
       (job-unsharing-failure job-id job (job-sharing-msg :load-failure job-id reason))))
    (job-unsharing-failure job-id nil (job-sharing-msg :not-found job-id))))

(defn- unshare-jobs-with-user
  [apps-client sharer {sharee :user :keys [analyses]}]
  (let [responses (mapv (partial unshare-job apps-client sharer sharee) analyses)]
    (cn/send-analysis-unsharing-notifications (:shortUsername sharer) sharee responses)
    {:user     sharee
     :analyses responses}))

(defn unshare-jobs
  [apps-client user unsharing-requests]
  (mapv (partial unshare-jobs-with-user apps-client user) unsharing-requests))
