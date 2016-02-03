(ns data-info.routes.domain.rename
  (:use [common-swagger-api.schema :only [describe
                                          NonBlankString]])
  (:require [schema.core :as s]))

(s/defschema MultiRenameRequest
  {:sources
   (describe [NonBlankString] "iRODS paths to the initial locations of the data items to rename.")

   :dest
   (describe NonBlankString "An iRODS path to the destination directory for the items being renamed.")})

(s/defschema MultiRenameResult
  {:user
   (describe NonBlankString "The user performing the request.")

   :sources
   (describe [NonBlankString] "iRODS paths to the initial locations of the data items being renamed.")

   :dest
   (describe NonBlankString "An iRODS path to the destination directory of the data items being renamed.")})

(s/defschema RenameResult
  {:user
   (describe NonBlankString "The user performing the request.")

   :source
   (describe NonBlankString "An iRODS path to the initial location of the data item being renamed.")

   :dest
   (describe NonBlankString "An iRODS path to the destination of the data item being renamed.")})

(s/defschema Filename
  {:filename (describe NonBlankString "The name of the data item.")})

(s/defschema Dirname
  {:dirname (describe NonBlankString "The directory name of the data item.")})
