(ns terrain.routes.fileio
  (:use [compojure.core]
        [terrain.auth.user-attributes])
  (:require [terrain.util.config :as config]
            [terrain.services.fileio.controllers :as fio]
            [terrain.util :as util]))


(defn secured-fileio-routes
  "The routes for file IO endpoints."
  []
  (util/optional-routes [config/data-routes-enabled]

    (GET "/fileio/download" [:as {:keys [params]}]
      (fio/download params))

    (POST "/fileio/upload" [dest :as req]
      (fio/upload (get-in req [:user-info :user]) dest req))

    (POST "/fileio/urlupload" [:as {:keys [params body]}]
      (fio/urlupload params body))

    (POST "/fileio/save" [:as {:keys [params body]}]
      (fio/save params body))

    (POST "/fileio/saveas" [:as {:keys [params body]}]
      (fio/saveas params body))))
