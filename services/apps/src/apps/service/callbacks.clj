(ns apps.service.callbacks
  (:require [clojure.tools.logging :as log]
            [apps.persistence.jobs :as jp]
            [apps.service.apps :as apps]
            [apps.util.service :as service]))

(defn update-de-job-status
  [{{end-date :completion_date :keys [status uuid]} :state}]
  (service/assert-valid uuid "no job UUID provided")
  (service/assert-valid status "no status provided")
  (log/info (str "received a status update for DE job " uuid ": status = " status))
  (when-not (= status jp/submitted-status)
    (apps/update-job-status uuid status end-date)))

(defn update-agave-job-status
  [job-id {:keys [status external-id end-time]}]
  (service/assert-valid job-id "no job UUID provided")
  (service/assert-valid status "no status provided")
  (service/assert-valid external-id "no external job ID provided")
  (log/info (str "received a status update for Agave job " external-id ": status = " status))
  (apps/update-job-status job-id external-id status end-time))
