(ns data-info.clients.metadata
  (:require [cemerick.url :as curl]
            [clj-http.client :as http]
            [data-info.util.config :as config]))

(defn- metadata-url
  [& components]
  (str (apply curl/url (config/metadata-base-url) components)))

(defn- resolve-data-type
  "Returns a type converted from the type field of a stat result to a type expected by the
   metadata service endpoints."
  [type]
  (let [type (name type)]
    (if (= type "dir")
      "folder"
      type)))

(defn list-metadata-avus
  [username target-type target-id]
  (http/get (metadata-url "avus" (resolve-data-type target-type) target-id)
    {:query-params     {:user username}
     :as               :json
     :follow_redirects false}))

(defn get-metadata-avus
  [username target-type target-id]
  (:body (list-metadata-avus username target-type target-id)))
