(ns tree-urls-client.middleware
  (:use [tree-urls-client.core :only [with-tree-urls-base]]))

(defn wrap-tree-urls-base
  [handler base-fn]
  (fn [request]
    (with-tree-urls-base (base-fn)
      (handler request))))
