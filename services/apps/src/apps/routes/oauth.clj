(ns apps.routes.oauth
  (:use [common-swagger-api.schema]
        [apps.routes.domain.oauth]
        [apps.routes.params]
        [apps.user :only [current-user]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.oauth :as oauth]))

(defroutes* oauth
  (GET* "/access-code/:api-name" []
    :path-params [api-name :- ApiName]
    :query       [params OAuthCallbackQueryParams]
    :return      OAuthCallbackResponse
    :summary     "Obtain an OAuth access token for an authorization code."
    (ok (oauth/get-access-token api-name params)))

  (GET* "/token-info/:api-name" []
    :path-params [api-name :- ApiName]
    :query       [params SecuredQueryParams]
    :return      TokenInfo
    :summary     "Return information about an OAuth access token."
    (ok (oauth/get-token-info api-name current-user))))
