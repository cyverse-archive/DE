(ns data-info.routes.schemas.tickets
  (:use [common-swagger-api.schema :only [describe
                                          NonBlankString
                                          StandardUserQueryParams]]
        [data-info.routes.schemas.common])
  (:require [schema.core :as s])
  (:import [java.util UUID]))

(s/defschema AddTicketQueryParams
  (assoc StandardUserQueryParams
         :public (describe Boolean "Whether the created tickets should be made public")))

(s/defschema TicketDefinition
  {:path (describe NonBlankString "The iRODS path for the ticket")
   :ticket-id (describe NonBlankString "The ID of the ticket. Usually, but not always, a UUID.")
   :download-url (describe NonBlankString "The URL for downloading the file associated with this ticket.")
   :download-page-url (describe NonBlankString "The URL for managing this ticket, getting links, seeing metadata, etc.")})

(s/defschema AddTicketResponse
  {:user
   (describe NonBlankString "The user performing the request.")

   :tickets
   (describe [TicketDefinition] "The tickets created")})

(s/defschema ListTicketsResponseMap
  {(describe s/Keyword "The iRODS data item's path")
   (describe [TicketDefinition] "The tickets for this path")})

(s/defschema ListTicketsResponse
  {:tickets
   (describe ListTicketsResponseMap "Map of tickets")})

;; used only for documentation
(s/defschema ListTicketsPathsMap
  {:/path/from/request/to/a/file/or/folder
   (describe [TicketDefinition] "The tickets for this path")})

;; used only for documentation
(s/defschema ListTicketsDocumentation
  {:tickets
   (describe [ListTicketsPathsMap] "the tickets")})

(s/defschema Tickets
  {:tickets (describe [NonBlankString] "A list of ticket IDs")})

(s/defschema DeleteTicketsResponse
  (assoc Tickets
   :user (describe NonBlankString "The user performing the request.")))
