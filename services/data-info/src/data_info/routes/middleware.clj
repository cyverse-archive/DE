(ns data-info.routes.middleware
  (:require [data-info.util.config :as config]
            [metadata-client.middleware :as client-middleware]))

(defn wrap-metadata-base-url
  [handler]
  (client-middleware/wrap-metadata-base-url handler config/metadata-base-url))
