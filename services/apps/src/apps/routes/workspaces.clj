(ns apps.routes.workspaces
  (:use [common-swagger-api.schema]
        [apps.routes.params]
        [apps.routes.schemas.workspace]
        [apps.user :only [current-user]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.workspace :as workspace]))

(defroutes* workspaces
  (GET* "/" []
        :query [params SecuredQueryParams]
        :return Workspace
        :summary "Obtain user workspace information."
        :description "This endpoint returns information about the workspace belonging to the
        authenticated user."
        (ok (workspace/get-workspace current-user))))
