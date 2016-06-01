(ns metadata-client.core
  (:use [kameleon.uuids :only [uuidify]])
  (:require [cemerick.url :as curl]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [clojure.tools.logging :as log]))

(def ^:dynamic *metadata-base*
  "Dynamic context to be used in generating URLs."
  "http://localhost:60000")

(defmacro with-metadata-base
  "A helper macro to change *metadata-base* within its body."
  [metadata-base & body]
  `(let [metadata-base# ~metadata-base]
     (binding [*metadata-base* metadata-base#]
       ~@body)))

(defn- metadata-url-encoded
  [& components]
  (log/debug "using metadata base" *metadata-base*)
  (str (apply curl/url *metadata-base* (map curl/url-encode components))))

(defn- get-options
  [params & {:keys [as] :or {as :stream}}]
  {:query-params     params
   :as               as
   :follow-redirects false})

(defn- post-options
  [body params & {:keys [as] :or {as :stream}}]
  {:query-params     params
   :body             body
   :content-type     :json
   :as               as
   :follow-redirects false})

(def ^:private put-options post-options)

(defn list-ontologies
  [username]
  (-> (http/get (metadata-url-encoded "ontologies")
                (get-options {:user username} :as :json))
      :body))

(defn list-hierarchies
  [username ontology-version]
  (http/get (metadata-url-encoded "ontologies" ontology-version)
            (get-options {:user username})))

(defn filter-hierarchy
  [username ontology-version root-iri attr target-types target-ids]
  (http/post (metadata-url-encoded "ontologies" ontology-version root-iri "filter")
             (post-options (json/encode {:target-types target-types :target-ids target-ids})
                           {:user username :attr attr})))

(defn filter-by-attr-value
  [username attr value target-types target-ids]
  (->> (http/post (metadata-url-encoded "avus" "filter-targets")
                  (post-options (json/encode {:target-types target-types :target-ids target-ids})
                                {:user  username
                                 :attr  attr
                                 :value value}
                                :as :json))
       :body
       :target-ids
       (map uuidify)))

(defn filter-unclassified
  [username ontology-version root-iri attr target-types target-ids]
  (->> (http/post (metadata-url-encoded "ontologies" ontology-version root-iri "filter-unclassified")
                  (post-options (json/encode {:target-types target-types :target-ids target-ids})
                                {:user username :attr attr}
                                :as :json))
       :body
       :target-ids
       (map uuidify)))

(defn list-avus
  [username target-type target-id & {:keys [as] :or {as :stream}}]
  (http/get (metadata-url-encoded "avus" target-type target-id)
            (get-options {:user username} :as as)))

(defn set-avus
  [username target-type target-id body]
  (http/put (metadata-url-encoded "avus" target-type target-id)
            (put-options body {:user username})))

(defn update-avus
  [username target-type target-id body]
  (http/post (metadata-url-encoded "avus" target-type target-id)
             (post-options body {:user username})))

(defn copy-metadata-avus
  [username target-type target-id dest-items]
  (http/post (metadata-url-encoded "avus" target-type target-id "copy")
             (post-options (json/encode {:targets dest-items}) {:user username})))
