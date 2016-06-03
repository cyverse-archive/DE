(ns data-info.services.metadata
  (:use [clojure-commons.error-codes]
        [clojure-commons.validators]
        [clj-jargon.init :only [with-jargon]]
        [clj-jargon.item-ops :only [copy-stream]]
        [clj-jargon.metadata]
        [kameleon.uuids :only [uuidify]]
        [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure.tools.logging :as log]
            [clojure.string :as string]
            [clojure.set :as s]
            [clojure-commons.file-utils :as ft]
            [cheshire.core :as json]
            [dire.core :refer [with-pre-hook! with-post-hook!]]
            [data-info.services.directory :as directory]
            [data-info.services.stat :as stat]
            [data-info.services.uuids :as uuids]
            [data-info.util.config :as cfg]
            [data-info.util.logging :as dul]
            [data-info.util.paths :as paths]
            [data-info.util.validators :as validators]
            [metadata-client.core :as metadata]))


(defn- fix-unit
  "Used to replace the IPCRESERVED unit with an empty string."
  [avu]
  (if (= (:unit avu) paths/IPCRESERVED)
    (assoc avu :unit "")
    avu))

(def ^:private ipc-regex #"(?i)^ipc")

(defn- ipc-avu?
  "Returns a truthy value if the AVU map passed in is reserved for the DE's use."
  [avu]
  (re-find ipc-regex (:attr avu)))

(defn- authorized-avus
  "Validation to make sure the AVUs aren't system AVUs. Throws a slingshot error
   map if the validation fails."
  [avus]
  (when (some ipc-avu? avus)
    (throw+ {:error_code ERR_NOT_AUTHORIZED
             :avus avus})))

(defn- list-path-metadata
  "Returns the metadata for a path. Passes all AVUs to (fix-unit).
   AVUs with a unit matching IPCSYSTEM are filtered out."
  [cm path & {:keys [system] :or {system false}}]
  (let [fixed-metadata (map fix-unit (get-metadata cm (ft/rm-last-slash path)))]
  (if system
      fixed-metadata
      (remove ipc-avu? fixed-metadata))))

(defn- reserved-unit
  "Turns a blank unit into a reserved unit."
  [avu-map]
  (if (string/blank? (:unit avu-map))
    paths/IPCRESERVED
    (:unit avu-map)))

(defn- resolve-data-type
  "Returns a type converted from the type field of a stat result to a type expected by the
   metadata service endpoints."
  [type]
  (let [type (name type)]
    (if (= type "dir")
      "folder"
      type)))

(defn- get-readable-data-item
  [cm user data-id]
  (let [{:keys [path] :as data-item} (uuids/path-for-uuid cm user data-id)]
    (validators/path-readable cm user path)
    data-item))

(defn metadata-get
  "Returns the metadata for a path. Filters out system AVUs
   if :system true is not passed to it, and replaces
   units set to ipc-reserved with an empty string."
  [user data-id & {:keys [system] :or {system false}}]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (let [{:keys [path type]} (get-readable-data-item cm user data-id)
          metadata-response   (metadata/list-avus user (resolve-data-type type) data-id :as :json)]
      (merge (:body metadata-response)
             {:irods-avus (list-path-metadata cm path :system system)
              :path       path}))))

(defn admin-metadata-get
  "Lists metadata for a path, showing all AVUs."
  [data-id]
  (metadata-get (cfg/irods-user) data-id :system true))

(defn- common-metadata-add
  "Adds an AVU to 'path'. The AVU is passed in as a map in the format:
   {
      :attr attr-string
      :value value-string
      :unit unit-string
   }
   It's a no-op if an AVU with the same attribute and value is already
   associated with the path."
  [cm path avu-map]
  (let [fixed-path (ft/rm-last-slash path)
        new-unit   (reserved-unit avu-map)
        attr       (:attr avu-map)
        value      (:value avu-map)]
    (log/debug "Fixed Path:" fixed-path)
    (log/debug "check" (true? (attr-value? cm fixed-path attr value)))
    (when-not (attr-value? cm fixed-path attr value)
      (log/debug "Adding " attr value "to" fixed-path)
      (add-metadata cm fixed-path attr value new-unit))
    fixed-path))

