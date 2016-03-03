(ns sharkbait.analyses
  (:require [clojure.string :as string]
            [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.members :as members]
            [sharkbait.permissions :as perms]
            [sharkbait.roles :as roles]))

(defn- find-analysis-owner-member
  "Searches for a membership for the user who submitted an analysis."
  [session subjects {:keys [username]}]
  (when-let [subject (subjects (string/replace username #"@iplantcollaborative.org$" ""))]
    (members/find-subject-member session subject true)))

(defn- grant-owner-permission
  "Grants ownership permission to an analysis."
  [session subjects de-users-role app-resource analysis]
  (when-let [member (find-analysis-owner-member session subjects analysis)]
    (perms/grant-role-membership-permission de-users-role member perms/own app-resource)))

(defn- register-analysis
  [session subjects de-users-role permission-def folder-name analysis]
  (let [analysis-resource (perms/create-permission-resource session permission-def folder-name (:id analysis))]
    (grant-owner-permission session subjects de-users-role analysis-resource analysis)))

(defn register-de-analyses
  [db-spec session subjects folder-names permission-def-name]
  (let [analyses-folder-name  (:de-analyses folder-names)
        permission-def        (perms/find-permission-def analyses-folder-name permission-def-name)
        pre-existing-analyses (perms/load-permission-resource-name-set permission-def)
        de-users-role-name    (format "%s:%s" (:de-users folder-names) consts/de-users-role-name)
        de-users-role         (roles/find-role session de-users-role-name)]
    (->> (db/list-de-analyses db-spec)
         (remove (comp (partial contains? pre-existing-analyses)
                       (partial str analyses-folder-name ":")
                       :id))
         (map (partial register-analysis session subjects de-users-role permission-def analyses-folder-name))
         (dorun))))
