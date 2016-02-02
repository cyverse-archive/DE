(ns clojure-commons.exception-util
  (:use [slingshot.slingshot :only [throw+]])
  (:require [clojure-commons.exception :as cx]))

(defn unauthorized
  "Throws an error indicating that the request is unauthorized."
  [reason & {:as ex-info}]
  (throw+ (assoc ex-info :type ::cx/authentication-not-found :error reason)))

(defn forbidden
  "Throws an error indicating that the request is forbidden."
  [reason & {:as ex-info}]
  (throw+ (assoc ex-info :type ::cx/forbidden :error reason)))
