(ns metadata.services.permanent-id-requests
  (:use [korma.db :only [transaction]]
        [slingshot.slingshot :only [throw+]])
  (:require [clojure.string :as string]
            [metadata.persistence.permanent-id-requests :as db]))

(defn- assert-request-found
  [request request-id]
  (when-not request
    (throw+ {:type  :clojure-commons.exception/not-found
             :id    request-id
             :error "A Permanent ID Request with this ID was not found."}))
  request)

(defn- assert-user-request-found
  [request user request-id]
  (when-not request
    (throw+ {:type  :clojure-commons.exception/not-found
             :user  user
             :id    request-id
             :error "A Permanent ID Request with this ID was not found for this user."}))
  request)

(defn- assert-request-unique
  [request]
  (when request
    (throw+ {:type    :clojure-commons.exception/not-unique
             :error   "A Permanent ID Request of this type for this item was already submitted."
             :request request}))
  request)

(defn- assert-status-found
  [request-id status]
  (when (nil? status)
    (throw+ {:type :clojure-commons.exception/failed-dependency
             :error "No status found for Permanent ID Request."
             :id request-id}))
  status)

(defn- assert-request-identifier-not-found
  [identifier]
  (when identifier
    (throw+ {:type       :clojure-commons.exception/exists
             :error      "This Permanent ID Request already has an associated identifier."
             :identifier identifier})))

(defn- format-listing-params
  [params]
  (if (:sort-field params)
    params
    (assoc params
      :sort-field :date_submitted
      :sort-dir   :DESC)))

(defn- format-admin-listing-params
  [params]
  (dissoc (format-listing-params params) :user))

(defn list-permanent-id-requests
  [params]
  {:requests (db/list-permanent-id-requests (format-listing-params params))})

(defn- add-request-history
  [{request-id :id :as request}]
  (assoc request :history (db/get-request-statuses request-id)))

(defn get-permanent-id-request
  [user request-id]
  (let [request (db/get-permanent-id-request user request-id)]
    (assert-user-request-found request user request-id)
    (add-request-history request)))

(defn create-permanent-id-request
  [user request]
  (assert-request-unique (db/find-permanent-id-request request))
  (let [request-id (:id (db/add-permanent-id-request user request))]
    (get-permanent-id-request user request-id)))

(defn list-permanent-id-request-status-codes
  [params]
  {:status_codes (db/list-permanent-id-request-status-codes)})

(defn list-permanent-id-request-types
  [params]
  {:request_types (db/list-permanent-id-request-types)})

(defn admin-list-permanent-id-requests
  [params]
  {:requests (db/list-permanent-id-requests (format-admin-listing-params params))})

(defn admin-get-permanent-id-request
  [user request-id]
  (let [request (db/get-permanent-id-request nil request-id)]
    (assert-request-found request request-id)
    (add-request-history request)))

(defn update-permanent-id-request
  [request-id user {:keys [status comments permanent_id] :as body}]
  (transaction
    (let [status-update (assoc body :user user)
          request       (-> (db/get-permanent-id-request nil request-id)
                            (assert-request-found request-id))
          prev-status   (->> (db/get-most-recent-status request-id)
                             (assert-status-found request-id))
          status        (or status prev-status)
          status-id     (:id (db/get-or-create-status-code status))
          comments      (when-not (string/blank? comments) comments)]
      (when permanent_id
        (assert-request-identifier-not-found (:permanent_id request))
        (db/update-permanent-id-request request-id permanent_id))
      (db/add-request-status-update {:permanent_id_request             request-id
                                     :permanent_id_request_status_code status-id
                                     :updated_by                       user
                                     :comments                         comments})))
  (admin-get-permanent-id-request user request-id))
