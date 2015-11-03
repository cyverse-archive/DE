(ns terrain.routes.admin
  (:use [compojure.core]
        [terrain.auth.user-attributes]
        [terrain.util])
  (:require [terrain.util.config :as config]
            [terrain.services.admin :as admin]
            [clojure.tools.logging :as log]))

(defn secured-admin-routes
  "The routes for the admin endpoints."
  []
  (optional-routes
    [config/admin-routes-enabled]

    (GET "/config" []
         (admin/config))

    (GET "/status" [:as req]
         (admin/status req))))
