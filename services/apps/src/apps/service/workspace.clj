(ns apps.service.workspace
  (:use [korma.db :only [transaction]])
  (:require [apps.clients.iplant-groups :as ipg]
            [apps.persistence.workspace :as wp]))

(defn get-workspace
  [{short-username :shortUsername :keys [username]}]
  (ipg/add-de-user short-username)
  (if-let [workspace (wp/get-workspace username)]
    (assoc workspace :new_workspace false)
    (assoc (wp/create-workspace username) :new_workspace true)))
