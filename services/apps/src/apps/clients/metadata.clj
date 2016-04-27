(ns apps.clients.metadata
  (:use [kameleon.uuids :only [uuidify]])
  (:require [cemerick.url :as curl]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [apps.util.config :as config]
            [apps.util.service :as util]))

(defn- metadata-url-encoded
  [& components]
  (str (apply curl/url (config/metadata-base) (map curl/url-encode components))))

(defn- get-options
  [params]
  {:query-params     params
   :as               :stream
   :follow-redirects false})

(defn- post-options
  [body params]
  {:query-params     params
   :body             body
   :content-type     :json
   :as               :stream
   :follow-redirects false})

(defn list-ontologies
  [username]
  (-> (http/get (metadata-url-encoded "ontologies")
                (get-options {:user username}))
      :body
      util/parse-json))

(defn list-hierarchies
  [username ontology-version]
  (http/get (metadata-url-encoded "ontologies" ontology-version)
            (get-options {:user username})))

(defn filter-hierarchy
  [username ontology-version root-iri app-ids]
  (http/post (metadata-url-encoded "ontologies" ontology-version root-iri "filter")
             (post-options (json/encode {:target-ids app-ids :target-types ["app"]})
                           {:user username})))

(defn filter-by-attr-value
  [username attr value app-ids]
  (->> (http/post (metadata-url-encoded "avus" "filter-targets")
                  (post-options (json/encode {:target-ids app-ids
                                              :target-types ["app"]
                                              :attr attr
                                              :value value})
                                {:user username}))
       :body
       util/parse-json
       :target-ids
       (map uuidify)))

(defn filter-unclassified
  [username ontology-version root-iri app-ids]
  (->> (http/post (metadata-url-encoded "ontologies" ontology-version root-iri "filter-unclassified")
                  (post-options (json/encode {:target-ids app-ids :target-types ["app"]})
                                {:user username}))
       :body
       util/parse-json
       :target-ids
       (map uuidify)))
