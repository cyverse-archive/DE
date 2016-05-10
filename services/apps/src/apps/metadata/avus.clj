(ns apps.metadata.avus
  (:require [apps.clients.metadata :as metadata-client]
            [apps.persistence.app-metadata :as app-db]
            [apps.service.apps.de.permissions :as perms]
            [apps.service.apps.de.validation :as validation]))

(defn list-avus
  [{username :shortUsername :as user} app-id admin?]
  (let [app (app-db/get-app app-id)]
    (validation/verify-app-permission user app "read" admin?)
    (metadata-client/list-avus username app-id)))

(defn set-avus
  [{username :shortUsername :as user} app-id body admin?]
  (let [app (app-db/get-app app-id)]
    (validation/verify-app-permission user app "write" admin?)
    (metadata-client/set-avus username app-id body)))

(defn update-avus
  [{username :shortUsername :as user} app-id body admin?]
  (let [app (app-db/get-app app-id)]
    (validation/verify-app-permission user app "write" admin?)
    (metadata-client/update-avus username app-id body)))
