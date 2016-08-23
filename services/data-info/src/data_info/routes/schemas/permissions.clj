(ns data-info.routes.schemas.permissions
  (:use [common-swagger-api.schema :only [describe NonBlankString]]
        [data-info.routes.schemas.common :only [PermissionEnum]])
  (:require [schema.core :as s]))

(s/defschema UserPermission
  {:user (describe String "The user's short username")
   :permission (describe PermissionEnum "The user's level of permission")})

(s/defschema PermissionsEntry
  {:path (describe String "The iRODS path to this file.")
   :user-permissions (describe [UserPermission] "An array of objects describing permissions.")})

(s/defschema PermissionsResponse
  {:paths (describe [PermissionsEntry] "An array of objects describing files and their permissions")})

(s/defschema DataItemPermissionsResponse
  (dissoc PermissionsEntry :path))

(s/defschema AnonFileUrls
  {(describe s/Keyword "the iRODS data item's path")
   (describe NonBlankString "the URL for the file to request in anon-files.")})

(s/defschema AnonShareInfo
  {:user
   (describe NonBlankString "The user performing the request.")

   :paths
   (describe AnonFileUrls "The anon-files URLs for the paths provided with the request.")})

;; Used only for display as documentation in Swagger UI
(s/defschema AnonFilePathsMap
  {:/path/from/request/to/a/file
   (describe NonBlankString "the URL for the file to request in anon-files.")})

;; Used only for display as documentation in Swagger UI
(s/defschema AnonShareResponse
  (assoc AnonShareInfo
         :paths (describe AnonFilePathsMap "The anon-files URLs for the paths provided with the request.")))
