(ns dewey.status
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY]]
            [cheshire.core :as json]))

(defroutes dewey-status-app
  (ANY "/" []
       (resource :available-media-types ["text/plain"]
                 :handle-ok "Welcome to Dewey!"))

  (ANY "/status" []
       (resource :available-media-types ["application/json"]
                 :handle-ok (json/encode {:service "dewey" :status "ok"}))))

(def dewey-handler
  (-> dewey-status-app
      (wrap-params)))

(defn start-jetty
  [listen-port]
  (require 'ring.adapter.jetty)
  ((eval 'ring.adapter.jetty/run-jetty) #'dewey-handler {:port listen-port}))
