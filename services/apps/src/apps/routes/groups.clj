(ns apps.routes.groups
  (:use [common-swagger-api.schema]
        [apps.routes.params :only [SecuredQueryParams]]
        [ring.util.http-response :only [ok]])
  (:require [apps.routes.schemas.groups :as schema]
            [apps.service.groups :as groups]))

(defroutes* admin-group-routes
  (GET* "/workshop" []
    :query [params SecuredQueryParams]
    :summary "Retrieve Workshop Group Information"
    :return schema/Group
    :description "This service allows administrators to retrieve information about the workshop users
    group."
    (ok (groups/get-workshop-group)))

  (GET* "/workshop/members" []
    :query [params SecuredQueryParams]
    :summary "List Workshop Group Members"
    :return schema/GroupMembers
    :description "This service allows administrators to list the members of the workshop users group."
    (ok (groups/get-workshop-group-members)))

  (PUT* "/workshop/members" []
    :query [params SecuredQueryParams]
    :body [body (describe schema/GroupMembersUpdate "The new list group member IDs.")]
    :return schema/GroupMembersUpdateResponse
    :summary "Update the Workshop Group Membership List"
    (ok (groups/update-workshop-group-members (:members body)))))
