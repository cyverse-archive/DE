(ns terrain.routes.oauth
  (:use [compojure.core])
  (:require [terrain.clients.apps.raw :as apps]
            [terrain.util.service :as service]))

(defn secured-oauth-routes
  "These routes are callback and general information routes for OAuth authorization codes. The callback needs to
   be secured because we need to associate the access token that we obtain using the authorization code with the
   user. Because of this. These can't be direct callback routes. Instead, the callbacks need to go through a
   servlet in the Discovery Environment backend."
  []
  (routes
   (GET "/oauth/access-code/:api-name" [api-name :as {params :params}]
     (service/success-response (apps/get-oauth-access-token api-name params)))

   (GET "/oauth/token-info/:api-name" [api-name]
     (service/success-response (apps/get-oauth-token-info api-name)))

   (GET "/oauth/redirect-uris" []
     (service/success-response (apps/get-oauth-redirect-uris)))))

(defn oauth-admin-routes
  "These routes are general OAuth information routes designed for administrators. They're primarily intended
   for troubleshooting."
  []
  (routes
   (GET "/oauth/token-info/:api-name" [api-name :as {params :params}]
     (service/success-response (apps/get-admin-oauth-token-info api-name params)))))
