(ns terrain.services.metadata.apps
  (:use [clojure.java.io :only [reader]]
        [clojure-commons.client :only [build-url-with-query]]
        [terrain.util.config]
        [terrain.util.transformers :only [secured-params add-current-user-to-map]]
        [terrain.auth.user-attributes]
        [terrain.services.user-prefs :only [user-prefs]]
        [terrain.util.email]
        [terrain.util.service])
  (:require [cheshire.core :as cheshire]
            [clj-http.client :as client]
            [clojure.string :as string]
            [clojure.tools.logging :as log]
            [terrain.clients.iplant-groups :as ipg]
            [terrain.clients.data-info :as di]
            [terrain.clients.apps :as dm]
            [terrain.clients.notifications :as dn]))

(defn- apps-request
  "Prepares a apps request by extracting only the body of the client request and sets the
   forwarded request's content-type to json."
  [req]
  (assoc (select-keys req [:body]) :content-type :json))

(defn- apps-url
  "Adds the name and email of the currently authenticated user to the apps URL with the given
   relative URL path."
  [query & components]
  (apply build-url-with-query (apps-base)
                              (secured-params query)
                              components))

(defn import-tools
  "This service will import deployed components into the DE and send
   notifications if notification information is included and the deployed
   components are successfully imported."
  [body]
  (let [json (decode-json body)]
    (dm/import-tools json)
    (dorun (map dn/send-tool-notification (:tools json))))
  (success-response))

(defn bootstrap
  "This service obtains information about and initializes the workspace for the authenticated user.
   It also records the fact that the user logged in."
  [{{:keys [ip-address]} :params {user-agent "user-agent"} :headers}]
  (assert-valid ip-address "Missing or empty query string parameter: ip-address")
  (assert-valid user-agent "Missing or empty request parameter: user-agent")
  (let [username    (:username current-user)
        user        (:shortUsername current-user)
        workspace   (future (dm/get-workspace))
        preferences (future (user-prefs (:username current-user)))
        login-record (future (dm/record-login ip-address user-agent))
        auth-redirect (future (dm/get-auth-redirect-uris))]
    (success-response
      {:workspaceId   (:id @workspace)
       :newWorkspace  (:new_workspace @workspace)
       :loginTime     (str (:login_time @login-record))
       :username      user
       :full_username username
       :email         (:email current-user)
       :firstName     (:firstName current-user)
       :lastName      (:lastName current-user)
       :userHomePath  (di/user-home-folder user)
       :userTrashPath (di/user-trash-folder user)
       :baseTrashPath (di/base-trash-folder)
       :preferences   @preferences
       :auth-redirect @auth-redirect})))

(defn logout
  "This service records the fact that the user logged out."
  [{:keys [ip-address login-time]}]
  (assert-valid ip-address "Missing or empty query string parameter: ip-address")
  (assert-valid login-time "Missing or empty query string parameter: login-time")
  (dm/record-logout ip-address login-time)
  (success-response))

(defn add-reference-genome
  "Adds a reference genome via apps."
  [req]
  (let [url (apps-url {} "admin" "reference-genomes")
        req (apps-request req)]
    (forward-post url req)))

(defn replace-reference-genomes
  "Replaces the reference genomes in the database with a new set of reference genomes."
  [req]
  (let [url (apps-url {} "admin" "reference-genomes")
        req (apps-request req)]
    (forward-put url req)))

(defn delete-reference-genomes
  "Logically deletes a reference genome in the database."
  [reference-genome-id]
  (client/delete (apps-url {} "admin" "reference-genomes" reference-genome-id)
                 {:as :stream}))

(defn update-reference-genome
  "Updates a reference genome via apps."
  [req reference-genome-id]
  (let [url (apps-url {} "admin" "reference-genomes" reference-genome-id)
        req (apps-request req)]
    (forward-patch url req)))

(defn- postprocess-tool-request
  "Postprocesses a tool request update or submission. The postprocessing function
   should take the tool request and user details as arguments."
  [res f]
  (if (<= 200 (:status res) 299)
    (let [tool-req     (cheshire/decode-stream (reader (:body res)) true)
          username     (string/replace (:submitted_by tool-req) #"@.*" "")
          user-details (ipg/format-like-trellis (ipg/lookup-subject-add-empty username username))]
      (f tool-req user-details))
    res))

(defn submit-tool-request
  "Submits a tool request on behalf of the user found in the request params."
  [req]
  (let [tool-request-url (apps-url {} "tool-requests")
        req (apps-request req)]
    (postprocess-tool-request
      (forward-post tool-request-url req)
      (fn [tool-req user-details]
        (send-tool-request-email tool-req user-details)
        (success-response tool-req)))))

(defn list-tool-requests
  "Lists the tool requests that were submitted by the authenticated user."
  []
  (client/get (apps-url {} "tool-requests")
              {:as :stream}))

(defn admin-list-tool-requests
  "Lists the tool requests that were submitted by any user."
  [params]
  (success-response (dm/admin-list-tool-requests params)))

(defn list-tool-request-status-codes
  "Lists the known tool request status codes."
  [params]
  (success-response (dm/list-tool-request-status-codes params)))

(defn update-tool-request
  "Updates a tool request with comments and possibly a new status."
  [req request-id]
  (let [url (apps-url {} "admin" "tool-requests" request-id "status")
        req (apps-request req)]
    (postprocess-tool-request
      (forward-post url req)
      (fn [tool-req user-details]
        (success-response tool-req)))))

(defn get-tool-request
  "Lists details about a specific tool request."
  [request-id]
  (client/get (apps-url {} "admin" "tool-requests" request-id)
              {:as :stream}))

(defn provide-user-feedback
  "Forwards feedback from the user to iPlant."
  [body]
  (send-feedback-email (cheshire/decode-stream (reader body)))
  (success-response))
