(ns data-info.routes.domain.avus
  (:use [common-swagger-api.schema :only [->optional-param
                                          describe
                                          NonBlankString
                                          StandardUserQueryParams]]
        [data-info.routes.domain.common])
  (:require [schema.core :as s])
  (:import [java.util UUID]))

(s/defschema AVUMap
  {:attr  (describe String "The attribute name")
   :value (describe String "The value associated with this attribute")
   :unit  (describe String "The unit associated with this value")})

(s/defschema AVUListing
  {:irods-avus
   (describe [AVUMap] "A list of AVUs on this object")})

(s/defschema MetadataServiceJSON
  {s/Keyword s/Any})

(s/defschema MetadataListing
  (merge AVUListing
         {(s/optional-key :metadata)
          (describe MetadataServiceJSON "A metadata service request/response object")}))

(s/defschema AVUGetResult
  (assoc MetadataListing
    :path (describe NonBlankString "The iRODS path of the file whose AVUs are being listed.")))

(s/defschema AddMetadataRequest
  (->optional-param MetadataListing :irods-avus))

(s/defschema AVUChangeResult
  {:path
   (describe NonBlankString "The iRODS path of the file whose AVUs changed.")
   :user
   (describe NonBlankString "The effective user who performed the request.")})

(s/defschema MetadataCopyRequestParams
  (merge StandardUserQueryParams
         {(s/optional-key :force)
          (describe Boolean
                    "Omitting this parameter, or setting its value to anything other than `true`,
                     will cause this endpoint to validate that none of the given `destination_ids`
                     already have Metadata Template AVUs set with any of the attributes found in any of
                     the Metadata AVUs associated with the source `data-id`,
                     otherwise an `ERR_NOT_UNIQUE` error is returned")}))

(s/defschema MetadataCopyRequest
  {:destination_ids (describe [UUID] "The IDs of the target data items")})

(s/defschema MetadataCopyResult
  (merge StandardUserQueryParams
         {:src   (describe String "The metadata source item's path")
          :paths (describe [String] "The list of paths to which metadata was copied")}))