(defn- common-metadata-delete
  "Removes an AVU from 'path'. The AVU is passed somewhat confusingly
   as a map of attr and value:
   {
      :attr attr-string
      :value value-string
   }
   It's a no-op if no AVU with that attribute and value is associated
   with the path."
   [cm path avu-map]
  (let [fixed-path (ft/rm-last-slash path)
        attr       (:attr avu-map)
        value      (:value avu-map)]
    (log/debug "Fixed Path:" fixed-path)
    (log/debug "check" (true? (attr-value? cm fixed-path attr value)))
    (when (attr-value? cm fixed-path attr value)
      (log/debug "Removing " attr value "from" fixed-path)
      (delete-metadata cm fixed-path attr value))
    fixed-path))

(defn metadata-add
  "Allows user to set metadata on a path. The user must exist in iRODS
   and have write permissions on the path. The path must exist. The
   irods-avus parameter must be a list of objects in this format:
   {
      :attr attr-string
      :value value-string
      :unit unit-string
   }

   The 'metadata' parameter must be in a format expected by the metadata service add AVUs endpoint,
   in addition to the 'irods-avus' key.
   Pass :system true to ignore restrictions on AVUs which may be added."
  [user data-id {:keys [irods-avus] :as metadata} & {:keys [system] :or {system false}}]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (let [{:keys [path type]} (get-readable-data-item cm user data-id)
          path (ft/rm-last-slash path)
          metadata (dissoc metadata :irods-avus)]
      (validators/path-writeable cm user path)
      (when-not system (authorized-avus irods-avus))
      (when-not (empty? metadata)
        (metadata/update-avus user (resolve-data-type type) data-id (json/encode metadata)))
      (doseq [avu-map irods-avus]
        (common-metadata-add cm path avu-map))
      {:path path
       :user user})))

(defn admin-metadata-add
  "Adds AVUs to path, bypassing user permission checks. See (metadata-add)
   for the AVU map format."
  [data-id body]
  (metadata-add (cfg/irods-user) data-id body :system true))

