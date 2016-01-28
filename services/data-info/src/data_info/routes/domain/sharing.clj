(ns data-info.routes.domain.sharing
  (:use [common-swagger-api.schema :only [describe
                                          NonBlankString]])
  (:require [schema.core :as s]))

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
