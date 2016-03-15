(ns apps.routes.analyses
  (:use [common-swagger-api.schema]
        [apps.routes.domain.analysis]
        [apps.routes.domain.analysis.listing]
        [apps.routes.domain.app]
        [apps.routes.params]
        [apps.user :only [current-user]]
        [apps.util.coercions :only [coerce!]]
        [ring.util.http-response :only [ok]])
  (:require [apps.json :as json]
            [apps.service.apps :as apps]
            [apps.routes.domain.permission :as perms]
            [apps.util.coercions :as coercions]))

(defroutes* analyses
  (GET* "/" []
        :query   [{:keys [filter] :as params} SecuredAnalysisListingParams]
        :return  AnalysisList
        :summary "List Analyses"
        :description "This service allows users to list analyses that they've previously submitted
        for execution."
        ;; JSON query params are not currently supported by compojure-api,
        ;; so we have to decode the String filter param and validate it here.
        (ok (coerce! AnalysisList
                 (apps/list-jobs current-user
                   (coercions/coerce!
                     (assoc SecuredAnalysisListingParams OptionalKeyFilter [FilterParams])
                     (assoc params :filter (json/from-json filter)))))))

  (POST* "/" []
         :query   [params SecuredQueryParamsEmailRequired]
         :body    [body AnalysisSubmission]
         :return  AnalysisResponse
         :summary "Submit an Analysis"
         :description   "This service allows users to submit analyses for execution. The `config`
         element in the analysis submission is a map from parameter IDs as they appear in
         the response from the `/apps/:app-id` endpoint to the desired values for those
         parameters."
         (ok (coerce! AnalysisResponse
                  (apps/submit-job current-user body))))

  (POST* "/permission-lister" []
         :query [params SecuredQueryParams]
         :body [body (describe perms/AnalysisIdList "The analysis permission listing request.")]
         :return perms/AnalysisPermissionListing
         :summary "List App Permissions"
         :description "This endpoint allows the caller to list the permissions for one or more analyses.
         The authenticated user must have read permission on every analysis in the request body for this
         endpoint to succeed."
         (ok (apps/list-job-permissions current-user (:analyses body))))

  (POST* "/sharing" []
         :query [params SecuredQueryParams]
         :body [body (describe perms/AnalysisSharingRequest "The analysis sharing request.")]
         :return perms/AnalysisSharingResponse
         :summary "Add Analysis Permissions"
         :description "This endpoint allows the caller to share multiple analyses with multiple users. The
         authenticated user must have ownership permission to every analysis in the request body for this
         endpoint to fully succeed. Note: this is a potentially slow operation and the response is returned
         synchronously. The DE UI handles this by allowing the user to continue working while the request is
         being processed. When calling this endpoint, please be sure that the response timeout is long
         enough. Using a response timeout that is too short will result in an exception on the client side.
         On the server side, the result of the sharing operation when a connection is lost is undefined. It
         may be worthwhile to repeat failed or timed out calls to this endpoint."
         (ok (apps/share-jobs current-user (:sharing body))))

  (POST* "/unsharing" []
         :query [params SecuredQueryParams]
         :body [body (describe perms/AnalysisUnsharingRequest "The analysis unsharing request.")]
         :return perms/AnalysisUnsharingResponse
         :summary "Revoke Analysis Permissions"
         :description "This endpoint allows the caller to revoke permission to access one or more analyses from
         one or more users. The authenticate user must have ownership permission to every analysis in the request
         body for this endoint to fully succeed. Note: like analysis sharing, this is a potentially slow
         operation."
         (ok (apps/unshare-jobs current-user (:unsharing body))))

  (PATCH* "/:analysis-id" []
          :path-params [analysis-id :- AnalysisIdPathParam]
          :query       [params SecuredQueryParams]
          :body        [body AnalysisUpdate]
          :return      AnalysisUpdateResponse
          :summary     "Update an Analysis"
          :description       "This service allows an analysis name or description to be updated."
          (ok (coerce! AnalysisUpdateResponse
                   (apps/update-job current-user analysis-id body))))

  (DELETE* "/:analysis-id" []
           :path-params [analysis-id :- AnalysisIdPathParam]
           :query       [params SecuredQueryParams]
           :summary     "Delete an Analysis"
           :description       "This service marks an analysis as deleted in the DE database."
           (ok (apps/delete-job current-user analysis-id)))

  (POST* "/shredder" []
         :query   [params SecuredQueryParams]
         :body    [body AnalysisShredderRequest]
         :summary "Delete Multiple Analyses"
         :description   "This service allows the caller to mark one or more analyses as deleted
         in the apps database."
         (ok (apps/delete-jobs current-user body)))

  (GET* "/:analysis-id/parameters" []
        :path-params [analysis-id :- AnalysisIdPathParam]
        :query       [params SecuredQueryParams]
        :return      AnalysisParameters
        :summary     "Display the parameters used in an analysis."
        :description       "This service returns a list of parameter values used in a previously
        executed analysis."
        (ok (coerce! AnalysisParameters
                 (apps/get-parameter-values current-user analysis-id))))

  (GET* "/:analysis-id/relaunch-info" []
        :path-params [analysis-id :- AnalysisIdPathParam]
        :query       [params SecuredQueryParams]
        :return      AppJobView
        :summary     "Obtain information to relaunch analysis."
        :description       "This service allows the Discovery Environment user interface to obtain an
        app description that can be used to relaunch a previously submitted job, possibly with
        modified parameter values."
        (ok (coerce! AppJobView
                 (apps/get-job-relaunch-info current-user analysis-id))))

  (POST* "/:analysis-id/stop" []
         :path-params [analysis-id :- AnalysisIdPathParam]
         :query       [params SecuredQueryParams]
         :return      StopAnalysisResponse
         :summary     "Stop a running analysis."
         :description       "This service allows DE users to stop running analyses."
         (ok (coerce! StopAnalysisResponse
                  (apps/stop-job current-user analysis-id))))

  (GET* "/:analysis-id/steps" []
        :path-params [analysis-id :- AnalysisIdPathParam]
        :query       [params SecuredQueryParams]
        :return      AnalysisStepList
        :summary     "Display the steps of an analysis."
        :description "This service returns a list of steps in an analysis."
        (ok (apps/list-job-steps current-user analysis-id))))
