(ns apps-beta-tagger.db.de
  (:use [apps-beta-tagger.db]
        [kameleon.uuids :only [uuidify]]
        [korma.db]
        [korma.core :exclude [update]])
  (:require [korma.core :as sql]))

(def beta-category-uuid (uuidify "5401bd14-6c14-4470-aedd-57b47ea1b979"))

(defn define-database
  "Defines the DE database connection to use from within Clojure."
  [{:keys [de-host de-port de-database de-user]}]
  (defdb de-db (create-db-spec de-host de-port de-database de-user)))

(defn get-beta-app-ids
  []
  (with-db de-db
    (map :app_id
         (select :app_category_app
                 (fields :app_id)
                 (where {:app_category_id beta-category-uuid})))))
