(ns sharkbait.users
  (:require [clojure.string :as string]
            [sharkbait.consts :as consts]
            [sharkbait.db :as db]
            [sharkbait.roles :as roles]
            [sharkbait.subjects :as subjects]))

(defn- register-de-user
  [de-users-role user]
  (when-let [subject (subjects/find-subject (string/replace (:username user) #"@.*$" "") false)]
    (roles/add-member de-users-role subject)))

(defn register-de-users
  [db-spec session]
  (let [de-users-role (roles/create-role session consts/de-users-folder consts/de-users-role-name)]
    (dorun (map (partial register-de-user de-users-role) (db/list-de-users db-spec)))))
