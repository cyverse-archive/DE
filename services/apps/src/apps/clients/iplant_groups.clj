(ns apps.clients.iplant-groups
  (:use [clojure-commons.error-codes :only [clj-http-error?]]
        [medley.core :only [remove-vals]]
        [slingshot.slingshot :only [try+]])
  (:require [apps.util.config :as config]
            [apps.util.service :as service]
            [cemerick.url :as curl]
            [clj-http.client :as http]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [clojure-commons.exception :as cx]
            [kameleon.uuids :refer [uuidify]]))

(def grouper-user-group-fmt "iplant:de:%s:users:de-users")
(def grouper-app-permission-def-fmt "iplant:de:%s:apps:app-permission-def")
(def grouper-app-resource-name-fmt "iplant:de:%s:apps:%s")
(def grouper-analysis-permission-def-fmt "iplant:de:%s:analyses:analysis-permission-def")
(def grouper-analysis-resource-name-fmt "iplant:de:%s:analyses:%s")

(def ^:private permission-precedence
  {"own"   0
   "write" 1
   "read"  2})

(defn get-permission-level
  ([perms id]
     (get-permission-level (perms id)))
  ([perms]
     (first (sort-by permission-precedence (map :action_name perms)))))

(defn has-permission-level
  [perms required-level id]
  (some (comp (partial = required-level) :action_name) (perms id)))

(def lacks-permission-level (complement has-permission-level))

(defn format-permission
  [[subject subject-perms]]
  {:user        subject
   :permission (get-permission-level subject-perms)})

(defn find-forbidden-resources
  [perms required-level ids]
  (filter (partial lacks-permission-level perms required-level) ids))

(defn- grouper-user-group
  []
  (format grouper-user-group-fmt (config/env-name)))

(defn- grouper-permission-def
  [fmt]
  (format fmt (config/env-name)))

(def ^:private grouper-app-permission-def
  (partial grouper-permission-def grouper-app-permission-def-fmt))

(def ^:private grouper-analysis-permission-def
  (partial grouper-permission-def grouper-analysis-permission-def-fmt))

(defn- grouper-resource-name
  [fmt id]
  (format fmt (config/env-name) id))

(def ^:private grouper-app-resource-name
  (partial grouper-resource-name grouper-app-resource-name-fmt))

(def ^:private grouper-analysis-resource-name
  (partial grouper-resource-name grouper-analysis-resource-name-fmt))

(defn- grouper-url
  [& components]
  (str (apply curl/url (config/ipg-base) components)))

