(ns terrain.core
  (:gen-class)
  (:use [clojure.java.io :only [file]]
        [clojure-commons.lcase-params :only [wrap-lcase-params]]
        [clojure-commons.query-params :only [wrap-query-params]]
        [service-logging.middleware :only [wrap-logging clean-context]]
        [compojure.core]
        [compojure.api.middleware :only [wrap-exceptions]]
        [ring.middleware.keyword-params]
        [terrain.routes.admin]
        [terrain.routes.callbacks]
        [terrain.routes.data]
        [terrain.routes.fileio]
        [terrain.routes.metadata]
        [terrain.routes.misc]
        [terrain.routes.notification]
        [terrain.routes.pref]
        [terrain.routes.session]
        [terrain.routes.tree-viewer]
        [terrain.routes.user-info]
        [terrain.routes.collaborator]
        [terrain.routes.filesystem]
        [terrain.routes.search]
        [terrain.routes.coge]
        [terrain.routes.oauth]
        [terrain.routes.favorites]
        [terrain.routes.tags]
        [terrain.routes.comments]
        [terrain.auth.user-attributes]
        [terrain.util.service])
  (:require [compojure.route :as route]
            [cheshire.core :as cheshire]
            [clojure-commons.exception :as cx]
            [terrain.util.config :as config]
            [clojure.tools.nrepl.server :as nrepl]
            [me.raynes.fs :as fs]
            [clj-http.client :as http]
            [common-cli.core :as ccli]
            [terrain.services.filesystem.icat :as icat]
            [terrain.util :as util]
            [terrain.util.transformers :as transform]
            [clojure.tools.logging :as log]
            [service-logging.thread-context :as tc]))

(defn delayed-handler
  [routes-fn]
  (fn [req]
    (let [handler ((memoize routes-fn))]
      (handler req))))

