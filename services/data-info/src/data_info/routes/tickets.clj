(ns data-info.routes.tickets
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.tickets])
  (:require [data-info.services.tickets :as tickets]
            [data-info.util.service :as svc]
            [data-info.util.schema :as s]))


(defroutes* ticket-routes

  (context* "/tickets" []
    :tags ["tickets"]

    (POST* "/" [:as {uri :uri}]
      :query [params AddTicketQueryParams]
      :body [body Paths]
      :return AddTicketResponse
      :summary "Create tickets"
      :description (str
"This endpoint allows creating tickets for a set of provided paths"
(get-error-code-block "ERR_NOT_A_USER, ..."))
      (svc/trap uri tickets/do-add-tickets params body)))

  (POST* "/ticket-lister" [:as {uri :uri}]
    :tags ["bulk"]
    :query [params StandardUserQueryParams]
    :body [body Paths]
    :return (s/doc-only ListTicketsResponse ListTicketsDocumentation)
    :summary "List tickets"
    :description (str
"This endpoint lists tickets for a set of provided paths."
(get-error-code-block "ERR_NOT_A_USER, ..."))
    (svc/trap uri tickets/do-list-tickets params body)))
