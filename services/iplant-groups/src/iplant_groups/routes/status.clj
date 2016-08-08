(ns iplant_groups.routes.status
  (:use [common-swagger-api.schema]
        [iplant_groups.routes.schemas.status]
        [ring.util.http-response :only [ok internal-server-error]])
  (:require [clojure-commons.service :as commons-service]
            [iplant_groups.clients.grouper :as grouper]
            [iplant_groups.util.config :as config]))

(defroutes* status
  (GET* "/" [:as {:keys [server-name server-port]}]
    :query [{:keys [expecting]} StatusParams]
    :return IplantGroupsStatusResponse
    :summary "Service Information"
    :description "This endpoint provides the name of the service and its version."
    ((if (and expecting (not= expecting (:app-name config/svc-info))) internal-server-error ok)
      (assoc (commons-service/get-docs-status config/svc-info server-name server-port config/docs-uri expecting)
             :grouper (grouper/grouper-ok?)))))
