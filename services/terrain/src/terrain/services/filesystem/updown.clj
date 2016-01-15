(ns terrain.services.filesystem.updown
  (:use [clojure-commons.validators]
        [terrain.services.filesystem.common-paths]
        [slingshot.slingshot :only [throw+]])
  (:require [clojure.tools.logging :as log]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [clojure-commons.error-codes :as error]
            [clojure-commons.file-utils :as ft]
            [terrain.clients.data-info :as data]))


(defn- download-file
  [user file attachment]
  (let [url-path         (data/mk-data-path-url-path file)
        req-map          {:query-params {:user user :attachment attachment} :as :stream}
        handle-not-found (fn [_ _ _] (throw+ {:error_code error/ERR_NOT_FOUND :path file}))]
    (data/trapped-request :get url-path req-map
      :403 handle-not-found
      :404 handle-not-found
      :410 handle-not-found
      :414 handle-not-found)))


(defn- attachment?
  [params]
  (if (= "1" (:attachment params "1")) true false))

(defn do-special-download
  [{user :user path :path :as params}]
  (download-file user path (attachment? params)))

(with-pre-hook! #'do-special-download
  (fn [params]
    (log-call "do-special-download" params)
    (validate-map params {:user string? :path string?})
    (let [user (:user params)
          path (:path params)]
      (log/info "User for download: " user)
      (log/info "Path to download: " path)

      (when (super-user? user)
        (throw+ {:error_code error/ERR_NOT_AUTHORIZED :user user})))))

(with-post-hook! #'do-special-download (log-func "do-special-download"))
