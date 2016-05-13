(ns metadata.persistence.avu
  (:use [korma.core :exclude [update]]
        [korma.db :only [transaction]]
        [slingshot.slingshot :only [throw+]])
  (:require [kameleon.db :as db]
            [korma.core :as sql]))

(def ^:private data-types [(db/->enum-val "file") (db/->enum-val "folder")])

(defn filter-targets-by-attr-values
  "Finds the given targets that have the given attribute and any of the given values."
  [target-types target-ids attribute values]
  (select :avus
          (modifier "DISTINCT")
          (fields :target_id
                  :target_type)
          (where {:target_id   [in target-ids]
                  :target_type [in (map db/->enum-val target-types)]
                  :attribute   attribute
                  :value       [in values]})))

(defn get-avu-by-id
  "Fetches an AVU by its ID."
  [id]
  (first (select :avus (where {:id id}))))

(defn get-avus-by-ids
  "Finds existing AVUs by a set of IDs."
  [ids]
  (select :avus (where {:id [in ids]})))

(defn get-avus-for-target
  "Gets AVUs for the given target."
  [target-type target-id]
  (select :avus
          (where {:target_id   target-id
                  :target_type (db/->enum-val target-type)})))

(defn get-avus-by-attr
  "Finds all existing AVUs by the given targets and the given set of attributes."
  [target-types target-ids attribute]
  (select :avus
          (where {:attribute   attribute
                  :target_id   [in target-ids]
                  :target_type [in (map db/->enum-val target-types)]})))

(defn get-avu-for-target
  "Finds an AVU by ID, validating its target_type and target_id match what's fetched from the database.
   Returns nil if an AVU with the given ID does not already exist in the database."
  [{:keys [id] :as avu}]
  (when-let [existing-avu (when id (get-avu-by-id id))]
    (when (not= (select-keys existing-avu [:target_type :target_id])
                (select-keys avu          [:target_type :target_id]))
      (throw+ {:type  :clojure-commons.exception/exists
               :error "AVU already attached to another target item."
               :avu   existing-avu}))
    existing-avu))

(defn add-avus
  "Adds the given AVUs to the Metadata database."
  [user-id avus]
  (let [fmt-avu (comp #(update % :target_type db/->enum-val)
                      #(assoc % :created_by  user-id
                                :modified_by user-id))]
    (insert :avus (values (map fmt-avu avus)))))

(defn update-avu
  "Updates the attribute, value, unit, modified_by, and modified_on fields of the given AVU."
  [user-id avu]
  (sql/update :avus
    (set-fields (-> (select-keys avu [:attribute :value :unit])
                    (assoc :modified_by user-id
                           :modified_on (sqlfn now))))
    (where (select-keys avu [:id]))))

(defn- update-valid-avu
  "Updates an AVU, validating its given target_type and target_id match what's already in the database.
   Returns nil if an AVU with the given ID does not already exist in the database."
  [user-id avu]
  (when (get-avu-for-target avu)
    (update-avu user-id avu)
    avu))

(defn- find-matching-avu
  "Finds an existing AVU by attribute, value, unit, target_type, target_id, and (if included) its ID."
  [avu]
  (let [required-keys [:attribute :value :unit :target_type :target_id]]
    (when (every? (partial contains? avu) required-keys)
      (first (select :avus
                     (where (-> (select-keys avu (conj required-keys :id))
                                (update :target_type db/->enum-val))))))))

(defn add-or-update-avu
  [user-id avu]
  (if-let [matching-avu (find-matching-avu avu)]
    matching-avu
    (if-let [existing-avu (update-valid-avu user-id avu)]
      existing-avu
      (add-avus user-id [avu]))))

(defn remove-avus
  "Removes AVUs with the given IDs from the Metadata database."
  [avu-ids]
  (delete :avus (where {:id [in avu-ids]})))

(defn remove-avu
  "Removes the AVU with the given ID from the Metadata database."
  [avu-id]
  (delete :avus (where {:id avu-id})))

(defn remove-orphaned-avus
  "Removes AVUs for the given target-id that are not in the given set of avu-ids."
  [target-type target-id avu-ids]
  (let [query (-> (delete* :avus)
                  (where {:target_id target-id
                          :target_type (db/->enum-val target-type)}))]
    (if (empty? avu-ids)
      (delete query)
      (delete (where query {:id [not-in avu-ids]})))))

(defn format-avu
  "Formats a Metadata AVU for JSON responses."
  [avu]
  (let [convert-timestamp #(assoc %1 %2 (db/millis-from-timestamp (%2 %1)))]
    (-> avu
      (convert-timestamp :created_on)
      (convert-timestamp :modified_on)
      (assoc :attr (:attribute avu))
      (dissoc :attribute :target_type))))

(defn avu-list
  "Lists AVUs for the given target."
  [target-type target-id]
  (map format-avu (get-avus-for-target target-type target-id)))
