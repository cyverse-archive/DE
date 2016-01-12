(ns terrain.routes.permanent-id-requests
  (:use [compojure.core]
        [terrain.services.permanent-id-requests]
        [terrain.util :only [optional-routes]])
  (:require [terrain.util.config :as config]
            [terrain.util.service :as service]))

(defn permanent-id-request-routes
  "The routes for Permanent ID Request endpoints."
  []
  (optional-routes
    [config/filesystem-routes-enabled]

    (GET "/permanent-id-requests" [:as {params :params}]
      (service/success-response (list-permanent-id-requests params)))

    (POST "/permanent-id-requests" [:as {:keys [params body]}]
      (service/success-response (create-permanent-id-request params body)))

    (GET "/permanent-id-requests/status-codes" [:as {params :params}]
      (service/success-response (list-permanent-id-request-status-codes params)))

    (GET "/permanent-id-requests/types" [:as {params :params}]
      (service/success-response (list-permanent-id-request-types params)))

    (GET "/permanent-id-requests/:request-id" [request-id :as {params :params}]
      (service/success-response (get-permanent-id-request request-id params)))))

(defn admin-permanent-id-request-routes
  "The admin routes for Permanent ID Request endpoints."
  []
  (optional-routes
    [#(and (config/admin-routes-enabled)
           (config/filesystem-routes-enabled))]

    (GET "/permanent-id-requests" [:as {params :params}]
      (service/success-response (admin-list-permanent-id-requests params)))

    (GET "/permanent-id-requests/:request-id" [request-id :as {params :params}]
      (service/success-response (admin-get-permanent-id-request request-id params)))

    (POST "/permanent-id-requests/:request-id/ezid" [request-id :as {:keys [params body]}]
      (service/success-response (create-permanent-id request-id params body)))

    (POST "/permanent-id-requests/:request-id/status" [request-id :as {:keys [params body]}]
      (service/success-response (update-permanent-id-request request-id params body)))))
