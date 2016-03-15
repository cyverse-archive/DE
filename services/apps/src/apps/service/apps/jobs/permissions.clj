(ns apps.service.apps.jobs.permissions
  (:use [korma.db :only [transaction]])
  (:require [apps.clients.iplant-groups :as iplant-groups]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.jobs.util :as ju]
            [clojure-commons.exception-util :as cxu]))

(defn supports-job-sharing?
  [apps-client job-id]
  (every? #(.supportsJobSharing apps-client %) (jp/list-representative-job-steps job-id)))

(defn- validate-job-permission-level
  [short-username perms required-level job-ids]
  (doseq [job-id job-ids]
    (let [user-perms (filter (comp (partial = short-username) :id :subject) (perms job-id))]
      (when (iplant-groups/lacks-permission-level {job-id user-perms} required-level job-id)
        (cxu/forbidden (str "insufficient privileges for analysis " job-id))))))

(defn- validate-job-sharing-support
  [apps-client job-ids]
  (doseq [job-id job-ids]
    (when-not (supports-job-sharing? apps-client job-id)
      (cxu/bad-request (str "analysis sharing not supported for " job-id)))))

(defn- verify-not-subjobs
  [jobs]
  (when-let [subjob-ids (seq (map :id (filter :parent-id jobs)))]
    (cxu/bad-request (str "analysis sharing not supported for members of a batch job") :jobs subjob-ids)))

(defn- validate-jobs-for-permissions
  [apps-client {short-username :shortUsername} perms required-level job-ids]
  (ju/validate-job-existence job-ids)
  (validate-job-permission-level short-username perms required-level job-ids)
  (validate-job-sharing-support apps-client job-ids))

(defn- format-job-permission
  [short-username perms {:keys [id job-name]}]
  {:id          id
   :name        job-name
   :permissions (mapv iplant-groups/format-permission
                      (remove (comp (partial = short-username) key)
                              (group-by (comp :id :subject) (perms id))))})

(defn- format-job-permission-listing
  [{short-username :shortUsername} perms jobs]
  {:analyses (mapv (partial format-job-permission short-username perms) jobs)})

(defn list-job-permissions
  [apps-client {:keys [username] :as user} job-ids]
  (ju/validate-job-existence job-ids)
  (transaction
   (let [jobs (jp/list-jobs-by-id job-ids)]
     (verify-not-subjobs jobs)
     (let [perms (iplant-groups/list-analysis-permissions job-ids)]
       (validate-jobs-for-permissions apps-client user perms "read" job-ids)
       (format-job-permission-listing user perms (jp/list-jobs-by-id job-ids))))))
