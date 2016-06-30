(ns data-info.routes.schemas.status
  (:use [common-swagger-api.schema :only [describe NonBlankString StatusResponse]]
        [data-info.routes.schemas.common])
  (:require [schema.core :as s]))

(s/defschema DataInfoStatusResponse
  (assoc StatusResponse
   :iRODS       (describe Boolean "Whether iRODS is running")))
