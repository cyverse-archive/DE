(ns data-info.routes.avus
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.avus])
  (:require [data-info.services.metadata :as meta]
            [data-info.util.service :as svc]))

(defroutes* avus-routes
  (context* "/admin/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (GET* "/avus" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :return AVUGetResult
      :summary "List AVUs (administrative)"
      :description (str "List iRODS AVUs associated with a data item. Include administrative/system AVUs."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE"))
      (svc/trap uri meta/admin-metadata-get data-id))

    (POST* "/avus" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [{:keys [irods-avus]} (describe AVUListing "An list of AVUs to add")]
      :return AVUChangeResult
      :summary "Add AVUs (administrative)"
      :description (str "Associate AVUs with a data item. Allow adding any AVU."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED"))
      (svc/trap uri meta/admin-metadata-add data-id irods-avus))

    (DELETE* "/avus" [:as {uri :uri}]
      :query [{:keys [user attr value]} AVUDeleteParams]
      :return AVUChangeResult
      :summary "Delete AVU (administrative)"
      :description (str "Delete a single AVU from a data item. Allows deleting any AVU."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED"))
      (svc/trap uri meta/admin-metadata-delete data-id [{:attr attr :value value}])))

  (context* "/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (GET* "/avus" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :return AVUGetResult
      :summary "List AVUs"
      :description (str "List iRODS AVUs associated with a data item."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE"))
      (svc/trap uri meta/metadata-get user data-id :system false))

    (POST* "/avus" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [{:keys [irods-avus]} (describe AVUListing "An list of AVUs to add")]
      :return AVUChangeResult
      :summary "Add AVUs"
      :description (str "Associate AVUs with a data item. Administrative AVUs may not be added with this endpoint."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED"))
      (svc/trap uri meta/metadata-add user data-id irods-avus))

    (PUT* "/avus" [:as {uri :uri}]
      :query [{:keys [user]} StandardUserQueryParams]
      :body [{:keys [irods-avus]} (describe AVUListing "A list of AVUs to set for this file. May not include administrative AVUs, and will not delete them.")]
      :return AVUSetResult
      :summary "Set AVUs"
      :description (str "Set the iRODS AVUS for a data item to a provided set. This set may not include administrative AVUs, and similarly will not remove administrative AVUs."
(get-error-code-block "ERR_NOT_A_USER, ERR_NOT_READABLE, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_NOT_AUTHORIZED"))
      (svc/trap uri meta/metadata-set user data-id irods-avus))))