(defn metadata-set
  "Allows user to set metadata on an item with the given data-id.
   The user must exist in iRODS and have write permissions on the data item.
   The 'metadata' parameter should be in a format expected by the metadata service set AVUs endpoint,
   with an 'irods-avus' key following the format used for (metadata-add)."
  [user data-id {:keys [irods-avus] :as metadata}]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (let [{:keys [path type]} (uuids/path-for-uuid cm user data-id)
          irods-avus (set (map #(select-keys % [:attr :value :unit]) irods-avus))
          current-avus (set (list-path-metadata cm path :system false))
          delete-irods-avus (s/difference current-avus irods-avus)
          metadata-request (json/encode (dissoc metadata :irods-avus))]
      (validators/path-writeable cm user path)
      (authorized-avus irods-avus)

      (metadata/set-avus user (resolve-data-type type) data-id metadata-request)
      (doseq [del-avu delete-irods-avus]
        (common-metadata-delete cm path del-avu))
      (doseq [avu irods-avus]
        (common-metadata-add cm path avu))

      {:path path
       :user user})))

(defn- find-attributes
  [cm attrs {:keys [path]}]
  (let [irods-avus (list-path-metadata cm path)
        matching-avus (filter #(contains? attrs (:attr %)) irods-avus)]
    (if-not (empty? matching-avus)
      {:path path
       :avus matching-avus}
      nil)))

(defn- validate-batch-add-attrs
  "Throws an error if any of the given paths already have metadata set with any of the attrs found in the
  irods-avus list."
  [cm irods-avus dest-items]
  (let [attrs      (set (map :attr irods-avus))
        duplicates (remove nil? (map (partial find-attributes cm attrs) dest-items))]
    (when-not (empty? duplicates)
      (throw+ {:error_code :clojure-commons.exception/not-unique
               :message    "Some paths already have metadata with some of the given attributes."
               :duplicates duplicates}))))

(defn- format-copy-dest-item
  [{:keys [id type]}]
  {:id   id
   :type (resolve-data-type type)})

(defn- get-writable-data-items
  [cm user data-ids]
  (let [data-items (map (partial stat/uuid-stat cm user) data-ids)
        paths (map :path data-items)]
    (validators/all-paths-writeable cm user paths)
    data-items))

(defn metadata-copy
  "Copies all IRODS AVUs visible to the client, and Metadata AVUs, from the data item with
   src-id to the items with dest-ids. When the 'force?' parameter is false or not set, additional
   validation is performed."
  [user force? src-id dest-ids]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (let [{:keys [path type]} (get-readable-data-item cm user src-id)
          dest-items (get-writable-data-items cm user dest-ids)
          dest-paths (map :path dest-items)
          dest-ids (map :id dest-items)
          irods-avus (list-path-metadata cm path)]
      (if-not force?
        (validate-batch-add-attrs cm irods-avus dest-items))
      (metadata/copy-metadata-avus user
                                   (resolve-data-type type)
                                   src-id
                                   (map format-copy-dest-item dest-items))
      (doseq [dest-id dest-ids]
        (metadata-add user dest-id {:irods-avus irods-avus}))
      {:user  user
       :src   path
       :paths dest-paths})))

(defn- stat-is-dir?
  [{:keys [type]}]
  (= :dir type))

(defn- segregate-files-from-folders
  "Takes a list of path-stat results and splits the folders and files into a map with :folders and
   :files keys, with the segregated lists as values."
  [folder-children]
  (zipmap [:folders :files]
    ((juxt filter remove) stat-is-dir? folder-children)))

(defn- get-data-item-metadata-for-save
  "Adds a :metadata key to the given data-item, with a list of all IRODS and Template AVUs together
   as the key's value. If recursive? is true and data-item is a folder, then includes all files and
   subfolders (plus all their files and subfolders) with their metadata in the resulting stat map."
  [cm user recursive? {:keys [id path type] :as data-item}]
  (let [irods-metadata (list-path-metadata cm path)
        metadata-avus (-> (metadata/list-avus user (resolve-data-type type) (uuidify id) :as :json)
                          :body
                          :avus)
        data-item (assoc data-item :metadata (concat irods-metadata metadata-avus))
        path->metadata (comp (partial get-data-item-metadata-for-save cm user recursive?)
                             (partial stat/path-stat cm user))]
    (if (and recursive? (stat-is-dir? data-item))
      (merge data-item
             (segregate-files-from-folders
               (map path->metadata (directory/get-paths-in-folder user path))))
      data-item)))

(defn- build-metadata-for-save
  [cm user data-item recursive?]
  (-> (get-data-item-metadata-for-save cm user recursive? data-item)
      (dissoc :uuid)
      (json/encode {:pretty true})))

(defn- metadata-save
  "Allows a user to export metadata from a file or folder with the given data-id to a file specified
   by dest."
  [user data-id dest recursive?]
  (with-jargon (cfg/jargon-cfg) [cm]
    (validators/user-exists cm user)
    (let [dest-dir (ft/dirname dest)
          src-data (uuids/path-for-uuid cm user data-id)
          src-path (:path src-data)]
      (validators/path-readable cm user src-path)
      (validators/path-exists cm dest-dir)
      (validators/path-writeable cm user dest-dir)
      (validators/path-not-exists cm dest)
      (when recursive?
        (validators/validate-num-paths-under-folder user src-path))

      (with-in-str (build-metadata-for-save cm user src-data recursive?)
        {:file (stat/decorate-stat cm user (copy-stream cm *in* user dest))}))))

(defn do-metadata-save
  "Entrypoint for the API. Calls (metadata-save)."
  [data-id {:keys [user]} {:keys [dest recursive]}]
  (metadata-save user (uuidify data-id) (ft/rm-last-slash dest) (boolean recursive)))
