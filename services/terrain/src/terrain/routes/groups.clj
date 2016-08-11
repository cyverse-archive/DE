(ns terrain.routes.groups
  (:use [compojure.core])
  (:require [terrain.clients.apps.raw :as apps]
            [terrain.util.service :as service]))

(defn admin-groups-routes
  []
  (routes
   (GET "/admin/groups/workshop" []
     (service/success-response (apps/get-workshop-group)))

   (GET "/admin/groups/workshop/members" []
     (service/success-response (apps/get-workshop-group-members)))

   (PUT "/admin/groups/workshop/members" [:as {:keys [body]])
     (service/success-response (apps/update-workshop-group-members body))
