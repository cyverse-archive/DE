(ns metadata-client.middleware
  (:use [metadata-client.core :only [with-metadata-base]]))

(defn wrap-metadata-base-url
  [handler base-url-fn]
  (fn [request]
    (with-metadata-base (base-url-fn) (handler request))))
