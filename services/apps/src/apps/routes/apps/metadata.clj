(ns apps.routes.apps.metadata
  (:use [common-swagger-api.routes]
        [common-swagger-api.schema]
        [apps.routes.middleware :only [wrap-metadata-base-url]]
        [apps.routes.params]
        [apps.user :only [current-user]])
  (:require [apps.metadata.avus :as avus]
            [apps.util.service :as service]
            [compojure.route :as route]))

(defroutes* app-metadata

  (GET* "/" []
        :path-params [app-id :- AppIdPathParam]
        :query [params SecuredQueryParams]
        :middlewares [wrap-metadata-base-url]
        :summary "View all Metadata AVUs"
        :description (str
"Lists all AVUs associated with an app.
 The authenticated user must have `read` permission to view this metadata."
(get-endpoint-delegate-block
  "metadata"
  "GET /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for response information.")
        (avus/list-avus current-user app-id false))

  (POST* "/" [:as {:keys [body]}]
         :path-params [app-id :- AppIdPathParam]
         :query [params SecuredQueryParams]
         :middlewares [wrap-metadata-base-url]
         :summary "Add/Update Metadata AVUs"
         :description (str
"Adds or updates Metadata AVUs on the app.
 The authenticated user must have `write` permission to edit this metadata."
(get-endpoint-delegate-block
  "metadata"
  "POST /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for request and response information.")
         (avus/update-avus current-user app-id body false))

  (PUT* "/" [:as {:keys [body]}]
        :path-params [app-id :- AppIdPathParam]
        :query [params SecuredQueryParams]
        :middlewares [wrap-metadata-base-url]
        :summary "Set Metadata AVUs"
        :description (str
"Sets Metadata AVUs on the app.
 The authenticated user must have `write` permission to edit this metadata."
(get-endpoint-delegate-block
  "metadata"
  "PUT /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for request and response information.")
        (avus/set-avus current-user app-id body false))

  (route/not-found (service/unrecognized-path-response)))

(defroutes* admin-app-metadata

  (GET* "/" []
        :path-params [app-id :- AppIdPathParam]
        :query [params SecuredQueryParams]
        :middlewares [wrap-metadata-base-url]
        :summary "View all Metadata AVUs"
        :description (str
"Lists all AVUs associated with the app."
(get-endpoint-delegate-block
  "metadata"
  "GET /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for response information.")
        (avus/list-avus current-user app-id true))

  (POST* "/" [:as {:keys [body]}]
         :path-params [app-id :- AppIdPathParam]
         :query [params SecuredQueryParams]
         :middlewares [wrap-metadata-base-url]
         :summary "Add/Update Metadata AVUs"
         :description (str
"Adds or updates Metadata AVUs on the app."
(get-endpoint-delegate-block
  "metadata"
  "POST /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for request and response information.")
         (avus/update-avus current-user app-id body true))

  (PUT* "/" [:as {:keys [body]}]
        :path-params [app-id :- AppIdPathParam]
        :query [params SecuredQueryParams]
        :middlewares [wrap-metadata-base-url]
        :summary "Set Metadata AVUs"
        :description (str
"Sets Metadata AVUs on the app."
(get-endpoint-delegate-block
  "metadata"
  "PUT /avus/{target-type}/{target-id}")
"Where `{target-type}` is `app`.
Please see the metadata service documentation for request and response information.")
        (avus/set-avus current-user app-id body true))

  (route/not-found (service/unrecognized-path-response)))
