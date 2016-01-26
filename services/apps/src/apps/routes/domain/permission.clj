(ns apps.routes.domain.permission
  (:use [common-swagger-api.schema :only [describe ErrorResponse NonBlankString]]
        [schema.core :only [defschema optional-key enum]])
  (:import [java.util UUID]))

(def PermissionEnum (enum "read" "write" "own" ""))

(defschema AppIdList
  {:apps (describe [NonBlankString] "A List of app identifiers")})

(defschema UserPermissionListElement
  {:user       (describe NonBlankString "The user ID")
   :permission (describe PermissionEnum "The permission level assigned to the user")})

(defschema AppPermissionListElement
  {:id          (describe NonBlankString "The app ID")
   :permissions (describe [UserPermissionListElement] "The list of user permissions for the app")})

(defschema AppPermissionListing
  {:apps (describe [AppPermissionListElement] "The list of app permissions")})

(defschema AppSharingRequestElement
  {:app_id     (describe NonBlankString "The app ID")
   :permission (describe PermissionEnum "The requested permission level")})

(defschema AppSharingResponseElement
  (assoc AppSharingRequestElement
    :success              (describe Boolean "A Boolean flag indicating whether the sharing request succeeded")
    (optional-key :error) (describe ErrorResponse "Information about any errors that may have occurred")))

(defschema UserAppSharingRequestElement
  {:user (describe NonBlankString "The user ID")
   :apps (describe [AppSharingRequestElement] "The list of app sharing requests for the user")})

(defschema UserAppSharingResponseElement
  (assoc UserAppSharingRequestElement
    :apps (describe [AppSharingResponseElement] "The list of app sharing responses for the user")))

(defschema AppSharingRequest
  {:sharing (describe [UserAppSharingRequestElement] "The list of app sharing requests")})

(defschema AppSharingResponse
  {:sharing (describe [UserAppSharingResponseElement] "The list of app sharing responses")})
