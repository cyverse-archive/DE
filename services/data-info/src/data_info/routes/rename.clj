(ns data-info.routes.rename
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.rename])
  (:require [data-info.services.rename :as rename]
            [data-info.util.service :as svc]))

(defroutes* rename-routes
  (POST* "/mover" [:as {uri :uri}]
    :tags ["bulk"]
    :query [params StandardUserQueryParams]
    :body [body (describe MultiRenameRequest "The paths to rename and their destination.")]
    :return MultiRenameResult
    :summary "Move Data Items"
    :description (str
"Given a list of sources and a destination in the body, moves all the sources into the given destination directory."
(get-error-code-block "ERR_NOT_A_FOLDER, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_EXISTS, ERR_TOO_MANY_PATHS, ERR_NOT_A_USER"))
    (svc/trap uri rename/do-move params body))

  (context* "/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (PUT* "/name" [:as {uri :uri}]
      :query [params StandardUserQueryParams]
      :body [body (describe Filename "The new name of the data item.")]
      :return RenameResult
      :summary "Rename Data Item"
      :description (str
"Moves the data item with the provided UUID to a new name within the same folder."
(get-error-code-block
  "ERR_NOT_A_FOLDER, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_EXISTS, ERR_INCOMPLETE_RENAME, ERR_NOT_A_USER, ERR_TOO_MANY_PATHS"))
      (svc/trap uri rename/do-rename-uuid params body data-id))

    (PUT* "/dir" [:as {uri :uri}]
      :query [params StandardUserQueryParams]
      :body [body (describe Dirname "The new directory name of the data item.")]
      :return RenameResult
      :summary "Move Data Item"
      :description (str
"Moves the data item with the provided UUID to a new folder, retaining its name."
(get-error-code-block
  "ERR_NOT_A_FOLDER, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_EXISTS, ERR_INCOMPLETE_RENAME, ERR_NOT_A_USER, ERR_TOO_MANY_PATHS"))
      (svc/trap uri rename/do-move-uuid params body data-id))

    (PUT* "/children/dir" [:as {uri :uri}]
      :query [params StandardUserQueryParams]
      :body [body (describe Dirname "The new directory name of the data items.")]
      :return MultiRenameResult
      :summary "Move Data Item Contents"
      :description (str
"Moves the contents of the folder with the provided UUID to a new folder, retaining their filenames."
(get-error-code-block
  "ERR_NOT_A_FOLDER, ERR_DOES_NOT_EXIST, ERR_NOT_WRITEABLE, ERR_EXISTS, ERR_INCOMPLETE_RENAME, ERR_NOT_A_USER, ERR_TOO_MANY_PATHS"))
      (svc/trap uri rename/do-move-uuid-contents params body data-id))))
