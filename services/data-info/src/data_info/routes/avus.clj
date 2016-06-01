(ns data-info.routes.avus
  (:use [common-swagger-api.routes]
        [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.avus]
        [data-info.routes.middleware :only [wrap-metadata-base-url]])
  (:require [data-info.services.metadata :as meta]
            [data-info.util.service :as svc]))

(defroutes* avus-routes
  (context* "/admin/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (GET* "/metadata" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :return AVUGetResult
      :middlewares [wrap-metadata-base-url]
      :summary "List AVUs (administrative)"
      :description
          (str "List all AVUs associated with a data item. Include administrative/system iRODS AVUs.
           This service appends the info described below in the Response Model (200) to the metadata
           service response for the given `data-id`."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE")
(get-endpoint-delegate-block
  "metadata"
  "GET /avus/{target-type}/{target-id}")
"Please see the metadata service for additional response information.")
      (svc/trap uri meta/admin-metadata-get data-id))

    (PATCH* "/metadata" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [body (describe AddMetadataRequest "The iRODS and Metadata AVUs to add")]
      :return AVUChangeResult
      :middlewares [wrap-metadata-base-url]
      :summary "Add AVUs (administrative)"
      :description
            (str "Associate iRODS and Metadata AVUs with a data item. Allow adding any AVU.
             The info described below in the `body` parameter's Model is processed by this endpoint,
             and what's left of the request body is forwarded to the metadata service."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED")
(get-endpoint-delegate-block
  "metadata"
  "POST /avus/{target-type}/{target-id}")
"Please see the metadata service for additional request information.")
      (svc/trap uri meta/admin-metadata-add data-id body)))

  (context* "/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (GET* "/metadata" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :return AVUGetResult
      :middlewares [wrap-metadata-base-url]
      :summary "List AVUs"
      :description
          (str "List all AVUs associated with a data item.
           This service appends the info described below in the Response Model (200) to the metadata
           service response for the given `data-id`."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE")
(get-endpoint-delegate-block
  "metadata"
  "GET /avus/{target-type}/{target-id}")
"Please see the metadata service for additional response information.")
      (svc/trap uri meta/metadata-get user data-id :system false))

    (PATCH* "/metadata" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [body (describe AddMetadataRequest "The iRODS and Metadata AVUs to add")]
      :return AVUChangeResult
      :middlewares [wrap-metadata-base-url]
      :summary "Add AVUs"
      :description
            (str "Associate iRODS and Metadata AVUs with a data item.
             Administrative iRODS AVUs may not be added with this endpoint.
             The info described below in the `body` parameter's Model is processed by this endpoint,
             and what's left of the request body is forwarded to the metadata service."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED")
(get-endpoint-delegate-block
  "metadata"
  "POST /avus/{target-type}/{target-id}")
"Please see the metadata service for additional request information.")
      (svc/trap uri meta/metadata-add user data-id body))

    (PUT* "/metadata" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [body (describe MetadataListing
                            "A list of AVUs to set for this file.
                             May not include administrative AVUs, and will not delete them.")]
      :return AVUChangeResult
      :middlewares [wrap-metadata-base-url]
      :summary "Set AVUs"
      :description
           (str "Set the iRODS and metadata AVUS for a data item to a provided set.
            The iRODS set may not include administrative AVUs,
            and similarly will not remove administrative AVUs.
            The info described below in the `body` parameter's Model is processed by this endpoint,
            and what's left of the request body is forwarded to the metadata service."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED")
(get-endpoint-delegate-block
  "metadata"
  "PUT /avus/{target-type}/{target-id}")
"Please see the metadata service for additional request information.")
      (svc/trap uri meta/metadata-set user data-id body))

    (POST* "/metadata/copy" [:as {uri :uri}]
      :query [{:keys [user force]} MetadataCopyRequestParams]
      :body [{:keys [destination_ids]} (describe MetadataCopyRequest "The destination data items.")]
      :return MetadataCopyResult
      :middlewares [wrap-metadata-base-url]
      :summary "Copy Metadata"
      :description
           (str "Copies all IRODS AVUs visible to the client and Metadata AVUs from the data
            item with the ID given in the URL to other data items with the IDs sent in the request body."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED, ERR_NOT_UNIQUE")
(get-endpoint-delegate-block
  "metadata"
  "POST /avus/{target-type}/{target-id}/copy"))
           (svc/trap uri meta/metadata-copy user force data-id destination_ids))))
