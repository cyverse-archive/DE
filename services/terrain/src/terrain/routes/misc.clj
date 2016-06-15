(ns terrain.routes.misc
  (:use [compojure.core]
        [ring.util.http-response])
  (:require [clojure.string :as string])
  (:import [java.util UUID]))

(defn unsecured-misc-routes
  []
  (routes
    (GET "/" [:as {{expecting :expecting} :params :as req}]
      (if (and expecting (not= expecting "terrain"))
        (internal-server-error (str "The infinite is attainable with Terrain!\nError: expecting " expecting "."))
        (ok "The infinite is attainable with Terrain!\n")))

    (GET "/uuid" []
      (string/upper-case (str (UUID/randomUUID))))))
