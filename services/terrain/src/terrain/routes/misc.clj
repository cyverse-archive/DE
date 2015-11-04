(ns terrain.routes.misc
  (:use [compojure.core]
        [terrain.util])
  (:require [clojure.string :as string])
  (:import [java.util UUID]))

(defn unsecured-misc-routes
  []
  (routes
    (GET "/" [request]
      "The infinite is attainable with Terrain!\n")

    (GET "/uuid" []
      (string/upper-case (str (UUID/randomUUID))))))
