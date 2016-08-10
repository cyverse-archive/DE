(ns apps.routes.groups
  (:use [common-swagger-api.schema]
        [apps.routes.params :only [SecuredQueryParams]]
        [ring.util.http-response :only [ok]])
  (:require [apps.routes.schemas.groups :as schema]
            [apps.service.groups :as groups]))

(defroutes* admin-group-routes
  (GET* "/workshop" []
    :query [params SecuredQueryParams]
    :summary "Retrieve Workshop Group Information"
    :return schema/Group
    :description "This service allows administrators to retrieve information about the workshop users
    group"
    (ok (groups/get-workshop-group))))
