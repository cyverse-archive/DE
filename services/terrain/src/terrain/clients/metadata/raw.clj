(ns terrain.clients.metadata.raw
  (:use [clojure-commons.core :only [remove-nil-values]]
        [terrain.util.transformers :only [user-params]])
  (:require [cemerick.url :as curl]
            [cheshire.core :as json]
            [clj-http.client :as http]
            [terrain.util.config :as config]))

(defn- metadata-url
  [& components]
  (str (apply curl/url (config/metadata-base) components)))

(defn- metadata-url-encoded
  [& components]
  (str (apply curl/url (config/metadata-base) (map curl/url-encode components))))

(defn resolve-data-type
  "Returns a type converted from the type field of a stat result to a type expected by the
   metadata service endpoints."
  [type]
  (let [type (name type)]
    (if (= type "dir")
    "folder"
    type)))

(defn get-options
  ([]
     (get-options {}))
  ([params]
     {:query-params     (user-params params)
      :as               :stream
      :follow-redirects false})
  ([params param-keys]
    (get-options (user-params params param-keys))))

(def delete-options get-options)

(defn post-options
  ([body]
     (post-options body {}))
  ([body params]
     {:query-params     (user-params params)
      :body             body
      :content-type     :json
      :as               :stream
      :follow-redirects false}))

(def put-options post-options)

(defn add-metadata-avus
  [target-type target-id avus-req]
  (http/post (metadata-url "avus" target-type target-id)
             (post-options (json/encode avus-req))))

(defn list-data-comments
  [target-id]
  (http/get (metadata-url "filesystem" "data" target-id "comments")
            {:as               :stream
             :follow_redirects false}))

(defn list-app-comments
  [target-id]
  (http/get (metadata-url "apps" target-id "comments")
            {:as               :stream
             :follow_redirects false}))

(defn add-data-comment
  [target-id data-type body]
  (http/post (metadata-url "filesystem" "data" target-id "comments")
             (post-options body {:data-type data-type})))

(defn add-app-comment
  [target-id body]
  (http/post (metadata-url "apps" target-id "comments") (post-options body)))

(defn update-data-retract-status
  [target-id comment-id retracted]
  (http/patch (metadata-url "filesystem" "data" target-id "comments" comment-id)
              (post-options nil {:retracted retracted})))

(defn update-app-retract-status
  [target-id comment-id retracted]
  (http/patch (metadata-url "apps" target-id "comments" comment-id)
              (post-options nil {:retracted retracted})))

(defn admin-update-data-retract-status
  [target-id comment-id retracted]
  (http/patch (metadata-url "admin" "filesystem" "data" target-id "comments" comment-id)
    (post-options nil {:retracted retracted})))

(defn admin-update-app-retract-status
  [target-id comment-id retracted]
  (http/patch (metadata-url "admin" "apps" target-id "comments" comment-id)
    (post-options nil {:retracted retracted})))

(defn delete-data-comment
  [target-id comment-id]
  (http/delete (metadata-url "admin" "filesystem" "data" target-id "comments" comment-id)
    (delete-options)))

(defn delete-app-comment
  [target-id comment-id]
  (http/delete (metadata-url "admin" "apps" target-id "comments" comment-id) (delete-options)))

(defn list-favorites
  [entity-type]
  (http/get (metadata-url "favorites" "filesystem") (get-options {:entity-type entity-type})))

(defn remove-favorite
  [target-id]
  (http/delete (metadata-url "favorites" "filesystem" target-id) (delete-options)))

(defn add-favorite
  [target-id data-type]
  (http/put (metadata-url "favorites" "filesystem" target-id)
            (post-options nil {:data-type data-type})))

(defn filter-favorites
  [uuids]
  (http/post (metadata-url "favorites" "filter") (post-options (json/encode {:filesystem uuids}))))

(defn list-attached-tags
  [target-id]
  (http/get (metadata-url "filesystem" "data" target-id "tags") (get-options)))

