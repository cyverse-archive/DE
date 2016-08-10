(ns apps.service.groups
  (:require [apps.clients.iplant-groups :as ipg]))

(defn get-workshop-group []
  (select-keys (ipg/get-workshop-group)
               [:name :type :description :display_extension :display-name :extension :id_index :id]))

(defn get-workshop-group-members []
  (ipg/get-workshop-group-members))
