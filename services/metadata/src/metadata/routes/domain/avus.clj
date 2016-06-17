(ns metadata.routes.domain.avus
  (:use [common-swagger-api.schema :only [->optional-param
                                          describe
                                          NonBlankString
                                          StandardUserQueryParams]]
        [metadata.routes.domain.common])
  (:require [schema.core :as s])
  (:import [java.util UUID]))

(def AvuIdPathParam (describe UUID "The AVU's UUID"))
(def AvuIdParam AvuIdPathParam)

(s/defschema FilterByAvuParams
  (merge StandardUserQueryParams
         {:attr  (describe NonBlankString "The Attribute's name")
          :value (describe NonBlankString "The Attribute's value")}))

(s/defschema Avu
  {:id AvuIdParam
   :attr (describe String "The Attribute's name")
   :value (describe String "The Attribute's value")
   :unit (describe String "The Attribute's unit")
   :target_id TargetIdPathParam
   :created_by (describe String "The ID of the user who created the AVU")
   :modified_by (describe String "The ID of the user who last modified the AVU")
   :created_on (describe Long "The date the AVU was created in ms since the POSIX epoch")
   :modified_on (describe Long "The date the AVU was last modified in ms since the POSIX epoch")
   (s/optional-key :avus) (describe [(s/recursive #'Avu)] "AVUs attached to this AVU")})

(s/defschema AvuList
  {:avus (describe [Avu] "The list of AVUs associated with the target")})

(s/defschema AvuRequest
  (-> Avu
      (->optional-param :id)
      (->optional-param :target_id)
      (->optional-param :created_by)
      (->optional-param :modified_by)
      (->optional-param :created_on)
      (->optional-param :modified_on)
      (assoc (s/optional-key :avus)
             (describe [(s/recursive #'AvuRequest)] "AVUs attached to this AVU"))))

(s/defschema AvuListRequest
  {:avus
   (describe [AvuRequest] "The AVUs to save for the target data item")})

(s/defschema SetAvuRequest
  (->optional-param AvuListRequest :avus))
