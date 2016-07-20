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

(defn list-integration-data [_ {:keys [search limit offset sort-field sort-dir]}]
  (let [sort-field (sort-field-to-db-field sort-field)]
    (transaction
     {:integration_data
      (mapv format-integration-data (amp/list-integration-data search limit offset sort-field (keyword sort-dir)))

      :total
      (amp/count-integration-data search)})))

(defn- duplicate-username [username]
  (cxu/bad-request (str "user " username " already has an integration data record")))

(defn- duplicate-email [email]
  (cxu/bad-request (str "email address " email " already has an integration data record")))

(defn- not-found [id]
  (cxu/not-found (str "integration data record " id " does not exist")))

(defn add-integration-data [_ {:keys [username name email]}]
  (let [qualified-username (when username (str username "@" (cfg/uid-domain)))]
    (cond
      (and username (amp/get-integration-data-by-username qualified-username))
      (duplicate-username username)

      (amp/get-integration-data-by-email email)
      (duplicate-email email))

    (let [id (:id (amp/get-integration-data qualified-username email name))]
      (format-integration-data (amp/get-integration-data-by-id id)))))

(defn get-integration-data [_ id]
  (let [integration-data (amp/get-integration-data-by-id id)]
    (if-not (nil? integration-data)
      (format-integration-data integration-data)
      (not-found id))))

(defn update-integration-data [_ id {:keys [name email]}]
  (let [integration-data (amp/get-integration-data-by-id id)]
    (cond
      (nil? integration-data)
      (not-found id)

      ;; The database already contains duplicates, so we're not going to complain unless the email
      ;; address is being changed.
      (and (not= (:integrator_email integration-data) email)
           (amp/get-integration-data-by-email email))
      (duplicate-email email)))

  (amp/update-integration-data id name email)
  (format-integration-data (amp/get-integration-data-by-id id)))
