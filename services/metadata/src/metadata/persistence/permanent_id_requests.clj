(ns metadata.persistence.permanent-id-requests
  (:use [clojure-commons.core :only [remove-nil-values]]
        [kameleon.db :only [millis-from-timestamp]]
        [kameleon.queries :only [add-query-limit add-query-offset add-query-sorting]]
        [korma.core :exclude [update]]
        [korma.db :only [transaction]])
  (:require [kameleon.db :as db]
            [korma.core :as sql]))

(def ^:private initial-status-code "Submitted")

(defn- where-if-defined
  "Adds a where clause to a query, filtering out all conditions for which the value is nil."
  [query clause]
  (let [clause (remove-nil-values clause)]
    (if (empty? clause)
      query
      (where query clause))))

(defn- list-permanent-id-requests-subselect
  "Creates a subselect query that can be used to list Permanent ID Requests."
  [user]
  (subselect [:permanent_id_requests :r]
    (fields :r.id
            :types.type
            :target_id
            :target_type
            :requested_by
            :original_path
            [:status_codes.name :status]
            [:statuses.date_assigned :status_date]
            [:statuses.updated_by])
    (join [:permanent_id_request_types :types] {:types.id :r.type})
    (join [:permanent_id_request_statuses :statuses] {:statuses.permanent_id_request :r.id})
    (join [:permanent_id_request_status_codes :status_codes]
          {:status_codes.id :statuses.permanent_id_request_status_code})
    (where-if-defined {:requested_by user})
    (order :statuses.date_assigned :ASC)))

(defn- list-permanent-id-requests-query
  "Lists the Permanent ID Requests that have been submitted by the user."
  [{user       :user
    row-offset :offset
    row-limit  :limit
    sort-field :sort-field
    sort-dir   :sort-dir}]
  (subselect [(list-permanent-id-requests-subselect user) :reqs]
      (fields :id :type :target_id :target_type :requested_by :original_path
              [(sqlfn :first :status_date) :date_submitted]
              [(sqlfn :last :status) :status]
              [(sqlfn :last :status_date) :date_updated]
              [(sqlfn :last :updated_by) :updated_by])
      (group :id :type :target_id :target_type :requested_by :original_path)
      (order (or sort-field :date_submitted) (or sort-dir :ASC))
      (limit row-limit)
      (offset row-offset)))

(defn- format-request-listing
  [request]
  (-> request
      (update-in [:date_submitted] millis-from-timestamp)
      (update-in [:date_updated] millis-from-timestamp)
      remove-nil-values))

(defn list-permanent-id-requests
  "Lists Permanent ID Requests, filtered and sorted by the given params."
  [{:keys [statuses] :as params}]
  (let [status-clause (when statuses ['in statuses])]
    (map format-request-listing
      (select [(list-permanent-id-requests-query params) :request_list]
        (where-if-defined {:status status-clause})))))

(defn get-permanent-id-request
  "Gets Permanent ID Request details. If the given user is not nil, only fetches details if the request
  was submitted by the given user."
  [user request-id]
  ((comp remove-nil-values first)
    (select [:permanent_id_requests :r]
      (fields :r.id
              :types.type
              :target_id
              :target_type
              :requested_by
              :original_path
              :permanent_id)
      (join [:permanent_id_request_types :types] {:types.id :r.type})
      (where-if-defined {:r.id request-id
                         :requested_by user}))))

(defn get-most-recent-status
  "Gets the most recent status for a Permanent ID Request."
  [request-id]
  ((comp :name first)
    (select [:permanent_id_requests :r]
            (fields :status_codes.name)
            (join [:permanent_id_request_statuses :statuses]
                  {:statuses.permanent_id_request :r.id})
            (join [:permanent_id_request_status_codes :status_codes]
                  {:status_codes.id :statuses.permanent_id_request_status_code})
            (where {:r.id request-id})
            (order :statuses.date_assigned :DESC)
            (limit 1))))

(defn- get-status-code
  "Attempts to retrieve a status code from the database."
  [status-name]
  (first (select :permanent_id_request_status_codes (where {:name status-name}))))

(defn- add-status-code
  "Adds a new status code."
  [status-name]
  (insert :permanent_id_request_status_codes
    (values {:name        status-name
             :description status-name})))

(defn get-or-create-status-code
  "Gets status code information from the database, adding one if the given name does not already exist."
  [status-name]
  (or (get-status-code status-name)
      (add-status-code status-name)))

(defn add-request-status-update
  "Adds a new Permanent ID Request status update."
  [status-update]
  (insert :permanent_id_request_statuses
    (values (select-keys status-update
              [:permanent_id_request
               :permanent_id_request_status_code
               :updated_by
               :date_assigned
               :comments]))))

(defn- format-request-status
  [status]
  (-> status
      (update-in [:status_date] millis-from-timestamp)
      remove-nil-values))

(defn get-request-statuses
  "Lists status updates for the given Permanent ID Request."
  [request-id]
  (map format-request-status
    (select [:permanent_id_request_statuses :statuses]
      (fields [:status_codes.name :status]
              :comments
              [:date_assigned :status_date]
              :updated_by)
      (join [:permanent_id_request_status_codes :status_codes]
            {:status_codes.id :statuses.permanent_id_request_status_code})
      (where {:permanent_id_request request-id}))))

(defn- request-type-subselect
  "Creates a subselect statement to find the primary key of a request type."
  [request-type]
  (subselect :permanent_id_request_types
    (fields :id)
    (where {:type request-type})))

(defn- status-code-subselect
  "Creates a subselect statement to find the primary key of a status code."
  [status-code]
  (subselect :permanent_id_request_status_codes
    (fields :id)
    (where {:name status-code})))

(defn find-permanent-id-request
  "Searches for a duplicate Permanent ID Request that matches the given request's type and target_id."
  [request]
  (let [where-vals (-> request
                       (select-keys [:type :target_id])
                       (update-in [:type] request-type-subselect))
        request-id (-> (select :permanent_id_requests (where where-vals)) first :id)]
    (when request-id
      (-> (select [(list-permanent-id-requests-query {}) :request_list] (where {:id request-id}))
          first
          format-request-listing))))

(defn- format-new-request-values
  [user request]
  (-> request
      (select-keys [:type :target_id :target_type :original_path])
      (update-in [:type] request-type-subselect)
      (update-in [:target_type] db/->enum-val)
      (assoc :requested_by user)))

(defn add-permanent-id-request
  "Adds a new Permanent ID Request for the given user."
  [user request]
  (transaction
    (let [{request-id :id :as new-request} (insert :permanent_id_requests
                                             (values (format-new-request-values user request)))]
      (insert :permanent_id_request_statuses
        (values {:permanent_id_request             request-id
                 :permanent_id_request_status_code (status-code-subselect initial-status-code)
                 :updated_by                       user}))
      new-request)))

(defn update-permanent-id-request
  "Records the Permanent ID for a given Request."
  [request-id permanent-id]
  (sql/update :permanent_id_requests
    (set-fields {:permanent_id permanent-id})
    (where {:id request-id})))

(defn list-permanent-id-request-status-codes
  []
  (select :permanent_id_request_status_codes))

(defn list-permanent-id-request-types
  []
  (select :permanent_id_request_types))
