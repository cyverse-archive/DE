(ns apps.service.integration-data
  (:use [korma.db :only [transaction]]
        [medley.core :only [remove-vals]])
  (:require [apps.persistence.app-metadata :as amp]
            [apps.util.config :as cfg]
            [clojure.string :as string]
            [clojure-commons.exception-util :as cxu]))

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
    (transaction
     {:integration_data
      (mapv format-integration-data (amp/list-integration-data search limit offset sort-field (keyword sort-dir)))

      :total
      (amp/count-integration-data search)})))

(defn add-integration-data [_ {:keys [username name email]}]
  (let [qualified-username (when username (str username "@" (cfg/uid-domain)))]
    (cond
      (and username (amp/get-integration-data-by-username qualified-username))
      (cxu/bad-request (str "user " username " already has an integration data record"))

      (amp/get-integration-data-by-email email)
      (cxu/bad-request (str "email address " email " already has an integration data record")))

    (let [id (:id (amp/get-integration-data qualified-username email name))]
      (format-integration-data (amp/get-integration-data-by-id id)))))
