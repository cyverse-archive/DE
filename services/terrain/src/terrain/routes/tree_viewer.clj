(ns terrain.routes.tree-viewer
  (:use [compojure.core]
        [terrain.services.buggalo]
        [terrain.util.service]
        [terrain.auth.user-attributes]
        [terrain.util])
  (:require [clojure.tools.logging :as log]
            [terrain.util.config :as config]))

(defn secured-tree-viewer-routes
  []
  (optional-routes
   [config/tree-viewer-routes-enabled]

   (GET "/tree-viewer-urls" [:as {:keys [params]}]
        (log/spy (tree-viewer-urls
                  (required-param params :path)
                  (:shortUsername current-user)
                  params)))))

(defn unsecured-tree-viewer-routes
  []
  (optional-routes
   [config/tree-viewer-routes-enabled]

   (POST "/tree-viewer-urls" [:as {:keys [body params]}]
         (tree-viewer-urls-for body params))))
