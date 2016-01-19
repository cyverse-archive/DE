(ns iplant_groups.service.folders
  (:require [iplant_groups.clients.grouper :as grouper]
            [iplant_groups.service.format :as fmt]
            [iplant_groups.util.service :as service]))

(defn folder-search
  [{:keys [user search]}]
  {:folders (mapv fmt/format-folder (grouper/folder-search user search))})

(defn get-folder
  [folder-name {:keys [user]}]
  (if-let [folder (grouper/get-folder user folder-name)]
    (fmt/format-folder folder)
    (service/not-found "folder" folder-name)))

(defn get-folder-privileges
  [folder-name {:keys [user]}]
  (let [[privileges attribute-names] (grouper/get-folder-privileges user folder-name)]
    {:privileges (mapv #(fmt/format-privilege attribute-names %) privileges)}))

(defn add-folder
  [{:keys [name description display_extension]} {:keys [user]}]
  (let [folder (grouper/add-folder user name display_extension description)]
    (fmt/format-folder folder)))

(defn add-folder-privilege
  [folder-name subject-id privilege-name {:keys [user]}]
  (let [[privilege attribute-names] (grouper/add-folder-privileges user folder-name subject-id [privilege-name])]
    (fmt/format-privilege attribute-names privilege :wsSubject)))

(defn remove-folder-privilege
  [folder-name subject-id privilege-name {:keys [user]}]
  (let [[privilege attribute-names] (grouper/remove-folder-privileges user folder-name subject-id [privilege-name])]
    (fmt/format-privilege attribute-names privilege :wsSubject)))

(defn update-folder
  [folder-name {:keys [name description display_extension]} {:keys [user]}]
  (let [folder (grouper/update-folder user folder-name name display_extension description)]
    (fmt/format-folder folder)))

(defn delete-folder
  [folder-name {:keys [user]}]
  (fmt/format-folder (grouper/delete-folder user folder-name)))