(defn update-attached-tags
  [target-id data-type type body]
  (http/patch (metadata-url "filesystem" "data" target-id "tags")
              (post-options body {:data-type data-type
                                  :type type})))

(defn get-tags-by-value
  [contains limit]
  (http/get (metadata-url "tags" "suggestions") (get-options (remove-nil-values {:contains contains
                                                                                 :limit limit}))))

(defn create-user-tag
  [body]
  (http/post (metadata-url "tags" "user") (post-options body)))

(defn update-user-tag
  [tag-id body]
  (http/patch (metadata-url "tags" "user" tag-id) (post-options body)))

(defn delete-user-tag
  [tag-id]
  (http/delete (metadata-url "tags" "user" tag-id) (delete-options)))

(defn list-templates
  []
  (http/get (metadata-url "templates") (get-options)))

(defn get-template
  [template-id]
  (http/get (metadata-url "templates" template-id) (get-options)))

(defn get-template-csv
  [template-id]
  (http/get (metadata-url "templates" template-id "blank-csv") (get-options)))

(defn get-template-guide
  [template-id]
  (http/get (metadata-url "templates" template-id "guide-csv") (get-options)))

(defn get-attribute
  [attr-id]
  (http/get (metadata-url "templates" "attr" attr-id) (get-options)))

(defn admin-list-templates
  []
  (http/get (metadata-url "admin" "templates") (get-options)))

(defn admin-add-template
  [template]
  (http/post (metadata-url "admin" "templates") (post-options template)))

(defn admin-update-template
  [template-id template]
  (http/put (metadata-url "admin" "templates" template-id)
            (put-options template)))

(defn admin-delete-template
  [template-id]
  (http/delete (metadata-url "admin" "templates" template-id) (delete-options)))

(defn get-ontology-hierarchies
  [ontology-version]
  (http/get (metadata-url-encoded "ontologies" ontology-version) (get-options)))

(defn upload-ontology
  [filename content-type istream]
  (http/post (metadata-url "admin" "ontologies")
             {:query-params     (user-params {})
              :multipart        [{:part-name "ontology-xml"
                                  :name      filename
                                  :mime-type content-type
                                  :content   istream}]
              :follow-redirects false}))

(defn delete-app-category-hierarchy
  [ontology-version root-iri]
  (http/delete (metadata-url-encoded "admin" "ontologies" ontology-version root-iri) (delete-options)))

(defn save-ontology-hierarchy
  [ontology-version root-iri]
  (http/put (metadata-url-encoded "admin" "ontologies" ontology-version root-iri) (get-options)))

(defn list-permanent-id-requests
  [params]
  (http/get (metadata-url "permanent-id-requests") (get-options params [:statuses
                                                                        :limit
                                                                        :offset
                                                                        :sort-field
                                                                        :sort-dir])))

(defn create-permanent-id-request
  [request]
  (http/post (metadata-url "permanent-id-requests") (post-options request)))

(defn list-permanent-id-request-status-codes
  []
  (http/get (metadata-url "permanent-id-requests" "status-codes") (get-options)))

(defn list-permanent-id-request-types
  []
  (http/get (metadata-url "permanent-id-requests" "types") (get-options)))

(defn get-permanent-id-request
  [request-id]
  (http/get (metadata-url "permanent-id-requests" request-id) (get-options)))

(defn admin-list-permanent-id-requests
  [params]
  (http/get (metadata-url "admin" "permanent-id-requests") (get-options params [:statuses
                                                                                :limit
                                                                                :offset
                                                                                :sort-field
                                                                                :sort-dir])))

(defn admin-get-permanent-id-request
  [request-id]
  (http/get (metadata-url "admin" "permanent-id-requests" request-id) (get-options)))

(defn update-permanent-id-request
  [request-id request]
  (http/post (metadata-url "admin" "permanent-id-requests" request-id "status") (post-options request)))
