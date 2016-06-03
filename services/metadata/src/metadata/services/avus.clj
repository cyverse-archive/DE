(ns metadata.services.avus
  (:use [kameleon.uuids :only [uuid]]
        [korma.db :only [transaction]]
        [slingshot.slingshot :only [throw+]])
  (:require [clojure.tools.logging :as log]
            [metadata.amqp :as amqp]
            [metadata.persistence.avu :as persistence]))

(defn filter-targets-by-attr-value
  "Filters the given target IDs by returning a list of any that have the given attr and value applied."
  [attr value target-types target-ids]
  {:target-ids (map :target_id
                    (persistence/filter-targets-by-attr-values target-types target-ids attr [value]))})

(defn- format-avu
  "Formats the given AVU for adding or updating."
  [target-type target-id {:keys [attr] :as avu}]
  (-> (select-keys avu [:id :value :unit])
      (assoc
        :attribute   attr
        :target_type target-type
        :target_id   target-id)))

(defn list-avus
  [target-type target-id]
  {:avus (persistence/avu-list target-type target-id)})

(defn update-avus
  [user-id target-type target-id {avus :avus}]
  (transaction
   (doseq [avu avus]
     (persistence/add-or-update-avu user-id
                                    (format-avu target-type target-id avu))))
  (amqp/publish-metadata-update user-id target-id)
  (list-avus target-type target-id))

(defn- remove-orphaned-avus
  "Removes any AVU for the given target-type and target-id that does not have a matching ID in the given
   set of avus."
  [target-type target-id avus]
  (->> avus
       (map :id)
       (remove nil?)
       (persistence/get-avus-by-ids)
       (map :id)
       (persistence/remove-orphaned-avus target-type target-id)))

(defn set-avus
  "Sets AVUs for the given user's data item."
  [user-id target-type target-id {avus :avus}]
  (transaction
   (remove-orphaned-avus target-type target-id avus)
   (doseq [avu avus]
     (persistence/add-or-update-avu user-id
                                    (format-avu target-type target-id avu))))
  (amqp/publish-metadata-update user-id target-id)
  (list-avus target-type target-id))

(defn- copy-avus-to-dest-targets
  "Copies Metadata AVUs to the given dest-targets."
  [user avus dest-targets]
  (transaction
    (doseq [{:keys [id type]} dest-targets]
      (update-avus user type id {:avus avus}))))

(defn- get-avu-copies
  "Fetches the list of Metadata AVUs for the given target,
   returning only the attr, value, and unit of each avu."
  [target-type target-id]
  (let [avus (persistence/avu-list target-type target-id)]
    (map #(select-keys % [:attr :value :unit]) avus)))

(defn copy-avus
  "Copies Metadata AVUs from the target item to dest-items."
  [user target-type target-id {dest-items :targets}]
  (let [avus (get-avu-copies target-type target-id)]
    (copy-avus-to-dest-targets user avus dest-items)
    nil))
