(ns apps.routes.integration-data
  (:use [common-swagger-api.schema]
        [apps.routes.params :only [IntegrationDataIdPathParam IntegrationDataSearchParams SecuredQueryParams]]
        [apps.user :only [current-user]]
        [ring.util.http-response :only [ok]])
  (:require [apps.routes.schemas.integration-data :as schema]
            [apps.service.integration-data :as integration-data]))

(defroutes* admin-integration-data
  (GET* "/" []
    :query [params IntegrationDataSearchParams]
    :summary "List Integration Data Records"
    :return schema/IntegrationDataListing
    :description "This service allows administrators to list and search for integration data in the DE apps
    database. Entries may be filtered by name or email address using the `search` query parameter. They may
    also be sorted by username, email address or name using the `:sort-field` query parameter."
    (ok (integration-data/list-integration-data current-user params)))

  (POST* "/" []
    :query [params SecuredQueryParams]
    :summary "Add an Integration Data Record"
    :body [body (describe schema/IntegrationDataRequest "The integration data record to add.")]
    :return schema/IntegrationData
    :description "This service allows administrators to add a new integration data record to the DE apps database."
    (ok (integration-data/add-integration-data current-user body)))

  (GET* "/:integration-data-id" []
    :path-params [integration-data-id :- IntegrationDataIdPathParam]
    :query [params SecuredQueryParams]
    :summary "Get an Integration Data Record"
    :return schema/IntegrationData
    :description "This service allows administrators to retrieve information about an integration data record."
    (ok (integration-data/get-integration-data current-user integration-data-id)))

  (PUT* "/:integration-data-id" []
    :path-params [integration-data-id :- IntegrationDataIdPathParam]
    :query [params SecuredQueryParams]
    :summary "Update an Integration Data Record"
    :body [body (describe schema/IntegrationDataUpdate "The updated integration data information.")]
    :return schema/IntegrationData
    :description "This service allows administrators to update integration data records in the DE apps database."
    (ok (integration-data/update-integration-data current-user integration-data-id body))))
