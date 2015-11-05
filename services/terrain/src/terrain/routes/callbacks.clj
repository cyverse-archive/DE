(ns terrain.routes.callbacks
  (:use [compojure.core]
        [terrain.util :only [optional-routes flagged-routes]])
  (:require [terrain.services.callbacks :as svc]
            [terrain.util.config :as config]))

(defn- de-callback-routes
  "Callback routes used by the DE."
  []
  (optional-routes
   [config/app-routes-enabled]

   (POST "/notification" [:as {body :body}]
         (svc/receive-notification body))))

(defn unsecured-callback-routes
  "All unsecured callback routes."
  []
  (context "/callbacks" []
           (flagged-routes
            (de-callback-routes))))
