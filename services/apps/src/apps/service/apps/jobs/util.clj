(ns apps.service.apps.jobs.util
  (:require [apps.persistence.jobs :as jp]
            [apps.util.service :as service]))

(defn validate-job-existence
  [job-ids]
  (let [missing-ids (jp/list-non-existent-job-ids (set job-ids))]
    (when-not (empty? missing-ids)
      (service/not-found "jobs" job-ids))))
