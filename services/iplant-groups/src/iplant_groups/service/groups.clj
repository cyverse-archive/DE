(ns iplant_groups.service.groups
  (:require [iplant_groups.clients.grouper :as grouper]
            [iplant_groups.service.format :as fmt]
            [iplant_groups.util.service :as service]))

(defn group-search
  [{:keys [user search folder]}]
  {:groups (mapv fmt/format-group (grouper/group-search user folder search))})

(defn get-group
  [group-name {:keys [user]}]
  (if-let [group (grouper/get-group user group-name)]
    (fmt/format-group-with-detail group)
    (service/not-found "group" group-name)))

(defn get-group-members
  [group-name {:keys [user]}]
  (let [[subjects attribute-names] (grouper/get-group-members user group-name)]
    {:members (mapv #(fmt/format-subject attribute-names %) subjects)}))

(defn get-group-privileges
  [group-name {:keys [user]}]
  (let [[privileges attribute-names] (grouper/get-group-privileges user group-name)]
    {:privileges (mapv #(fmt/format-privilege attribute-names %) privileges)}))

(defn add-group
  [{:keys [type name description display_extension]} {:keys [user]}]
  (let [group (grouper/add-group user type name display_extension description)]
    (fmt/format-group-with-detail group)))

(defn add-group-privilege
  [group-name subject-id privilege-name {:keys [user]}]
  (let [[privilege attribute-names] (grouper/add-group-privileges user group-name subject-id [privilege-name])]
    (fmt/format-privilege attribute-names privilege :wsSubject)))

(defn remove-group-privilege
  [group-name subject-id privilege-name {:keys [user]}]
  (let [[privilege attribute-names] (grouper/remove-group-privileges user group-name subject-id [privilege-name])]
    (fmt/format-privilege attribute-names privilege :wsSubject)))

(defn update-group
  [group-name {:keys [name description display_extension]} {:keys [user]}]
  (let [group (grouper/update-group user group-name name display_extension description)]
    (fmt/format-group-with-detail group)))

(defn delete-group
  [group-name {:keys [user]}]
  (fmt/format-group (grouper/delete-group user group-name)))

(defn replace-members
  [group-name {:keys [members]} {:keys [user]}]
  {:results (mapv fmt/format-member-subject-update-response
                  (grouper/replace-group-members user group-name members))})

(defn add-member
  [group-name subject-id {:keys [user]}]
  (grouper/add-group-member user group-name subject-id))

(defn remove-member
  [group-name subject-id {:keys [user]}]
  (grouper/remove-group-member user group-name subject-id))
