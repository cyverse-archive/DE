(ns apps.clients.data-info
  (:use [medley.core :only [map-kv]])
  (:require [cemerick.url :as curl]
            [cheshire.core :as cheshire]
            [clj-http.client :as http]
            [clojure.string :as string]
            [apps.util.config :as config]
            [apps.util.service :as service]))

(defn- secured-params
  [user]
  {:user (:shortUsername user)})

(defn- data-info-url
  [& components]
  (str (apply curl/url (config/data-info-base-url)
              (map #(string/replace % #"^/+|/+$" "") components))))

;; currently doesn't support passing UUIDs, but could be changed to
(defn get-file-stats
  [user paths]
  (when (seq paths)
    (:body
     (http/post (data-info-url "stat-gatherer")
                {:query-params (secured-params user)
                 :body         (cheshire/encode {:paths paths})
                 :content-type :json
                 :as           :json}))))

(defn get-data-ids
  [user paths]
  (->> (get-file-stats user paths)
       :paths
       (map-kv (fn [k v] [k (:id v)]))))

(defn get-data-id
  [user path]
  ((keyword path) (get-data-ids user [path])))

(defn get-paths-exist
  [user paths]
  (when (seq paths)
    (:body
     (http/post (data-info-url "existence-marker")
                {:query-params (secured-params user)
                 :body         (cheshire/encode {:paths paths})
                 :content-type :json
                 :as           :json}))))

(defn get-file-contents
  [user path]
  (:body
   (http/get (data-info-url "data" "path" path)
             {:query-params (secured-params user)
              :as           :stream})))

(defn get-path-list-contents
  [user path]
  (->> (slurp (get-file-contents user path))
       (string/split-lines)
       (remove empty?)
       (drop 1)))

(defn create-directory
  [user path]
  (http/post (data-info-url "data" "directories")
             {:query-params (secured-params user)
              :body         (cheshire/encode {:paths [path]})
              :content-type :json
              :as           :stream}))

(defn share-data-item
  [user data-id share-with permission]
  (:body
   (http/put (data-info-url "data" data-id "permissions" share-with permission)
             {:query-params (secured-params user)
              :as           :json})))

(defn share-path
  [user path share-with permission]
  (share-data-item user (get-data-id user path) share-with permission))

(defn unshare-data-item
  [user data-id unshare-with]
  (:body
   (http/delete (data-info-url "data" data-id "permissions" unshare-with)
                {:query-params (secured-params user)
                 :as           :json})))

(defn unshare-path
  [user path unshare-with]
  (unshare-data-item user (get-data-id user path) unshare-with))
