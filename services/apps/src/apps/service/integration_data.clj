(ns apps.service.integration-data
  (:use [medley.core :only [remove-vals]])
  (:require [apps.persistence.app-metadata :as amp]
            [clojure.string :as string]))

(defn- sort-field-to-db-field [sort-field]
  (cond (= sort-field :name)  :integrator_name
        (= sort-field :email) :integrator_email
        :else                 sort-field))

(defn- format-integration-data [{:keys [id username] email :integrator_email name :integrator_name}]
  (->> {:id       id
        :username (when-not (nil? username) (string/replace username #"@.*" ""))
        :email    email
        :name     name}
       (remove-vals nil?)))

(defn list-integration-data [user {:keys [search limit offset sort-field sort-dir]}]
  (let [sort-field (sort-field-to-db-field sort-field)]
    {:integration_data
     (mapv format-integration-data (amp/list-integration-data search limit offset sort-field (keyword sort-dir)))

     :total
     (amp/count-integration-data search)}))
