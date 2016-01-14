(ns iplant_groups.core
  (:gen-class)
  (:require [iplant_groups.util.config :as config]
            [me.raynes.fs :as fs]
            [clj-http.client :as http]
            [clojure.tools.logging :as log]
            [common-cli.core :as ccli]
            [service-logging.thread-context :as tc]))

(defn init-service
  ([]
     (init-service config/default-config-file))
  ([config-path]
     (config/load-config-from-file config-path)))

(defn cli-options
  []
  [["-c" "--config PATH" "Path to the config file"
    :default config/default-config-file
    :validate [#(fs/exists? %) "The config file does not exist."
               #(fs/readable? %) "The config file is not readable."]]
   ["-v" "--version" "Print out the version number."]
   ["-h" "--help"]])

(defn run-jetty
  []
  (require 'iplant_groups.routes
           'ring.adapter.jetty)
  (log/warn "Started listening on" (config/listen-port))
  ((eval 'ring.adapter.jetty/run-jetty) (eval 'iplant_groups.routes/app) {:port (config/listen-port)}))

(defn -main
  [& args]
  (tc/with-logging-context config/svc-info
    (let [{:keys [options arguments errors summary]} (ccli/handle-args config/svc-info args cli-options)]
      (init-service (:config options))
      (http/with-connection-pool {:timeout 5 :threads 10 :insecure? false :default-per-route 10}
        (run-jetty)))))
