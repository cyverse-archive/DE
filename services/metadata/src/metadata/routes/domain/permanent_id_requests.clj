(ns metadata.routes.domain.permanent-id-requests
  (:use [common-swagger-api.schema :only [describe
                                          PagingParams
                                          SortFieldDocs
                                          SortFieldOptionalKey
                                          StandardUserQueryParams]]
        [clojure-commons.error-codes]
        [metadata.routes.domain.common])
  (:require [metadata.persistence.permanent-id-requests :as db]
            [schema.core :as s])
  (:import [java.util UUID]))

(def PermanentIDRequestIdParam (describe UUID "The Permanent ID Requests's UUID"))
(def PermanentIDRequestTypeEnum (apply s/enum (map :type (db/list-permanent-id-request-types))))

(s/defschema PermanentIDRequest
  {:type (describe PermanentIDRequestTypeEnum "The type of persistent ID requested")
   :target_id (describe UUID "The UUID of the data item for which the persistent ID is being requested")
   :target_type DataTypeParam})

(s/defschema PermanentIDRequestBase
  (merge PermanentIDRequest
    {:id PermanentIDRequestIdParam
     :requested_by (describe String "The username of the user that submitted the Permanent ID Request")}))

(s/defschema PermanentIDRequestStatusUpdate
  {(s/optional-key :status) (describe String "The status code of the Permanent ID Request update")
   (s/optional-key :comments) (describe String "The curator comments of the Permanent ID Request status update")})

(s/defschema PermanentIDRequestStatus
  (merge PermanentIDRequestStatusUpdate
    {:status_date (describe Long "The timestamp of the Permanent ID Request status update")
     :updated_by (describe String "The username that updated the Permanent ID Request status")}))

(s/defschema PermanentIDRequestDetails
  (merge PermanentIDRequestBase
    {:history (describe [PermanentIDRequestStatus] "A list of Permanent ID Request status updates")}))

(s/defschema PermanentIDRequestListing
  (merge PermanentIDRequestBase
    {:date_submitted (describe Long "The timestamp of the Permanent ID Request submission")
     :status (describe String "The current status of the Permanent ID Request")
     :date_updated (describe Long "The timestamp of the last Permanent ID Request status update")
     :updated_by (describe String "The username of the user that last updated the Permanent ID Request status")}))

(s/defschema PermanentIDRequestList
  {:requests (describe [PermanentIDRequestListing] "A list of Permanent ID Requests")})

(def ValidPermanentIDRequestListSortFields
  (s/enum
    :type
    :target_type
    :requested_by
    :date_submitted
    :status
    :date_updated
    :updated_by))

(s/defschema PermanentIDRequestListPagingParams
  (-> PagingParams
      (assoc SortFieldOptionalKey (describe ValidPermanentIDRequestListSortFields SortFieldDocs))
      (merge StandardUserQueryParams)))

(s/defschema PermanentIDRequestStatusCode
  {:id (describe UUID "The Status Code's UUID")
   :name (describe String "The Status Code")
   :description (describe String "A brief description of the Status Code")})

(s/defschema PermanentIDRequestStatusCodeList
  {:status_codes (describe [PermanentIDRequestStatusCode] "A list of Permanent ID Request Status Codes")})

(s/defschema PermanentIDRequestType
  {:id (describe UUID "The Request Type's UUID")
   :type (describe String "The Request Type")
   :description (describe String "A brief description of the Request Type")})

(s/defschema PermanentIDRequestTypeList
  {:request_types (describe [PermanentIDRequestType] "A list of Permanent ID Request Types")})
