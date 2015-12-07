(ns terrain.routes.session
  (:use [compojure.core]
        [terrain.services.user-sessions]
        [terrain.util])
  (:require [terrain.util.config :as config]))

(defn secured-session-routes
  []
  (optional-routes
   [config/session-routes-enabled]

   (GET "/sessions" []
        (user-session))

   (POST "/sessions" [:as {body :body}]
         (user-session (slurp body)))

   (DELETE "/sessions" []
           (remove-session))))
