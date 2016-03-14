(ns data-info.routes.permissions
  (:use [common-swagger-api.schema]
        [data-info.routes.domain.common]
        [data-info.routes.domain.permissions])
  (:require [data-info.services.users :as users]
            [data-info.services.permissions :as perms]
            [data-info.util.service :as svc]))

(defroutes* permissions-routes

  (context* "/permissions-gatherer" []
    :tags ["bulk"]

    (POST* "/" [:as {uri :uri}]
      :query [params StandardUserQueryParams]
      :body [body (describe Paths "The paths to gather permissions information on.")]
      :return PermissionsResponse
      :summary "File and Folder Permissions Information"
      :description (str
"This endpoint allows the caller to get user permissions information about many files and folders at once."
" The authenticated user must be the owner of these files, and that user's permissions, as well as the permissions of administrative users, are filtered out."
" The owner permissions are found in the normal stat return, e.g. from /stat-gatherer."
(get-error-code-block
  "ERR_NOT_A_USER, ERR_DOES_NOT_EXIST, ERR_NOT_OWNER, ERR_NOT_READABLE"))
      (svc/trap uri users/do-user-permissions params body)))

  (context* "/data/:data-id" []
    :path-params [data-id :- DataIdPathParam]
    :tags ["data-by-id"]

    (context* "/permissions" []

      (GET* "/" [:as {uri :uri}]
        :query [params StandardUserQueryParams]
        :return DataItemPermissionsResponse
        :summary "List Data Item Permissions"
        :description (str
"Lists permissions for a data item."
(get-error-code-block "ERR_DOES_NOT_EXIST, ERR_NOT_READABLE, ERR_NOT_A_USER"))
        (svc/trap uri perms/list-permissions params data-id))

      (PUT* "/:share-with/:permission" [:as {uri :uri}]
        :path-params [share-with :- (describe NonBlankString "The user to grant permissions to.")
                      permission :- (describe PermissionEnum "The permission level to grant.")]
        :query [params StandardUserQueryParams]
        :return DataItemPermissionsResponse
        :summary "Grant Data Item Permissions"
        :description (str
"Grants access to a data item."
(get-error-code-block "ERR_DOES_NOT_EXIST, ERR_NOT_OWNER, ERR_NOT_A_USER"))
        (svc/trap uri perms/add-permission params data-id share-with permission))

      (DELETE* "/:unshare-with" [:as {uri :uri}]
        :path-params [unshare-with :- (describe NonBlankString "The user whose permissions will be revoked.")]
        :query [params StandardUserQueryParams]
        :return DataItemPermissionsResponse
        :summary "Revoke Data Item Permissions"
        :description (str
"Revokes access to a data item."
(get-error-code-block "ERR_DOES_NOT_EXIST, ERR_NOT_OWNER, ERR_NOT_A_USER"))
        (svc/trap uri perms/remove-permission params data-id unshare-with)))))
