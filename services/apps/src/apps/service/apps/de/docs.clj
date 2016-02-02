(ns apps.service.apps.de.docs
  (:use [slingshot.slingshot :only [throw+]])
  (:require [apps.persistence.app-documentation :as dp]
            [apps.persistence.app-metadata :as ap]
            [apps.service.apps.de.validation :as de-validation]
            [apps.validation :as v]
            [clojure-commons.validators :as cv]))

(defn- get-references
  "Returns a list of references from the database for the given app ID."
  [app-id]
  (map :reference_text (dp/get-app-references app-id)))

(defn- get-app-docs*
  "Retrieves app documentation."
  [app-id]
  (if-let [docs (dp/get-documentation app-id)]
    (assoc docs :references (get-references app-id))
    (throw+ {:type   :clojure-commons.exception/not-found
             :error  "App documentation not found"
             :app_id app-id})))

(defn get-app-docs
  "Retrieves documentation details for the given app ID."
  [user app-id]
  (de-validation/verify-app-permission user (ap/get-app app-id) "read")
  (get-app-docs* app-id))

(defn edit-app-docs
  "Updates an App's documentation and modified details in the database."
  [{:keys [username]} app-id {docs :documentation}]
  (when (get-app-docs* app-id)
    (dp/edit-documentation (v/get-valid-user-id username) docs app-id))
  (get-app-docs* app-id))

(defn owner-edit-app-docs
  "Updates an app's documentation in the database if the user has permission to edit the app."
  [user app-id docs]
  (let [app (ap/get-app app-id)]
    (when-not (cv/user-owns-app? user app)
      (de-validation/verify-app-permission user app "write")))
  (edit-app-docs user app-id docs))

(defn add-app-docs
  "Adds an App's documentation to the database."
  [{:keys [username]} app-id {docs :documentation}]
  (when-let [current-docs (dp/get-documentation app-id)]
    (throw+ {:type   :clojure-commons.exception/exists
             :error  "App already has documentation"
             :app_id app-id}))
  (dp/add-documentation (v/get-valid-user-id username) docs app-id)
  (get-app-docs* app-id))

(defn owner-add-app-docs
  "Adds an app's documentation to the database if the user has permission to edit the app."
  [user app-id docs]
  (let [app (ap/get-app app-id)]
    (when-not (cv/user-owns-app? user app)
      (de-validation/verify-app-permission user (ap/get-app app-id) "write")))
  (add-app-docs user app-id docs))