(defn- id-from-resource
  [resource]
  (last (string/split resource #":")))

(defn lookup-subject
  "Uses iplant-groups's subject lookup by ID endpoint to retrieve user details."
  [user short-username]
  (-> (http/get (grouper-url "subjects" short-username) {:query-params {:user user} :as :json})
      (:body)))

(defn add-de-user
  "Adds a user to the de-users group."
  [subject-id]
  (http/put (grouper-url "groups" (grouper-user-group) "members" subject-id)
            {:query-params {:user (config/de-grouper-user)}}))

(defn- retrieve-permissions*
  "Retrieves permission assignments from Grouper."
  [role subject attribute-def attribute-def-names]
  (->> {:user                (config/de-grouper-user)
        :role                role
        :attribute_def       attribute-def
        :attribute_def_names attribute-def-names
        :subject_id          subject}
       (remove-vals nil?)
       (hash-map :as :json :query-params)
       (http/get (grouper-url "attributes" "permissions"))
       :body
       :assignments))

(defn- retrieve-permissions
  "Retrieves permission assignments from Grouper."
  [role subject ids get-attribute-def to-attribute-resource-name]
  (retrieve-permissions* role subject (get-attribute-def) (map to-attribute-resource-name ids)))

(defn- retrieve-app-permissions
  "Retrieves app permission assignments from Grouper."
  ([subject app-ids]
     (retrieve-app-permissions nil subject app-ids))
  ([role subject app-ids]
     (retrieve-permissions role subject app-ids grouper-app-permission-def grouper-app-resource-name)))

(defn- retrieve-analysis-permissions
  "Retrieves analysis permission assignments from Grouper."
  ([subject analysis-ids]
     (retrieve-analysis-permissions nil subject analysis-ids))
  ([role subject analysis-ids]
     (retrieve-permissions role subject analysis-ids grouper-analysis-permission-def grouper-analysis-resource-name)))

(defn- group-permissions
  "Groups permissions by resource ID. The resource ID must be a UUID."
  [perms]
  (group-by (comp uuidify id-from-resource :name :attribute_definition_name) perms))

(defn load-app-permissions
  "Loads app permissions for a user from Grouper."
  ([user]
     (load-app-permissions user nil))
  ([user app-ids]
     (group-permissions (retrieve-app-permissions user app-ids))))

(defn list-app-permissions
  "Loads an app permission listing from Grouper."
  [app-ids]
  (group-permissions (retrieve-app-permissions nil app-ids)))

(defn load-analysis-permissions
  "Loads analysis permissions for a user from Grouper."
  ([user]
     (load-analysis-permissions user nil))
  ([user analysis-ids]
     (group-permissions (retrieve-analysis-permissions user analysis-ids))))

(defn list-analysis-permissions
  "Loads an analysis permission listing from Grouper."
  [analysis-ids]
  (group-permissions (retrieve-analysis-permissions nil analysis-ids)))

(defn- create-resource
  "Creates a new permission name in grouper."
  [resource-name permission-def]
  (:body (http/post (grouper-url "attributes")
                    {:query-params {:user (config/de-grouper-user)}
                     :form-params  {:name                 resource-name
                                    :attribute_definition {:name permission-def}}
                     :content-type :json
                     :as           :json})))

(defn- remove-resource
  "Removes an existing permission name from grouper."
  [resource-name]
  (http/delete (grouper-url "attributes" resource-name)
               {:query-params {:user (config/de-grouper-user)}}))

(defn- grant-role-user-permission
  "Grants permission to access a resource to an individual user."
  [user role resource-name action]
  (:body (http/put (grouper-url "attributes" resource-name "permissions" "memberships" role user action)
                   {:query-params {:user (config/de-grouper-user)}
                    :form-params  {:allowed true}
                    :content-type :json
                    :as           :json})))

(defn register-private-app
  "Registers a new private app in Grouper."
  [user app-id]
  (let [app-resource-name (grouper-app-resource-name app-id)]
    (create-resource app-resource-name (grouper-app-permission-def))
    (grant-role-user-permission user (grouper-user-group) app-resource-name "own")))

(defn delete-app-resource
  "Deletes an app resource permission name in grouper."
  [app-id]
  (remove-resource (grouper-app-resource-name app-id)))

(defn- format-role-permissions
  "Formats the role permissions for a permission update request."
  [role-permissions]
  (mapv (fn [[role-name action-name]]
          {:role_name role-name :action_name action-name})
        role-permissions))

(defn- format-membership-permissions
  "Formats the membership permissions for a permission update request."
  [membership-permissions]
  (mapv (fn [[role-name subject-id action-name]]
          {:role_name role-name :subject_id subject-id :action_name action-name})
        membership-permissions))

(defn- permission-update-body
  "Formats the request body for altering the the permissions of a single app in bulk."
  [role-permissions membership-permissions]
  (remove-vals nil? {:role_permissions       (format-role-permissions role-permissions)
                     :membership_permissions (format-membership-permissions membership-permissions)}))

(defn- public-permission-update-body
  "Formats the request body for making a resource publicly accessible in the DE."
  []
  (permission-update-body [[(grouper-user-group) "read"]] []))

(defn make-app-public
  "Makes an app publicly accessible in Grouper."
  [app-id]
  (:body (http/put (grouper-url "attributes" (grouper-app-resource-name app-id) "permissions")
                   {:query-params {:user (config/de-grouper-user)}
                    :form-params  (public-permission-update-body)
                    :content-type :json
                    :as           :json})))

(defn- share-resource
  "Shares a resource with a user."
  [resource-name role-name subject-id level]
  (http/put (grouper-url "attributes" resource-name "permissions" "memberships" role-name subject-id level)
            {:query-params {:user (config/de-grouper-user)}
             :form-params  {:allowed true}
             :content-type :json
             :as           :json})
  nil)

(defn- unshare-resource
  "Unshares a resource with a user."
  [resource-name role-name subject-id]
  (http/delete (grouper-url "attributes" resource-name "permissions" "memberships" role-name subject-id)
               {:query-params {:user (config/de-grouper-user)}
                :as           :json})
  nil)

(defn- share-app*
  "Shares an app with a user."
  [app-id subject-id level]
  (share-resource (grouper-app-resource-name app-id) (grouper-user-group) subject-id level))

(defn- unshare-app*
  "Unshares an app with a user."
  [app-id subject-id]
  (unshare-resource (grouper-app-resource-name app-id) (grouper-user-group) subject-id))

(defn- get-error-reason
  "Attempts to extract the reason for an error from an iplant-groups response body."
  [body status]
  (let [status-msg (str "HTTP status: " status)]
    (try+
      (or (:grouper_result_message (service/parse-json body)) status-msg)
      (catch Object _ status-msg))))

(defn share-app
  "Shares an app with a user."
  [app-id subject-id level]
  (try+
   (share-app* app-id subject-id level)
   (catch clj-http-error? {:keys [status body]}
     (let [reason (get-error-reason body status)]
       (log/error (str "unable to share " app-id " with " subject-id ": " reason)))
     "the app sharing request failed")))

(defn unshare-app
  "Unshares an app with a user."
  [app-id subject-id]
  (try+
   (unshare-app* app-id subject-id)
   (catch clj-http-error? {:keys [status body]}
     (let [reason (get-error-reason body status)]
       (log/error (str "unable to unshare " app-id " with " subject-id ": " reason)))
     "the app unsharing request failed")))

(defn register-analysis
  "Registers a new analysis in Grouper."
  [user analysis-id]
  (let [analysis-resource-name (grouper-analysis-resource-name analysis-id)]
    (create-resource analysis-resource-name (grouper-analysis-permission-def))
    (grant-role-user-permission user (grouper-user-group) analysis-resource-name "own")))

(defn- share-analysis*
  [analysis-id subject-id level]
  (share-resource (grouper-analysis-resource-name analysis-id) (grouper-user-group) subject-id level))

(defn- unshare-analysis*
  [analysis-id subject-id]
  (unshare-resource (grouper-analysis-resource-name analysis-id) (grouper-user-group) subject-id))

(defn share-analysis
  "Shares an analysis with a user."
  [analysis-id subject-id level]
  (try+
   (share-analysis* analysis-id subject-id level)
   (catch clj-http-error? {:keys [status body]}
     (let [reason (get-error-reason body status)]
       (log/error (str "uanble to share " analysis-id " with " subject-id ": " reason)))
     "the analysis sharing request failed")))

(defn unshare-analysis
  "Unshares an analysis with a user."
  [analysis-id subject-id]
  (try+
   (unshare-analysis* analysis-id subject-id)
   (catch clj-http-error? {:keys [status body]}
     (let [reason (get-error-reason body status)]
       (log/error (str "uanble to unshare " analysis-id " with " subject-id ": " reason)))
     "the analysis unsharing request failed")))
