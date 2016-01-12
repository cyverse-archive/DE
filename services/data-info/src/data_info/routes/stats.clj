(ns data-info.routes.stats
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.stats])
  (:require [data-info.services.stat :as stat]
            [data-info.util.service :as svc]
            [data-info.util.schema :as s]))


(defroutes* stat-gatherer

            ; FIXME Update apps exception handling when data-info excptn hndlg updated
            ; apps catches exceptions thrown from this EP.
  (context* "/stat-gatherer" []
    :tags ["bulk"]

    (POST* "/" [:as {uri :uri}]
      :query [params StatQueryParams]
      :body [body (describe OptionalPathsOrDataIds "The path or data ids of the data objects to gather status information on.")]
      :return (s/doc-only StatusInfo StatResponse)
      :summary "File and Folder Status Information"
      :description (str
"This endpoint allows the caller to get information about many files and folders at once, potentially also validating permissions on the files/folders for the user provided."
(get-error-code-block
  "ERR_DOES_NOT_EXIST, ERR_NOT_READABLE, ERR_NOT_WRITEABLE, ERR_NOT_OWNER, ERR_NOT_A_USER, ERR_TOO_MANY_RESULTS"))
      (svc/trap uri stat/do-stat params body))))