(defn- wrap-user-info
  [handler]
  (fn [request]
    (let [user-info (transform/add-current-user-to-map {})]
         (log/log 'AccessLogger :trace nil "entering wrap-user-info")
      (if (nil? (:user user-info))
        (handler request)
        (tc/with-logging-context {:user-info (cheshire/encode user-info)}
                                 (handler request))))))

(defn- start-nrepl
  []
  (nrepl/start-server :port 7888))

(defn- iplant-conf-dir-file
  [filename]
  (when-let [conf-dir (System/getenv "IPLANT_CONF_DIR")]
    (let [f (file conf-dir filename)]
      (when (.isFile f) (.getPath f)))))

(defn- cwd-file
  [filename]
  (let [f (file filename)]
    (when (.isFile f) (.getPath f))))

(defn- classpath-file
  [filename]
  (some-> (Thread/currentThread)
          (.getContextClassLoader)
          (.findResource filename)
          (.toURI)
          (file)))

(defn- no-configuration-found
  [filename]
  (throw (RuntimeException. (str "configuration file " filename " not found"))))

(defn- find-configuration-file
  []
  ((some-fn iplant-conf-dir-file cwd-file classpath-file no-configuration-found) "terrain.properties"))

(defn load-configuration-from-file
  "Loads the configuration properties from a file."
  ([]
     (load-configuration-from-file (find-configuration-file)))
  ([path]
     (config/load-config-from-file path)))

(defn lein-ring-init
  "This function is used by leiningen ring plugin to initialize terrain."
  []
  (load-configuration-from-file)
  (icat/configure-icat)
  (start-nrepl))

(defn repl-init
  "This function is used to manually initialize terrain from the leiningen REPL."
  []
  (load-configuration-from-file)
  (icat/configure-icat))

(defn cli-options
  []
  [["-c" "--config PATH" "Path to the config file"
    :default "/etc/iplant/de/terrain.properties"]
   ["-v" "--version" "Print out the version number."]
   ["-h" "--help"]])

(def svc-info
  {:desc "DE service for business logic"
   :app-name "terrain"
   :group-id "org.iplantc"
   :art-id "terrain"
   :service "terrain"})

(defn secured-routes-no-context
  []
  (util/flagged-routes
    (app-category-routes)
    (apps-routes)
    (app-comment-routes)
    (analysis-routes)
    (coge-routes)
    (reference-genomes-routes)
    (tool-routes)
    (route/not-found (unrecognized-path-response))))

(defn secured-routes
  []
  (util/flagged-routes
    (secured-notification-routes)
    (secured-metadata-routes)
    (secured-pref-routes)
    (secured-collaborator-routes)
    (secured-user-info-routes)
    (secured-tree-viewer-routes)
    (secured-data-routes)
    (secured-session-routes)
    (secured-fileio-routes)
    (secured-filesystem-routes)
    (secured-filesystem-metadata-routes)
    (secured-search-routes)
    (secured-oauth-routes)
    (secured-favorites-routes)
    (secured-tag-routes)
    (data-comment-routes)
    (route/not-found (unrecognized-path-response))))

(defn admin-routes
  []
  (util/flagged-routes
    (secured-admin-routes)
    (admin-data-comment-routes)
    (admin-category-routes)
    (admin-apps-routes)
    (admin-app-comment-routes)
    (admin-filesystem-metadata-routes)
    (admin-notification-routes)
    (admin-reference-genomes-routes)
    (admin-tool-routes)
    (route/not-found (unrecognized-path-response))))

(defn unsecured-routes
  []
  (util/flagged-routes
    (unsecured-misc-routes)
    (unsecured-notification-routes)
    (unsecured-tree-viewer-routes)
    (unsecured-callback-routes)))

(def admin-handler
  (-> (delayed-handler admin-routes)
      (wrap-routes authenticate-current-user)
      (wrap-routes wrap-user-info)
      (wrap-routes validate-current-user)
      (wrap-routes wrap-exceptions  cx/exception-handlers)
      (wrap-routes wrap-logging)))

(def secured-routes-handler
  (-> (delayed-handler secured-routes)
      (wrap-routes authenticate-current-user)
      (wrap-routes wrap-user-info)
      (wrap-routes wrap-exceptions  cx/exception-handlers)
      (wrap-routes wrap-logging)))

(def secured-routes-no-context-handler
  (-> (delayed-handler secured-routes-no-context)
      (wrap-routes authenticate-current-user)
      (wrap-routes wrap-user-info)
      (wrap-routes wrap-exceptions  cx/exception-handlers)
      (wrap-routes wrap-logging)))

(def unsecured-routes-handler
  (-> (delayed-handler unsecured-routes)
      (wrap-routes wrap-exceptions cx/exception-handlers)
      (wrap-routes wrap-logging)))

(defn terrain-routes
  []
  (util/flagged-routes
    unsecured-routes-handler
    (context "/admin" [] admin-handler)
    (context "/secured" [] secured-routes-handler)
    secured-routes-no-context-handler))

(defn site-handler
  [routes-fn]
  (-> (delayed-handler routes-fn)
      wrap-keyword-params
      wrap-lcase-params
      wrap-query-params
      clean-context))

(def app
  (site-handler terrain-routes))

(defn run-jetty
  []
  (require 'ring.adapter.jetty)
  (log/warn "Started listening on" (config/listen-port))
  ((eval 'ring.adapter.jetty/run-jetty) app {:port (config/listen-port)}))

(defn -main
  [& args]
  (tc/with-logging-context svc-info
    (let [{:keys [options]} (ccli/handle-args svc-info args cli-options)]
      (when-not (fs/exists? (:config options))
        (ccli/exit 1 (str "The config file does not exist.")))
      (when-not (fs/readable? (:config options))
        (ccli/exit 1 "The config file is not readable."))
      (config/load-config-from-file (:config options))
      (http/with-connection-pool {:timeout 5 :threads 10 :insecure? false :default-per-route 10}
        (icat/configure-icat)
        (run-jetty)))))
