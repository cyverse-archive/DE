(ns apps.metadata.avus
  (:require [apps.persistence.app-metadata :as app-db]
            [apps.service.apps.de.categorization :as categorization]
            [apps.service.apps.de.validation :as validation]
            [apps.util.service :as service]
            [cheshire.core :as json]
            [metadata-client.core :as metadata-client]))

(defn list-avus
  [{username :shortUsername :as user} app-id admin?]
  (let [app (app-db/get-app app-id)]
    (validation/verify-app-permission user app "read" admin?)
    (metadata-client/list-avus username "app" app-id)))

(defn set-avus
  [{username :shortUsername :as user} app-id body admin?]
  (let [{app-name :name :as app} (app-db/get-app app-id)
        request (service/parse-json body)]
    (validation/verify-app-permission user app "write" admin?)
    (categorization/validate-app-name-in-hierarchy-avus username app-id app-name (:avus request))
    (metadata-client/set-avus username "app" app-id (json/encode request))))

(defn update-avus
  [{username :shortUsername :as user} app-id body admin?]
  (let [{app-name :name :as app} (app-db/get-app app-id)
        request (service/parse-json body)]
    (validation/verify-app-permission user app "write" admin?)
    (categorization/validate-app-name-in-hierarchy-avus username app-id app-name (:avus request))
    (metadata-client/update-avus username "app" app-id (json/encode request))))
