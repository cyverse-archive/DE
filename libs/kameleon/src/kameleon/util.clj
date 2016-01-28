(ns kameleon.util
  (:use [korma.core :exclude [update]])
  (:require [clojure.tools.logging :as log]))

(defn query-spy
  [desc query]
  (log/debug desc (sql-only (select query)))
  query)
