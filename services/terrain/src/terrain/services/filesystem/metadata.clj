(ns terrain.services.filesystem.metadata
  (:use [clojure-commons.error-codes]
        [clojure-commons.validators]
        [terrain.services.filesystem.common-paths]
        [terrain.services.filesystem.validators]
        [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [clojure.walk :as walk]
            [clojure-commons.file-utils :as ft]
            [cemerick.url :as url]
            [cheshire.core :as json]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [terrain.clients.metadata.raw :as metadata-client]
            [terrain.clients.data-info :as data]
            [terrain.clients.data-info.raw :as data-raw]
            [terrain.services.filesystem.icat :as icat]
            [terrain.services.filesystem.validators :as validators]
            [terrain.util.config :as cfg]
            [terrain.util.service :as service]))

(defn- service-response->json
  [response]
  (->> response :body service/decode-json))

(defn- find-attributes
  [attrs user uuid]
  (let [{:keys [irods-avus path]} (data/get-metadata-json user uuid)
        matching-avus (filter #(contains? attrs (:attr %)) irods-avus)]
    (if-not (empty? matching-avus)
      {:path path
       :avus matching-avus}
      nil)))

(defn- validate-batch-add-attrs
  "Throws an error if any of the given paths already have metadata set with any of the given attrs."
  [user uuids attrs]
  (let [duplicates (remove nil? (map (partial find-attributes attrs user) uuids))]
    (when-not (empty? duplicates)
      (validators/duplicate-attrs-error duplicates))))

(defn do-metadata-get
  "Entrypoint for the API."
  [{user :user} data-id]
  (data-raw/get-avus user (uuidify data-id)))

(with-pre-hook! #'do-metadata-get
  (fn [params data-id]
    (log-call "do-metadata-get" data-id params)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-get (log-func "do-metadata-get"))

(defn do-metadata-set
  "Entrypoint for the API that calls (metadata-set).
   Body is a map with :irods-avus and :metadata keys."
  [data-id {user :user} body]
  (data-raw/set-avus user (uuidify data-id) body))

(with-pre-hook! #'do-metadata-set
  (fn [data-id params body]
    (log-call "do-metadata-set" data-id params body)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-set (log-func "do-metadata-set"))

(defn do-metadata-copy
  "Entrypoint for the API that calls (metadata-copy)."
  [{:keys [user]} data-id force body]
  (data-raw/metadata-copy user force data-id body))

(with-pre-hook! #'do-metadata-copy
  (fn [{:keys [user] :as params} data-id force {dest-ids :destination_ids :as body}]
    (log-call "do-metadata-copy" params data-id body)
    (validate-map params {:user string?})
    (validate-map body {:destination_ids sequential?})
    (validate-num-paths dest-ids)))

(with-post-hook! #'do-metadata-copy (log-func "do-metadata-copy"))

(defn do-metadata-save
  "Forwards request to data-info service."
  [data-id params body]
  (data-raw/save-metadata (:user params) data-id (:dest body) (:recursive body)))

(with-pre-hook! #'do-metadata-save
  (fn [data-id params body]
    (log-call "do-metadata-save" data-id params body)
    (validate-map params {:user string?})))

(with-post-hook! #'do-metadata-save (log-func "do-metadata-save"))

;; FIXME: The logic coordinating data-info endpoints with the metadata service should be migrated down into data-info
(defn- bulk-add-file-avus
  "Applies metadata from a list of attributes and values to the given path.
   If an AVU's attribute is found in the given template-attrs map, then that AVU is stored in the
   metadata db; all other AVUs are stored in IRODS."
  [user template-attrs attrs [path path-info] values]
  (let [avus (map (partial zipmap [:attr :value :unit]) (map vector attrs values (repeat "")))
        template-attr? #(contains? template-attrs (:attr %))
        [template-avus irods-avus] ((juxt filter remove) template-attr? avus)
        metadata {:metadata   {:avus template-avus}
                  :irods-avus irods-avus}]
    (when-not (and (empty? template-avus) (empty? irods-avus))
      (data-raw/add-avus user (get path-info "id") metadata))
    (merge {:path path} metadata)))

(defn- parse-template-attrs
  "Fetches Metadata Template attributes from the metadata service if given a template-id UUID.
   Returns a map with the attribute names as keys, or nil."
  [template-id]
  (when template-id
    (->> (metadata-client/get-template template-id)
         :body
         service/decode-json
         :attributes
         (group-by :name))))

(defn- format-csv-metadata-filename
  [dest-dir ^String filename]
  (ft/rm-last-slash
    (if (.startsWith filename "/")
      filename
      (ft/path-join dest-dir filename))))

(defn- bulk-add-avus
  "Applies metadata from a list of attributes and filename/values to those files found under
   dest-dir."
  [user dest-dir force? template-attrs attrs csv-filename-values]
  (let [format-path (partial format-csv-metadata-filename dest-dir)
        paths (map (comp format-path first) csv-filename-values)
        path-info-map (-> (data-raw/collect-stats user :paths paths :validation-behavior "write") :body json/decode (get "paths"))
        value-lists (map rest csv-filename-values)
        irods-attrs (clojure.set/difference (set attrs) (set (keys template-attrs)))]
    (if-not force?
      (validate-batch-add-attrs user (map #(get-in path-info-map [% "id"]) paths) irods-attrs))
  (mapv (partial bulk-add-file-avus user template-attrs attrs)
    path-info-map value-lists)))

(defn- keyword-to-int [kw] (Integer/parseInt (name kw)))

(defn- deparse-csv-line
  [line]
  (mapv second (into (sorted-map-by #(< (keyword-to-int %1) (keyword-to-int %2))) line)))

(defn- get-csv
  [user src separator]
  (let [path-uuid (data/uuid-for-path user src)
        chunk-size 1048576 ;; This corresponds to the largest size allowed by the UI's viewer, 1024KB.
                           ;; It could probably be larger without issue, but this is the expected largest
                           ;; value for the underlying endpoint in practice. Ideally, it'll never matter
                           ;; and everything will fit in one page.
        get-page  (fn [page] (service-response->json (data-raw/read-tabular-chunk user path-uuid separator page chunk-size)))]
    (loop [page 1 max-pages nil csv []]
      (let [{max-pages :number-pages new-csv :csv :as res} (get-page page)
            csv (concat csv (map deparse-csv-line (remove empty? new-csv)))]
        (if (< page (Integer/parseInt max-pages))
            (recur (+ page 1) (Integer/parseInt max-pages) csv)
            csv)))))

(defn- parse-metadata-csv
  "Parses filenames and metadata to apply from a CSV file input stream.
   If a template-id is provided, then AVUs with template attributes are stored in the metadata db,
   and all other AVUs are stored in IRODS."
  [user dest-dir force? template-id ^String separator src]
  (let [csv (get-csv user src separator)
        attrs (-> csv first rest)
        csv-filename-values (rest csv)
        template-attrs (parse-template-attrs template-id)]
    {:path-metadata
     (bulk-add-avus user dest-dir force? template-attrs attrs csv-filename-values)}))

(defn parse-metadata-csv-file
  "Parses filenames and metadata to apply from a source CSV file in the data store"
  [{:keys [user]} {:keys [src dest force template-id separator] :or {separator "%2C"}}]
  (service/success-response
    (parse-metadata-csv user dest
      (Boolean/parseBoolean force)
      (uuidify template-id)
      separator
      src)))

(with-pre-hook! #'parse-metadata-csv-file
  (fn [user-info params]
    (log-call "parse-metadata-csv-file" user-info params)
    (validate-map user-info {:user string?})
    (validate-map params {:src string?
                          :dest string?})))

(with-post-hook! #'parse-metadata-csv-file (log-func "parse-metadata-csv-file"))
