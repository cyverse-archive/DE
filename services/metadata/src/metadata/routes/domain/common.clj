(ns metadata.routes.domain.common
  (:use [common-swagger-api.schema :only [describe StandardUserQueryParams]])
  (:require [schema.core :as s])
  (:import [java.util UUID]))

(def DataTypes ["file" "folder"])
(def TargetTypes (concat DataTypes ["analysis" "app" "user"]))

(def TargetIdPathParam (describe UUID "The target item's UUID"))
(def TargetTypeEnum (apply s/enum TargetTypes))

(def DataTypeEnum (apply s/enum DataTypes))
(def DataTypeParam (describe DataTypeEnum "The type of the requested data item."))

(s/defschema StandardDataItemQueryParams
  (assoc StandardUserQueryParams
    :data-type DataTypeParam))

(s/defschema TargetIDList
  {:target-ids (describe [UUID] "A list of target IDs")})

(s/defschema DataIdList
  {:filesystem (describe [UUID] "A list of UUIDs, each for a file or folder")})

(s/defschema TargetFilterRequest
  {:target-ids
   (describe [UUID] "List of IDs to filter")

   :target-types
   (describe [TargetTypeEnum] "The types of the given IDs")})
