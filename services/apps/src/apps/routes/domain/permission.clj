(ns apps.routes.domain.permission
  (:use [common-swagger-api.schema :only [describe ErrorResponse NonBlankString]]
        [schema.core :only [defschema optional-key enum]])
  (:import [java.util UUID]))

(def PermissionEnum (enum "read" "write" "own" ""))

(defschema AppIdList
  {:apps (describe [NonBlankString] "A List of app IDs")})

(defschema UserPermissionListElement
  {:user       (describe NonBlankString "The user ID")
   :permission (describe PermissionEnum "The permission level assigned to the user")})

(defschema AppPermissionListElement
  {:id          (describe NonBlankString "The app ID")
   :name        (describe NonBlankString "The app name")
   :permissions (describe [UserPermissionListElement] "The list of user permissions for the app")})

(defschema AppPermissionListing
  {:apps (describe [AppPermissionListElement] "The list of app permissions")})

(defschema AppSharingRequestElement
  {:app_id     (describe NonBlankString "The app ID")
   :permission (describe PermissionEnum "The requested permission level")})

(defschema AppSharingResponseElement
  (assoc AppSharingRequestElement
    :app_name             (describe NonBlankString "The app name")
    :success              (describe Boolean "A Boolean flag indicating whether the sharing request succeeded")
    (optional-key :error) (describe ErrorResponse "Information about any error that may have occurred")))

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

(defschema AppUnsharingResponseElement
  {:app_id               (describe NonBlankString "The app ID")
   :app_name             (describe NonBlankString "The app name")
   :success              (describe Boolean "A Boolean flag indicating whether the unsharing request succeeded")
   (optional-key :error) (describe ErrorResponse "Information about any error that may have occurred")})

(defschema UserAppUnsharingRequestElement
  {:user (describe NonBlankString "The user ID")
   :apps (describe [NonBlankString] "The list of app IDs")})

(defschema UserAppUnsharingResponseElement
  (assoc UserAppUnsharingRequestElement
    :apps (describe [AppUnsharingResponseElement] "The list of app sharing responses for the user")))

(defschema AppUnsharingRequest
  {:unsharing (describe [UserAppUnsharingRequestElement] "The list of app unsharing requests")})

(defschema AppUnsharingResponse
  {:unsharing (describe [UserAppUnsharingResponseElement] "The list of app unsharing responses")})

(defschema AnalysisIdList
  {:analyses (describe [UUID] "A List of analysis IDs")})

(defschema AnalysisPermissionListElement
  {:id          (describe UUID "The analysis ID")
   :name        (describe NonBlankString "The analysis name")
   :permissions (describe [UserPermissionListElement] "The list of user permissions for the analysis")})

(defschema AnalysisPermissionListing
  {:analyses (describe [AnalysisPermissionListElement] "The list of analysis permissions")})
