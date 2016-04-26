(ns notification-agent.scheduler
  (:use [slingshot.slingshot :only [try+]])
  (:require [clojure.tools.logging :as log]
            [clojurewerkz.quartzite.conversion :as qc]
            [clojurewerkz.quartzite.jobs :as j :refer [defjob]]
            [clojurewerkz.quartzite.schedule.simple :as ss]
            [clojurewerkz.quartzite.scheduler :as qs]
            [clojurewerkz.quartzite.triggers :as t]
            [notification-agent.amqp :as amqp]
            [notification-agent.db :as db]
            [notification-agent.time :as nt]))

(def ^:private scheduler (ref nil))

(defjob PostSystemMsg
  [ctx]
  (if-let [uuid (get (qc/from-job-data ctx) "uuid")]
    (if-let [msg (db/get-system-notification-by-uuid uuid)]
      (amqp/publish-system-msg msg)
      (log/error "system msg," uuid ", not found"))
    (log/error "no system message ID provided to scheduled job")))

(defn schedule-system-message
  "Schedule the system message for delivery. Note that we use the `ignore-misfires` option in the trigger.
   Counterintuitively, this causes the scheduler to fire the trigger as soon as it can. For more information,
   see http://clojurequartz.info/articles/triggers.html#the_ignore_misfires_instruction"
  [{uuid :uuid activation-date :activation_date}]
  (let [uuid (str uuid)]
    (qs/schedule @scheduler
                 (j/build (j/of-type PostSystemMsg)
                          (j/with-identity (j/key uuid "system-msg"))
                          (j/using-job-data {"uuid" uuid}))
                 (t/build (t/with-identity (t/key uuid "system-msg"))
                          (t/start-at (nt/timestamp->datetime activation-date))
                          (t/with-schedule (ss/schedule (ss/ignore-misfires)))))))

(defn reschedule-system-message
  "Reschedules a system message. This involves removing any triggers for the message and scheduling a new
   trigger for the message, provided that the message hasn't been deactivated."
  [{uuid :uuid deactivation-date :deactivation_date :as msg}]
  (qs/delete-trigger @scheduler (t/key (str uuid) "system-msg"))
  (when-not (nt/past? (nt/timestamp->datetime deactivation-date))
    (schedule-system-message msg)))

(defn init
  []
  (dosync (ref-set scheduler (qs/start (qs/initialize))))
  (dorun (map schedule-system-message (db/list-future-system-notifications))))
