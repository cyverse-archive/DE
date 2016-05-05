(ns data-info.routes.domain.data
  (:use [common-swagger-api.schema :only [describe
                                          NonBlankString
                                          PagingParams
                                          SortFieldDocs
                                          SortFieldOptionalKey
                                          StandardUserQueryParams]]
        [data-info.routes.domain.common]
        [heuristomancer.core :as info])
  (:require [schema.core :as s]))

(s/defschema FileUploadQueryParams
  (assoc StandardUserQueryParams
         :dest (describe NonBlankString "The destination directory for the uploaded file.")))

(s/defschema MetadataSaveRequest
  {:dest
   (describe NonBlankString "An IRODS path to a destination file where the metadata will be saved")

   :recursive
   (describe Boolean
     "When set to true and the given source is a folder, then all files and subfolders (plus all
      their files and subfolders) under the source folder will be included in the exported file,
      along with all of their metadata")})

(def ValidSortFields
  #{:datecreated
    :datemodified
    :name
    :path
    :size})

(s/defschema FolderListingParams
  (merge
    StandardUserQueryParams
    (assoc PagingParams
      SortFieldOptionalKey
      (describe (apply s/enum ValidSortFields) SortFieldDocs))
    {(s/optional-key :entity-type)
     (describe (s/enum :any :file :folder) "The type of folder items to include in the response.")

     (s/optional-key :bad-chars)
     (describe String
       "A list of characters which will mark a folder item's `badName` field to true if found in
        that item's name.")

     (s/optional-key :bad-name)
     (describe (s/either [String] String)
       "A list of names which will mark a folder item's `badName` field to true if its name matches
        any in the list.")

     (s/optional-key :bad-path)
     (describe (s/either [String] String)
       "A list of paths which will mark a folder item's `badName` field to true if its path matches
        any in the list.")

     (s/optional-key :info-type)
     (describe (s/either [ValidInfoTypesEnum] ValidInfoTypesEnum)
       "A list of info-types with which to filter a folder's result items.")

     (s/optional-key :attachment)
     (describe Boolean "Download file contents as attachment.")}))

(s/defschema TabularChunkParams
  (assoc
    StandardUserQueryParams
    :separator (describe s/Str "The separator value to use, url-encoded. %09 is the value for tab.")
    :page      (describe s/Int "The page of the results to get, relative to the page size.")
    :size      (describe s/Int "The page size to attempt. This will not be exact, because partial lines will not be provided.")))

(s/defschema ChunkParams
  (assoc
    StandardUserQueryParams
    :position (describe s/Int "The position to read from.")
    :size     (describe s/Int "The read length.")))

(s/defschema ChunkReturn
  {:path       (describe NonBlankString "The file path")
   :user       (describe NonBlankString "The requesting user.")
   :start      (describe NonBlankString "The start location for the read.")
   :chunk-size (describe NonBlankString "The size of the read.")
   :file-size  (describe NonBlankString "The file's total size.")
   :chunk      (describe String "The read result.")})

(s/defschema CSVEntry
  {(describe s/Keyword "The column number.")
   (describe String "The column data.")})

(s/defschema CSVDoc
  {:a-string-quoted-column-number (describe String "The column data.")})

(s/defschema TabularChunkReturn
  (-> ChunkReturn
    (dissoc :start :chunk)
    (assoc :page (describe NonBlankString "The page number.")
           :number-pages (describe NonBlankString "The total number of pages")
           :max-cols (describe NonBlankString "The maximum number of columns present.")
           :csv (describe [CSVEntry] "The tabular data result.")))) 

(s/defschema TabularChunkDoc
  (-> TabularChunkReturn
    (dissoc :csv)
    (assoc :csv (describe [CSVDoc] "The tabular data result."))))

(s/defschema ManifestURL
  {:label (describe NonBlankString "A label")
   :url   (describe NonBlankString "The URL being described. For anon-files URLs, the label will be 'anonymous'. For CoGe URLs, the label will start with 'gene_'. For tree URLs, the label will *usually* start with 'tree_', but it's not guaranteed as the DE does not produce these labels.")})

(s/defschema Manifest
  {:content-type
   (describe NonBlankString "The detected media type of the data contained in this file")

   :infoType
   (describe String "The type of contents in this file")

   :urls
   (describe [ManifestURL] "A set of URLs associated with this file")})
