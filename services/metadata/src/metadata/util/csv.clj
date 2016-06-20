(ns metadata.util.csv
  (:require [clojure.data.csv :as csv]))

(defn csv-string
  [data]
  (let [string-writer (java.io.StringWriter.)]
    (csv/write-csv string-writer data)
    (. string-writer toString)))
