(ns kameleon.util
  (:use [korma.core :exclude [update]])
  (:require [clojure.tools.logging :as log]))

(defn query-spy
  [desc query]
  (log/debug desc (sql-only (select query)))
  query)

(defn normalize-string
  "Normalizes a string for use in comparisons. Comparisons in which this function is used on both sides will be
  case-insensitive with leading and trailing whitespace removed and consecutive whitespace collapsed to a single
  space."
  [s]
  (sqlfn lower (sqlfn regexp_replace (sqlfn trim s) "\\s+" " ")))
