(ns terrain.routes.data
  (:use [compojure.core]
        [terrain.services.file-listing]
        [terrain.services.sharing :only [share unshare]]
        [terrain.auth.user-attributes]
        [terrain.util])
  (:require [terrain.util.config :as config]
            [terrain.clients.data-info :as data]
            [terrain.clients.saved-searches :as saved]))

(defn secured-data-routes
  "The routes for data sharing endpoints."
  []
  (optional-routes
   [config/data-routes-enabled]

   (POST "/filetypes/type" [:as req]
      (controller req data/set-file-type :params :body))

   (GET "/filetypes/type-list" [:as req]
      (controller req data/get-type-list))

   (POST "/share" [:as req]
         (share req))

   (POST "/unshare" [:as req]
         (unshare req))

   (GET "/saved-searches" []
        (saved/get-saved-searches (:username current-user)))

   (POST "/saved-searches" [:as {:keys [body]}]
         (saved/set-saved-searches (:username current-user) body))

   (DELETE "/saved-searches" []
           (saved/delete-saved-searches (:username current-user)))))
