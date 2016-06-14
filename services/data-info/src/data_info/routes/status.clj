(ns data-info.routes.status
  (:use [common-swagger-api.schema]
        [data-info.routes.schemas.status])
  (:require [clojure-commons.service :as commons-service]
            [clojure-commons.error-codes :as ce]
            [data-info.services.status :as status]
            [data-info.util.config :as config]
            [data-info.util.service :as service]
            [ring.util.http-response :refer [ok internal-server-error]]
            [schema.core :as s]))

(defroutes* status
    (GET* "/" [:as {:keys [uri server-name server-port]}]
      :query [{:keys [expecting]} StatusParams]
      :return DataInfoStatusResponse
      :tags ["service-info"]
      :summary "Service Information"
      :description "This endpoint provides the name of the service and its version."
      (ce/trap uri
        #((if (and expecting (not= expecting (:app-name config/svc-info))) internal-server-error ok)
           (assoc (commons-service/get-docs-status config/svc-info server-name server-port config/docs-uri expecting)
                  :iRODS (status/irods-running?)))))

    (GET* "/admin/config" [:as {:keys [uri]}]
      :return (describe s/Any "A map of configuration keys to values.")
      :tags ["service-info"]
      :summary "Configuration Information"
      :description "This endpoint provides the service's currently-running configuration, with private values such as credentials filtered out."
      (service/trap uri config/masked-config)))
