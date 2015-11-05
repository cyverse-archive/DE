(ns terrain.routes.collaborator
  (:use [compojure.core]
        [terrain.util :only [optional-routes]])
  (:require [terrain.clients.metadactyl.raw :as metadactyl]
            [terrain.util.config :as config]
            [terrain.util.service :as service]))

(defn secured-collaborator-routes
  []
  (optional-routes
   [config/collaborator-routes-enabled]

   (GET "/collaborators" []
        (service/success-response (metadactyl/get-collaborators)))

   (POST "/collaborators" [:as {:keys [body]}]
         (service/success-response (metadactyl/add-collaborators body)))

   (POST "/remove-collaborators" [:as {:keys [body]}]
         (service/success-response (metadactyl/remove-collaborators body)))))
