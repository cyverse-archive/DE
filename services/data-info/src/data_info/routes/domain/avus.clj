(ns data-info.routes.domain.avus
  (:use [common-swagger-api.schema :only [describe
                                          NonBlankString
                                          StandardUserQueryParams]]
        [data-info.routes.domain.common])
  (:require [schema.core :as s]))

(s/defschema AVUMap
  {:attr  (describe String "The attribute name")
   :value (describe String "The value associated with this attribute")
   :unit  (describe String "The unit associated with this value")})

(s/defschema AVUDeleteParams
  (merge StandardUserQueryParams
    (dissoc AVUMap :unit)))

(s/defschema AVUListing
  {:irods-avus (describe [AVUMap] "A list of AVUs on this object.")})

(s/defschema AVUGetResult
  (assoc AVUListing
   :path
   (describe NonBlankString "The iRODS path of the file whose AVUs are being listed.")))

(s/defschema AVUChangeResult
  {:path
   (describe NonBlankString "The iRODS path of the file whose AVUs changed.")
   :user
   (describe NonBlankString "The effective user who performed the request.")})

(s/defschema AVUSetResult
  (assoc AVUChangeResult
         :type
         (describe (s/enum :dir :file) "Whether this data object is a directory or file.")))
