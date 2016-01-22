(ns metadata.routes.permanent-id-requests
  (:use [common-swagger-api.schema]
        [metadata.routes.domain.common]
        [metadata.routes.domain.permanent-id-requests]
        [metadata.services.permanent-id-requests]
        [ring.util.http-response :only [ok]]))

(defroutes* permanent-id-request-routes
  (context* "/permanent-id-requests" []
    :tags ["permanent-id-requests"]

    (GET* "/" []
      :query [params PermanentIDRequestListPagingParams]
      :return PermanentIDRequestList
      :summary "List Permanent ID Requests"
      :description "Lists all Permanent ID Requests submitted by the requesting user."
      (ok (list-permanent-id-requests params)))

    (POST* "/" []
      :query [{:keys [user]} StandardUserQueryParams]
      :body [body PermanentIDRequest]
      :return PermanentIDRequestDetails
      :summary "Create a Permanent ID Request"
      :description "Creates a Permanent ID Request for the requesting user."
      (ok (create-permanent-id-request user body)))

    (GET* "/status-codes" []
      :query [params StandardUserQueryParams]
      :return PermanentIDRequestStatusCodeList
      :summary "List Permanent ID Request Status Codes"
      :description
"Lists all Permanent ID Request Status Codes that have been assigned to a request status update.
 This allows a status to easily be reused by admins in future status updates."
      (ok (list-permanent-id-request-status-codes params)))

    (GET* "/types" []
      :query [params StandardUserQueryParams]
      :return PermanentIDRequestTypeList
      :summary "List Permanent ID Request Types"
      :description
      "Lists the allowed Permanent ID Request Types the user can select when submitting a new request."
      (ok (list-permanent-id-request-types params)))

    (GET* "/:request-id" []
      :path-params [request-id :- PermanentIDRequestIdParam]
      :query [{:keys [user]} StandardUserQueryParams]
      :return PermanentIDRequestDetails
      :summary "List Permanent ID Request Details"
      :description "Allows a user to retrieve details for one of their Permanent ID Request submissions."
      (ok (get-permanent-id-request user request-id)))))

(defroutes* admin-permanent-id-request-routes
  (context* "/admin/permanent-id-requests" []
    :tags ["admin-permanent-id-requests"]

    (GET* "/" []
      :query [params PermanentIDRequestListPagingParams]
      :return PermanentIDRequestList
      :summary "List Permanent ID Requests"
      :description "Allows administrators to list Permanent ID Requests from all users."
      (ok (admin-list-permanent-id-requests params)))

    (GET* "/:request-id" []
      :path-params [request-id :- PermanentIDRequestIdParam]
      :query [{:keys [user]} StandardUserQueryParams]
      :return PermanentIDRequestDetails
      :summary "Get Permanent ID Request Details"
      :description "Allows administrators to retrieve details for a Permanent ID Request from any user."
      (ok (admin-get-permanent-id-request user request-id)))

    (POST* "/:request-id/status" []
      :path-params [request-id :- PermanentIDRequestIdParam]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [body PermanentIDRequestStatusUpdate]
      :return PermanentIDRequestDetails
      :summary "Update the Status of a Permanent ID Request"
      :description
"Allows administrators to update the status of a Permanent ID Request from any user.

`Note`: The status code is case-sensitive, and if it isn't defined in the database already then it will
 be added to the list of known status codes."
      (ok (update-permanent-id-request request-id user body)))))
