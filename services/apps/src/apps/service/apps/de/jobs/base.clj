(ns apps.service.apps.de.jobs.base
  (:require [apps.metadata.params :as mp]
            [apps.persistence.app-metadata :as ap]
            [apps.service.apps.de.jobs.common :as ca]
            [apps.service.apps.de.jobs.condor]
            [apps.service.apps.de.jobs.fapi]
            [apps.service.apps.de.jobs.protocol]
            [apps.service.apps.de.jobs.util :as util]))

(defn- build-job-request-formatter
  [user submission]
  (let [email    (:email user)
        app-id   (:app_id submission)
        app      (ap/get-app app-id)
        io-maps  (ca/load-io-maps app-id)
        params   (mp/load-app-params app-id)
        defaults (ca/build-default-values-map params)
        params   (group-by :step_id params)]
    (if (util/fapi-app? app)
      (apps.service.apps.de.jobs.fapi.JobRequestFormatter.
       user email submission app io-maps defaults params)
      (apps.service.apps.de.jobs.condor.JobRequestFormatter.
       user email submission app io-maps defaults params))))

(defn build-submission
  [user submission]
  (.buildSubmission (build-job-request-formatter user submission)))
