(ns apps.routes.integration-data
  (:use [common-swagger-api.schema]
        [apps.routes.params :only [IntegrationDataSearchParams]]
        [apps.routes.schemas.integration-data :only [IntegrationDataListing]]
        [apps.user :only [current-user]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.integration-data :as integration-data]))

(defroutes* admin-integration-data
  (GET* "/" []
    :query [params IntegrationDataSearchParams]
    :summary "List Integration Data Records"
    :return IntegrationDataListing
    :description "This service allows administrators to list and search for integration data in the DE apps
    database. Entries may be filtered by name or email address using the `search` query parameter. They may
    also be sorted by username, email address or name using the `:sort-field` query parameter."
    (ok (integration-data/list-integration-data current-user params))))
