(ns apps.service.apps.agave
  (:use [kameleon.uuids :only [uuidify]])
  (:require [clojure.string :as string]
            [apps.persistence.jobs :as jp]
            [apps.service.apps.agave.listings :as listings]
            [apps.service.apps.agave.pipelines :as pipelines]
            [apps.service.apps.agave.jobs :as agave-jobs]
            [apps.service.apps.job-listings :as job-listings]
            [apps.service.apps.permissions :as app-permissions]
            [apps.service.apps.util :as apps-util]
            [apps.service.util :as util]
            [apps.util.service :as service]))

(defn- reject-app-documentation-edit-request
  []
  (service/bad-request "Cannot edit documentation for HPC apps with this service"))

(def app-permission-rejection "Cannot list or modify the permissions of HPC apps with this service")

(def analysis-permission-rejection "Cannot list or modify the permissions of HPC analyses with this service")

(defn- reject-app-permission-request
  []
  (service/bad-request app-permission-rejection))

(deftype AgaveApps [agave user-has-access-token? user]
  apps.protocols.Apps

  (getUser [_]
    user)

  (getClientName [_]
    jp/agave-client-name)

  (getJobTypes [_]
    [jp/agave-job-type])

  (listAppCategories [_ {:keys [hpc]}]
    (when-not (and hpc (.equalsIgnoreCase hpc "false"))
      [(.hpcAppGroup agave)]))

  (hasCategory [_ category-id]
    (= category-id (uuidify (:id (.hpcAppGroup agave)))))

  (listAppsInCategory [_ category-id params]
    (when (= category-id (uuidify (:id (.hpcAppGroup agave))))
      (listings/list-apps agave category-id params)))

  (searchApps [_ search-term params]
    (when (user-has-access-token?)
      (listings/search-apps agave search-term params)))

  (canEditApps [_]
    false)

  (listAppIds [_]
    nil)

  (getAppJobView [_ app-id]
    (when-not (util/uuid? app-id)
      (.getApp agave app-id)))

  (getAppDetails [_ app-id]
    (when-not (util/uuid? app-id)
      (.getAppDetails agave app-id)))

  (isAppPublishable [_ app-id]
    (when-not (util/uuid? app-id)
      false))

  (getAppTaskListing [_ app-id]
    (when-not (util/uuid? app-id)
      (.listAppTasks agave app-id)))

  (getAppToolListing [_ app-id]
    (when-not (util/uuid? app-id)
      (.getAppToolListing agave app-id)))

  (getAppInputIds [_ app-id]
    (when-not (util/uuid? app-id)
      (.getAppInputIds agave app-id)))

  (formatPipelineTasks [_ pipeline]
    (pipelines/format-pipeline-tasks agave pipeline))

  (listJobs [self params]
    (job-listings/list-jobs self user params))

  (loadAppTables [_ app-ids]
    (let [agave-app-ids (remove util/uuid? app-ids)]
      (if (and (seq agave-app-ids) (user-has-access-token?))
        (listings/load-app-tables agave agave-app-ids)
        [])))

  (submitJob [this submission]
    (when-not (util/uuid? (:app_id submission))
      (agave-jobs/submit agave user submission)))

  (submitJobStep [_ job-id submission]
    (agave-jobs/submit-step agave job-id submission))

  (translateJobStatus [self job-type status]
    (when (apps-util/supports-job-type? self job-type)
      (or (.translateJobStatus agave status) status)))

  (updateJobStatus [self job-step job status end-date]
    (when (apps-util/supports-job-type? self (:job-type job-step))
      (agave-jobs/update-job-status agave job-step job status end-date)))

  (getDefaultOutputName [_ io-map source-step]
    (agave-jobs/get-default-output-name agave io-map source-step))

  (getJobStepStatus [_ job-step]
    (agave-jobs/get-job-step-status agave job-step))

  (prepareStepSubmission [_ job-id submission]
    (agave-jobs/prepare-step-submission agave job-id submission))

  (getParamDefinitions [_ app-id]
    (listings/get-param-definitions agave app-id))

  (stopJobStep [self {:keys [job-type external-id]}]
    (when (and (apps-util/supports-job-type? self job-type)
               (not (string/blank? external-id)))
      (.stopJob agave external-id)))

  (getAppDocs [_ app-id]
    (when-not (util/uuid? app-id)
      {:app_id        app-id
       :documentation ""
       :references    []}))

  (ownerEditAppDocs [_ app-id _]
    (when-not (util/uuid? app-id)
      (reject-app-documentation-edit-request)))

  (ownerAddAppDocs [_ app-id _]
    (when-not (util/uuid? app-id)
      (reject-app-documentation-edit-request)))

  (adminEditAppDocs [_ app-id _]
    (when-not (util/uuid? app-id)
      (reject-app-documentation-edit-request)))

  (adminAddAppDocs [_ app-id _]
    (when-not (util/uuid? app-id)
      (reject-app-documentation-edit-request)))

  (listAppPermissions [_ app-ids]
    (when (and (user-has-access-token?)
               (some (complement util/uuid?) app-ids))
      (reject-app-permission-request)))

  (shareApps [self sharing-requests]
    (app-permissions/process-app-sharing-requests self sharing-requests))

  (shareAppsWithUser [self app-names sharee user-app-sharing-requests]
    (app-permissions/process-user-app-sharing-requests self app-names sharee user-app-sharing-requests))

  (shareAppWithUser [_ app-names _ app-id level]
    (when (and (user-has-access-token?)
               (not (util/uuid? app-id)))
      (let [category (.hpcAppGroup agave)]
        (app-permissions/app-sharing-failure app-names app-id level category category app-permission-rejection))))

  (unshareApps [self unsharing-requests]
    (app-permissions/process-app-unsharing-requests self unsharing-requests))

  (unshareAppsWithUser [self app-names sharee app-ids]
    (app-permissions/process-user-app-unsharing-requests self app-names sharee app-ids))

  (unshareAppWithUser [_ app-names _ app-id]
    (when (and (user-has-access-token?)
               (not (util/uuid? app-id)))
      (let [category (.hpcAppGroup agave)]
        (app-permissions/app-unsharing-failure app-names app-id category app-permission-rejection))))

  (hasAppPermission [_ username app-id required-level]
    (when (and (user-has-access-token?)
               (not (util/uuid? app-id)))
      false))

  (supportsJobSharing [_ _]
    false))
