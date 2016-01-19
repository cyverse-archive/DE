(ns apps.tasks
  (:require [apps.service.apps :as apps]
            [apps.util.config :as config])
  (:import [java.util.concurrent ScheduledThreadPoolExecutor TimeUnit]))

(def ^:private thread-pool-size 10)
(def ^:private executor (ScheduledThreadPoolExecutor. thread-pool-size))

(defn set-logging-context!
  "Sets the logging ThreadContext for the threads in the task thread pool."
  [cm]
  (apps/set-logging-context! cm))
