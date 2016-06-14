(ns metadata.routes.status
  (:use [common-swagger-api.schema]
        [ring.util.http-response :only [ok internal-server-error]])
  (:require [clojure-commons.service :as commons-service]
            [metadata.util.config :as config]))

(defroutes* status
  (GET* "/" [:as {:keys [server-name server-port]}]
    :query [{:keys [expecting]} StatusParams]
    :return StatusResponse
    :summary "Service Information"
    :description "This endpoint provides the name of the service and its version."
    ((if (and expecting (not= expecting (:app-name config/svc-info))) internal-server-error ok)
      (commons-service/get-docs-status config/svc-info server-name server-port config/docs-uri expecting))))
