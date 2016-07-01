(ns apps.routes.collaborators
  (:use [common-swagger-api.schema]
        [apps.routes.params]
        [apps.routes.schemas.collaborator]
        [apps.user :only [current-user]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.collaborators :as collaborators]))

(defroutes* collaborators
  (GET* "/" []
        :query [params SecuredQueryParams]
        :summary "List Collaborators"
        :return Collaborators
        :description "This service allows users to retrieve a list of their collaborators."
        (ok (collaborators/get-collaborators current-user)))

  (POST* "/" []
         :query [params SecuredQueryParams]
         :summary "Add Collaborators"
         :body [body (describe Collaborators "The collaborators to add.")]
         :description "This service allows users to add other users to their list of collaborators."
         (ok (collaborators/add-collaborators current-user body)))

  (POST* "/shredder" []
         :query [params SecuredQueryParams]
         :summary "Remove Collaborators"
         :body [body (describe Collaborators "The collaborators to remove.")]
         :description "This service allows users to remove other users from their list of
         collaborators."
         (ok (collaborators/remove-collaborators current-user body))))
