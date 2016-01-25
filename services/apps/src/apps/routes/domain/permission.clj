(ns apps.routes.domain.permission
  (:use [common-swagger-api.schema :only [describe NonBlankString]]
        [schema.core :only [defschema optional-key enum]])
  (:import [java.util UUID]))

(defschema AppIdList
  {:apps (describe [NonBlankString] "A List of app identifiers")})

(defschema UserPermissionListElement
  {:user       (describe NonBlankString "The user ID")
   :permission (describe (enum "read" "write" "own" "") "The permission level assigned to the user")})

(defschema AppPermissionListElement
  {:id          (describe NonBlankString "The app ID")
   :permissions (describe [UserPermissionListElement] "The list of user permissions for the app")})

(defschema AppPermissionListing
  {:apps (describe [AppPermissionListElement] "The list of app permissions")})
