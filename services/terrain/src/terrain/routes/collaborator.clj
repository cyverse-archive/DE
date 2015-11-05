(ns terrain.routes.collaborator
  (:use [compojure.core]
        [terrain.util :only [optional-routes]])
  (:require [terrain.clients.apps.raw :as apps]
            [terrain.util.config :as config]
            [terrain.util.service :as service]))

(defn secured-collaborator-routes
  []
  (optional-routes
   [config/collaborator-routes-enabled]

   (GET "/collaborators" []
        (service/success-response (apps/get-collaborators)))

   (POST "/collaborators" [:as {:keys [body]}]
         (service/success-response (apps/add-collaborators body)))

   (POST "/remove-collaborators" [:as {:keys [body]}]
         (service/success-response (apps/remove-collaborators body)))))
