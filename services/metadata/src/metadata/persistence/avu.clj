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

(defn get-existing-metadata-template-avus-by-attr
  "Finds all existing AVUs by the given data-id and the given set of attributes."
  [data-id attributes]
  (select :avus
    (where {:attribute   [in attributes]
            :target_id   data-id
            :target_type [in data-types]})))

(defn find-existing-metadata-template-avu
  "Finds an existing AVU by ID or attribute, and by target_id."
  [avu]
  (let [id-key (if (:id avu) :id :attribute)]
    (first
      (select :avus
        (where {id-key       (id-key avu)
                :target_id   (:target_id avu)
                :target_type [in data-types]})))))

(defn get-avus-for-metadata-template
  "Gets AVUs for the given Metadata Template."
  [data-id template-id]
  (select :avus
    (join [:template_instances :t]
          {:t.avu_id :avus.id})
    (where {:t.template_id template-id
            :avus.target_id data-id
            :avus.target_type [in data-types]})))

(defn get-metadata-template-ids
  "Finds Metadata Template IDs associated with the given user's data item."
  [data-id]
  (select [:template_instances :t]
    (fields :template_id)
    (join :avus {:t.avu_id :avus.id})
    (where {:avus.target_id data-id
            :avus.target_type [in data-types]})
    (group :template_id)))

(defn remove-avu-template-instances
  "Removes the given Metadata Template AVU associations."
  [template-id avu-ids]
  (delete :template_instances
    (where {:template_id template-id
            :avu_id [in avu-ids]})))

(defn add-template-instances
  "Associates the given AVU with the given Metadata Template ID."
  [template-id avu-ids]
  (transaction
    (remove-avu-template-instances template-id avu-ids)
    (insert :template_instances
      (values (map #(hash-map :template_id template-id, :avu_id %) avu-ids)))))

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
  [user-id {:keys [id] :as avu}]
  (when-let [existing-avu (when id (get-avu-by-id id))]
    (when (not= (select-keys existing-avu [:target_type :target_id])
                (select-keys avu          [:target_type :target_id]))
      (throw+ {:type  :clojure-commons.exception/exists
               :error "AVU already attached to another target item."
               :avu   existing-avu}))
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

(defn remove-data-item-template-instances
  "Removes all Metadata Template AVU associations from the given data item."
  [data-id]
  (let [avu-id-select (-> (select* :avus)
                          (fields :id)
                          (where {:target_id data-id
                                  :target_type [in data-types]}))]
    (delete :template_instances (where {:avu_id [in (subselect avu-id-select)]}))))

(defn remove-orphaned-avus
  "Removes AVUs for the given target-id that are not in the given set of avu-ids."
  [target-type target-id avu-ids]
  (let [query (-> (delete* :avus)
                  (where {:target_id target-id
                          :target_type (db/->enum-val target-type)}))]
    (if (empty? avu-ids)
      (transaction
        (remove-data-item-template-instances target-id)
        (delete query))
      (delete (where query {:id [not-in avu-ids]})))))

(defn set-template-instances
  "Associates the given AVU IDs with the given Metadata Template ID,
   removing all other Metadata Template ID associations."
  [data-id template-id avu-ids]
  (transaction
    (remove-data-item-template-instances data-id)
    (add-template-instances template-id avu-ids)))

(defn format-avu
  "Formats a Metadata Template AVU for JSON responses."
  [avu]
  (let [convert-timestamp #(assoc %1 %2 (db/millis-from-timestamp (%2 %1)))]
    (-> avu
      (convert-timestamp :created_on)
      (convert-timestamp :modified_on)
      (assoc :attr (:attribute avu))
      (dissoc :attribute :target_type))))

(defn- get-metadata-template-avus
  "Gets a map containing AVUs for the given Metadata Template and the template's ID."
  [data-id template-id]
  (let [avus (get-avus-for-metadata-template data-id template-id)]
    {:template_id template-id
     :avus (map format-avu avus)}))

(defn metadata-template-list
  "Lists all Metadata Template AVUs for the given user's data item."
  [data-id]
  (let [template-ids (get-metadata-template-ids data-id)]
    {:data_id data-id
     :templates (map (comp (partial get-metadata-template-avus data-id) :template_id)
                  template-ids)}))

(defn metadata-template-avu-list
  "Lists AVUs for the given Metadata Template on the given user's data item."
  [data-id template-id]
  (assoc (get-metadata-template-avus data-id template-id)
    :data_id data-id))

(defn avu-list
  "Lists AVUs for the given target."
  [target-type target-id]
  (map format-avu (get-avus-for-target target-type target-id)))
