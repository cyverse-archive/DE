(ns metadata.routes.domain.avus
  (:use [common-swagger-api.schema :only [->optional-param
                                          describe
                                          NonBlankString
                                          StandardUserQueryParams]]
        [metadata.routes.domain.common])
  (:require [schema.core :as s])
  (:import [java.util UUID]))

(def TemplateIdPathParam (describe UUID "The Metadata Template's UUID"))
(def AvuIdPathParam (describe UUID "The Metadata Template AVU's UUID"))

(def DataItemIdParam TargetIdPathParam)
(def AvuIdParam AvuIdPathParam)
(def MetadataTemplateIdParam TemplateIdPathParam)

(s/defschema FilterByAvuParams
  (merge StandardUserQueryParams
         {:attr  (describe NonBlankString "The Attribute's name")
          :value (describe NonBlankString "The Attribute's value")}))

(s/defschema AvuCopyQueryParams
  (assoc StandardUserQueryParams
    (s/optional-key :force)
    (describe Boolean "
Whether to validate that none of the destination data items already have Metadata Template AVUs set
with any of the attributes found in any of the Metadata Template AVUs associated with the source
`data-id`, otherwise an `ERR_NOT_UNIQUE` error is returned")))

(s/defschema Avu
  {:id AvuIdParam
   :attr (describe String "The Attribute's name")
   :value (describe String "The Attribute's value")
   :unit (describe String "The Attribute's unit")
   :target_id TargetIdPathParam
   :created_by (describe String "The ID of the user who created the AVU")
   :modified_by (describe String "The ID of the user who last modified the AVU")
   :created_on (describe Long "The date the AVU was created in ms since the POSIX epoch")
   :modified_on (describe Long "The date the AVU was last modified in ms since the POSIX epoch")})

(s/defschema AvuList
  {:avus (describe [Avu] "The list of AVUs associated with the target")})

(s/defschema MetadataTemplateAvuList
  (merge AvuList
         {:template_id MetadataTemplateIdParam}))

(s/defschema DataItemMetadataTemplateList
  {:data_id DataItemIdParam
   :templates (describe [MetadataTemplateAvuList]
                "The list of the data item's Metadata Templates and their associated AVUs")})

(s/defschema DataItemMetadataTemplateAvuList
  (merge MetadataTemplateAvuList
    {:data_id DataItemIdParam}))

(s/defschema AvuRequest
  (-> Avu
      (->optional-param :id)
      (->optional-param :target_id)
      (->optional-param :created_by)
      (->optional-param :modified_by)
      (->optional-param :created_on)
      (->optional-param :modified_on)))

(s/defschema AvuListRequest
  {:avus
   (describe [AvuRequest]
             "The AVUs to save for the target data item and to associate with the Metadata Template.")})

(s/defschema UpdateMetadataTemplateAvuRequest
  (-> MetadataTemplateAvuList
      (->optional-param :template_id)
      (merge AvuListRequest)))

(s/defschema SetMetadataTemplateAvuRequest
  (->optional-param UpdateMetadataTemplateAvuRequest :avus))

(s/defschema SetAvuRequest
  (->optional-param AvuListRequest :avus))

(s/defschema DataItem
  {:id   DataItemIdParam
   :type (describe DataTypeEnum "The type of this data item")})

(s/defschema DataItemList
  {:filesystem (describe [DataItem] "A list of file and folder items")})
