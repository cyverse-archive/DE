(ns apps.routes.schemas.integration-data
  (:use [common-swagger-api.schema :only [describe NonBlankString]]
        [schema.core :only [defschema optional-key]])
  (:import [java.util UUID]))

(defschema IntegrationDataUpdate
  {:email
   (describe NonBlankString "The user's email address.")

   :name
   (describe NonBlankString "The user's name.")})

(defschema IntegrationDataRequest
  (assoc IntegrationDataUpdate
         (optional-key :username)
         (describe NonBlankString "The username associated with the integration data entry.")))

(defschema IntegrationData
  (assoc IntegrationDataRequest
         :id (describe UUID "The integration data identifier.")))

(defschema IntegrationDataListing
  {:integration_data
   (describe [IntegrationData] "The list of integration data entries.")

   :total
   (describe Long "The total number of matching integration data entries.")})
