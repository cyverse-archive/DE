(ns apps.persistence.categories
  (:use [kameleon.queries :only [get-user-id]]
        [korma.core :exclude [update]]
        [korma.db :only [transaction]]))

(defn add-hierarchy-version
  [username version]
  (insert :app_hierarchy_version
          (values {:version version
                   :applied_by (get-user-id username)})))

(defn get-active-hierarchy-version
  []
  ((comp :version first)
   (select :app_hierarchy_version
           (fields :version)
           (order :applied :DESC)
           (limit 1))))
