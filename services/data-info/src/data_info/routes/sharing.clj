(ns data-info.routes.sharing
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.sharing])
  (:require [data-info.services.sharing :as sharing]
            [data-info.util.service :as svc]
            [data-info.util.schema :as s]))

(defroutes* sharing-routes
  (POST* "/anonymizer" [:as {uri :uri}]
    :tags ["bulk"]
    :query [params StandardUserQueryParams]
    :body [body (describe Paths "The paths to make readable by the anonymous user.")]
    :return (s/doc-only AnonShareInfo AnonShareResponse)
    :summary "Make Data Items Anonymously Readable"
    :description (str
"Given a list of files in the body, makes the files readable by the anonymous user."
(get-error-code-block "ERR_NOT_A_FILE, ERR_DOES_NOT_EXIST, ERR_NOT_OWNER, ERR_TOO_MANY_PATHS, ERR_NOT_A_USER"))
    (svc/trap uri sharing/do-anon-files params body)))
