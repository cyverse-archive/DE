(ns terrain.routes.pref
  (:use [compojure.core]
        [terrain.services.user-prefs]
        [terrain.util])
  (:require [clojure-commons.error-codes :as ce]
            [terrain.util.config :as config]))

(defn secured-pref-routes
  []
  (optional-routes
   [config/pref-routes-enabled]

   (GET "/preferences" []
        (do-get-prefs))

   (POST "/preferences" [:as {body :body}]
         (do-post-prefs (slurp body)))

   (DELETE "/preferences" []
           (remove-prefs))))
