(ns apps.routes.schemas.integration-data
  (:use [common-swagger-api.schema :only [describe NonBlankString]]
        [schema.core :only [defschema optional-key]])
  (:import [java.util UUID]))

(defschema IntegrationData
  {:id
   (describe UUID "The integration data identifier.")

   (optional-key :username)
   (describe NonBlankString "The username associated with the integration data entry.")

   :email
   (describe NonBlankString "The user's email address.")

   :name
   (describe NonBlankString "The user's name.")})

(defschema IntegrationDataListing
  {:integration_data
   (describe [IntegrationData] "The list of integration data entries.")

   :total
   (describe Long "The total number of matching integration data entries.")})
