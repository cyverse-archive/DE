(ns data-info.routes.domain.trash
  (:use [common-swagger-api.schema :only [describe NonBlankString]]
        [data-info.routes.domain.common])
  (:require [schema.core :as s]))

(s/defschema Trash
  (assoc Paths
         :trash (describe String "The path of the trash directory that was emptied.")))

(s/defschema RestoredFile
  {:restored-path (describe NonBlankString "The path the file was restored to.")
   :partial-restore (describe Boolean "If this file was restored to the home directory rather than to its former location, due to missing metadata.")})

(s/defschema RestorationMap
 {(describe s/Keyword "The IRODS data item's original path in the trash")
  (describe RestoredFile "The restored file information.")})

(s/defschema Restoration
  {:restored (describe RestorationMap "A map of paths from the request to their restoration info")})

;; Used only for display as documentation in Swagger UI
(s/defschema RestorationPathsMap
  {:/path/from/request/to/a/file/or/folder
   (describe RestoredFile "The restored file information.")})

;; Used only for display as documentation in Swagger UI
(s/defschema RestorationPaths
  {:restored
   (describe RestorationPathsMap "A map of paths from the request to their restoration info")})

(s/defschema TrashPathsMap
  {(describe s/Keyword "The iRODS data item's original path.")
   (describe String "The data item's path in the trash")})

(s/defschema TrashPaths
  (assoc Paths
         :trash-paths (describe TrashPathsMap "A map of paths from the request to their location in the trash, if any.")))

;; Used only for documentation in Swagger UI
(s/defschema TrashPathsDocMap
  {:/path/from/request/to/a/file/or/folder
   (describe String "The data item's path in the trash")})

;; Used only for documentation in Swagger UI
(s/defschema TrashPathsDoc
  (assoc TrashPaths
         :trash-paths (describe TrashPathsDocMap "A map of paths from the request to their location in the trash, if any.")))
