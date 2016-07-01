(ns iplant_groups.routes.groups
  (:use [common-swagger-api.schema]
        [iplant_groups.routes.schemas.group]
        [iplant_groups.routes.schemas.privileges]
        [iplant_groups.routes.schemas.params]
        [ring.util.http-response :only [ok]])
  (:require [iplant_groups.service.groups :as groups]))

(defroutes* groups
  (GET* "/" []
        :query       [params GroupSearchParams]
        :return      GroupList
        :summary     "Group Search"
        :description "This endpoint allows callers to search for groups by name. Only groups that
        are visible to the given user will be listed. The folder name, if provided, contains the
        name of the folder to search. Any folder name provided must exactly match the name of a
        folder in the system."
        (ok (groups/group-search params)))

  (POST* "/" []
        :return      GroupWithDetail
        :query       [params StandardUserQueryParams]
        :body        [body (describe BaseGroup "The group to add.")]
        :summary     "Add Group"
        :description "This endpoint allows adding a new group."
        (ok (groups/add-group body params)))

  (context* "/:group-name" []
    :path-params [group-name :- GroupNamePathParam]

    (GET* "/" []
          :query       [params StandardUserQueryParams]
          :return      GroupWithDetail
          :summary     "Get Group Information"
          :description "This endpoint allows callers to get detailed information about a single
          group."
          (ok (groups/get-group group-name params)))

    (PUT* "/" []
          :return      GroupWithDetail
          :query       [params StandardUserQueryParams]
          :body        [body (describe GroupUpdate "The group information to update.")]
          :summary     "Update Group"
          :description "This endpoint allows callers to update group information."
          (ok (groups/update-group group-name body params)))

    (DELETE* "/" []
          :query       [params StandardUserQueryParams]
          :return      GroupStub
          :summary     "Delete Group"
          :description "This endpoint allows deleting a group if the current user has permissions to do so."
          (ok (groups/delete-group group-name params)))

    (context* "/privileges" []
      (GET* "/" []
            :query       [params StandardUserQueryParams]
            :return      GroupPrivileges
            :summary     "List Group Privileges"
            :description "This endpoint allows callers to list the privileges visible to the current user of a single
            group."
            (ok (groups/get-group-privileges group-name params)))

      (context* "/:subject-id/:privilege-name" []
        :path-params [subject-id :- SubjectIdPathParam
                      privilege-name :- ValidGroupPrivileges]

        (PUT* "/" []
              :query       [params StandardUserQueryParams]
              :return      Privilege
              :summary     "Add Group Privilege"
              :description "This endpoint allows callers to add a specific privilege for a specific subject to a
              specific group."
              (ok (groups/add-group-privilege group-name subject-id privilege-name params)))

        (DELETE* "/" []
              :query       [params StandardUserQueryParams]
              :return      Privilege
              :summary     "Remove Group Privilege"
              :description "This endpoint allows callers to remove a specific privilege for a specific subject to a
              specific group."
              (ok (groups/remove-group-privilege group-name subject-id privilege-name params)))))

    (context* "/members" []
      (GET* "/" []
            :query       [params StandardUserQueryParams]
            :return      GroupMembers
            :summary     "List Group Members"
            :description "This endpoint allows callers to list the members of a single group."
            (ok (groups/get-group-members group-name params)))

      (context* "/:subject-id" []
        :path-params [subject-id :- SubjectIdPathParam]

        (PUT* "/" []
              :query       [params StandardUserQueryParams]
              :summary     "Add Group members"
              :description "This endpoint allows callers to add members to a group. Note that a request to add a user
              who is already a member of the group is treated as a no-op and no error will be reported."
              (groups/add-member group-name subject-id params)
              (ok))

        (DELETE* "/" []
                 :query       [params StandardUserQueryParams]
                 :summary     "Remove Group members"
                 :description "This endpoint allows callers to add members to a group. Note that a request to remove
                 someone who is not currently a member of the group (even a non-existent user) is treated as a no-op
                 and no error will be reported."
                 (groups/remove-member group-name subject-id params)
                 (ok))))))
