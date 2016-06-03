(ns terrain.services.metadata.comments
  (:use [slingshot.slingshot :only [try+ throw+]])
  (:require [clojure-commons.error-codes :as err]
            [clojure-commons.validators :as validators]
            [terrain.auth.user-attributes :as user]
            [terrain.clients.data-info :as data]
            [terrain.clients.apps :as apps]
            [terrain.clients.metadata.raw :as metadata]
            [terrain.services.filesystem.uuids :as data-uuids]
            [terrain.util.validators :as valid]))

(defn- extract-accessible-entry-id
  [user entry-id-txt]
  (try+
    (let [entry-id (valid/extract-uri-uuid entry-id-txt)]
      (data/validate-uuid-accessible user entry-id)
      entry-id)
    (catch [:error_code err/ERR_DOES_NOT_EXIST] _ (throw+ {:error_code err/ERR_NOT_FOUND}))))

(defn- extract-entry-id
  [entry-id-txt]
  (let [entry-id (valid/extract-uri-uuid entry-id-txt)]
    (when-not (data-uuids/uuid-exists? entry-id)
      (throw+ {:error_code err/ERR_NOT_FOUND :uuid entry-id}))
    entry-id))

(defn- extract-app-id
  [app-id]
  (let [app-uuid (valid/extract-uri-uuid app-id)]
    (apps/get-app-details app-uuid)
    app-uuid))

(defn- read-body
  [stream]
  (try+
    (slurp stream)
    (catch OutOfMemoryError _
      (throw+ {:error_code err/ERR_REQUEST_BODY_TOO_LARGE}))))


(defn add-data-comment
  "Adds a comment to a filesystem entry.

   Parameters:
     entry-id - the `entry-id` from the request. This should be the UUID corresponding to the entry
                being commented on
     body - the request body. It should be a JSON document containing the comment"
  [entry-id body]
  (let [user     (:shortUsername user/current-user)
        entry-id (extract-accessible-entry-id user entry-id)
        tgt-type (data/resolve-data-type entry-id)]
    (metadata/add-data-comment entry-id tgt-type (read-body body))))

(defn add-app-comment
  "Adds a comment to an App.

   Parameters:
     app-id - the UUID corresponding to the App being commented on
     body - the request body. It should be a JSON document containing the comment"
  [app-id body]
  (metadata/add-app-comment (extract-app-id app-id) (read-body body)))

(defn list-data-comments
  "Returns a list of comments attached to a given filesystem entry.

   Parameters:
     entry-id - the `entry-id` from the request. This should be the UUID corresponding to the entry
                being inspected"
  [entry-id]
  (metadata/list-data-comments
    (extract-accessible-entry-id (:shortUsername user/current-user) entry-id)))

(defn list-app-comments
  "Returns a list of comments attached to a given App ID.

   Parameters:
     app-id - the `app-id` from the request. This should be the UUID corresponding to the App being
              inspected"
  [app-id]
  (metadata/list-app-comments (extract-app-id app-id)))

(defn update-data-retract-status
  "Changes the retraction status for a given comment.

   Parameters:
     entry-id - the `entry-id` from the request. This should be the UUID corresponding to the entry
                owning the comment being modified
     comment-id - the comment-id from the request. This should be the UUID corresponding to the
                  comment being modified
     retracted - the `retracted` query parameter. This should be either `true` or `false`."
  [entry-id comment-id retracted]
  (let [user        (:shortUsername user/current-user)
        comment-id  (valid/extract-uri-uuid comment-id)
        entry-id    (extract-accessible-entry-id user entry-id)
        owns-entry? (= (keyword (:permission (data/stat-by-uuid user entry-id))) :own)]
    (if owns-entry?
      (metadata/admin-update-data-retract-status entry-id comment-id retracted)
      (metadata/update-data-retract-status entry-id comment-id retracted))))

(defn update-app-retract-status
  "Changes the retraction status for a given comment.

   Parameters:
     app-id - the UUID corresponding to the App owning the comment being modified
     comment-id - the comment-id from the request. This should be the UUID corresponding to the
                  comment being modified
     retracted - the `retracted` query parameter. This should be either `true` or `false`."
  [app-id comment-id retracted]
  (let [app-id     (valid/extract-uri-uuid app-id)
        comment-id (valid/extract-uri-uuid comment-id)
        app        (apps/get-app-details app-id)
        owns-app?  (validators/user-owns-app? user/current-user app)]
    (if owns-app?
      (metadata/admin-update-app-retract-status app-id comment-id retracted)
      (metadata/update-app-retract-status app-id comment-id retracted))))

(defn delete-data-comment
  [entry-id comment-id]
  (metadata/delete-data-comment (extract-entry-id entry-id) (valid/extract-uri-uuid comment-id)))

(defn delete-app-comment
  [app-id comment-id]
  (metadata/delete-app-comment (extract-app-id app-id) (valid/extract-uri-uuid comment-id)))
