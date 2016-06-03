(ns debug-utils.log-time
  (:require [clojure.tools.logging :as log]))

(def ^:dynamic *depth*
  "Dynamic context to be used in calculating indentation."
  0)

(defmacro log-time [label & body]
  `(binding [*depth* (+ *depth* 1)]
     (let [start# (System/nanoTime)
           ret# (do ~@body)
           end# (System/nanoTime)]
       (log/info (str (apply str (take (- *depth* 1) (repeat "    "))) ~label " took: " (/ (- end# start#) 1e6) "ms"))
       ret#)))
