(ns iplant_groups.service.attributes
  (:require [iplant_groups.clients.grouper :as grouper]
            [iplant_groups.service.format :as fmt]))

(defn permission-assignment-search
  [{:keys [user] :as params}]
  (let [attribute-assignments (grouper/permission-assignment-search user params)]
    {:assignments (mapv fmt/format-permission-with-detail attribute-assignments)}))

(defn attribute-search
  [{:keys [user search exact]}]
  {:attributes (mapv fmt/format-attribute-name (grouper/attribute-name-search user search exact))})

(defn add-attribute-name
  [{:keys [name description display_extension attribute_definition]} {:keys [user]}]
  (let [attribute-name (grouper/add-attribute-name user attribute_definition name display_extension description)]
    (fmt/format-attribute-name attribute-name)))

(defn assign-role-permission
  [{:keys [user]} {:keys [allowed]} attribute-name role-name action-name]
  (let [attribute-assign (grouper/assign-role-permission user attribute-name role-name allowed [action-name])]
    (fmt/format-attribute-assign attribute-assign)))

(defn remove-role-permission
  [{:keys [user]} attribute-name role-name action-name]
  (let [attribute-assign (grouper/remove-role-permission user attribute-name role-name [action-name])]
    (fmt/format-attribute-assign attribute-assign)))

(defn assign-membership-permission
  [{:keys [user]} {:keys [allowed]} attribute-name role-name subject-id action-name]
  (fmt/format-attribute-assign
   (grouper/assign-membership-permission user attribute-name role-name subject-id allowed [action-name])))

(defn remove-membership-permission
  [{:keys [user]} attribute-name role-name subject-id action-name]
  (let [attribute-assign (grouper/remove-membership-permission user attribute-name role-name subject-id [action-name])]
    (fmt/format-attribute-assign attribute-assign)))

(defn replace-permissions
  [{:keys [user]} {:keys [role_permissions membership_permissions]} attribute-name]
  (grouper/replace-permissions user attribute-name role_permissions membership_permissions))
