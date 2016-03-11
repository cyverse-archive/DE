(ns apps.service.apps.jobs.sharing
  (:use [clostache.parser :only [render]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [apps.clients.data-info :as data-info]
            [apps.clients.iplant-groups :as iplant-groups]
            [apps.clients.notifications :as cn]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.jobs.permissions :as job-permissions]
            [apps.util.service :as service]
            [clojure-commons.error-codes :as ce]))

(defn- get-job-name
  [job-id {job-name :job_name}]
  (or job-name (str "analysis ID " job-id)))

(def job-sharing-formats
  {:not-found    "analysis ID {{analysis-id}} does not exist"
   :load-failure "unable to load permissions for {{analysis-id}}: {{detail}}"
   :not-allowed  "insufficient privileges for analysis ID {{analysis-id}}"})

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

(defn- do-job-sharing-steps
  [apps-client sharer sharee job-id job level]
  (or (share-app-for-job apps-client sharer sharee job-id job)
      (share-output-folder sharer sharee job)
      (iplant-groups/share-analysis job-id sharee level)))

(defn- share-accessible-job
  [apps-client sharer sharee job-id job level]
  (if-let [failure-reason (do-job-sharing-steps apps-client sharer sharee job-id job level)]
    (job-sharing-failure job-id job level failure-reason)
    (job-sharing-success job-id job level)))

(defn- share-extant-job
  [apps-client sharer sharee job-id job level]
  (if (has-analysis-permission (:shortUsername sharer) job-id "own")
    (share-accessible-job apps-client sharer sharee job-id job level)
    (job-sharing-failure job-id job level (job-sharing-msg :not-allowed job-id))))

(defn- share-job
  [apps-client sharer sharee {job-id :analysis_id level :permission}]
  (if-let [job (jp/get-job-by-id job-id)]
    (try+
     (share-extant-job apps-client sharer sharee job-id job level)
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
