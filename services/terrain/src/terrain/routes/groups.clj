(ns terrain.routes.groups
  (:use [compojure.core])
  (:require [terrain.clients.apps.raw :as apps]
            [terrain.util.service :as service]))

(defn admin-groups-routes
  []
  (routes
   (GET "/groups/workshop" []
     (service/success-response (apps/get-workshop-group)))

   (GET "/groups/workshop/members" []
     (service/success-response (apps/get-workshop-group-members)))

   (PUT "/groups/workshop/members" [:as {:keys [body]}]
     (service/success-response (apps/update-workshop-group-members body)))))
