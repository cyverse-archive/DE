(ns apps.service.apps.util
  (:require [clojure.string :as string]))

(defn supports-job-type?
  [apps-client job-type]
  (contains? (set (.getJobTypes apps-client)) job-type))

(defn get-app-name
  [app-names app-id]
  (let [app-name (app-names (str app-id))]
    (if (string/blank? app-name)
      (str "app ID " app-id)
      app-name)))
