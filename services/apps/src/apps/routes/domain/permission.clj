(ns apps.routes.domain.permission
  (:use [common-swagger-api.schema :only [describe]]
        [schema.core :only [defschema optional-key enum]])
  (:import [java.util UUID]))

(defschema AppIdList
  {:apps (describe [String] "A List of app identifiers")})

(defschema UserPermission
  {:user       (describe String "The user ID")
   :permission (describe String "The permission level assigned to the user")})

(defschema AppPermissions
  {:id          (describe String "The app ID")
   :permissions (describe [UserPermission] "The list of user permissions for the app")})

(defschema AppPermissionListing
  {:apps (describe [AppPermissions] "The list of app permissions")})
