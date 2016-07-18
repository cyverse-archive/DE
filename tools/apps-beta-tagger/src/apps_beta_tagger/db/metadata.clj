(ns apps-beta-tagger.db.metadata
  (:use [apps-beta-tagger.db]
        [korma.db]
        [korma.core :exclude [update]])
  (:require [clojure.tools.logging :as log]
            [kameleon.db :as db]))

(def avu-creator-username "ipcdev")

(def beta-avu
  {:attribute   "n2t.net/ark:/99152/h1459"
   :value       "beta"
   :unit        ""
   :target_type (db/->enum-val "app")
   :created_by  avu-creator-username
   :modified_by avu-creator-username})

(def beta-label-avu
  {:attribute   "rdfs:label"
   :value       "releaseStatus"
   :unit        "attr"
   :target_type (db/->enum-val "avu")
   :created_by  avu-creator-username
   :modified_by avu-creator-username})

(defn define-database
  "Defines the metadata database connection to use from within Clojure."
  [{:keys [meta-host meta-port meta-database meta-user]}]
  (defdb metadata-database (create-db-spec meta-host meta-port meta-database meta-user)))


(defn- add-beta-avu
  "Adds a beta AVU to the app with the given `app-id`"
  [app-id]
  (insert :avus (values (assoc beta-avu :target_id app-id))))

(defn- add-beta-avu-label
  "Adds a beta label AVU to the AVU with the given `beta-avu-id`"
  [beta-avu-id]
  (insert :avus (values (assoc beta-label-avu :target_id beta-avu-id))))

(defn- add-app-beta-avu
  "Adds a beta AVU, with a beta label AVU, to the app with the given `app-id`"
  [app-id]
  (->> app-id
       add-beta-avu
       :id
       add-beta-avu-label))

(defn tag-beta-apps
  [app-ids]
  (with-db metadata-database
    (transaction
      (doseq [app-id app-ids]
        (add-app-beta-avu app-id))))
  (log/info "Successfully added a beta AVU to" (count app-ids) "apps found in the 'Beta' category."))
