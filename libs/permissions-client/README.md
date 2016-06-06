# permissions-client

A client library for the CyVerse Discovery Environment permissions service.

## Usage

``` clojure
(require '[permissions-client.core :as pc])

(def client (pc/new-permissions-client base-uri))

;; Get service status information.
(pc/get-status client)

;; Subject operations.
(def subjects (pc/list-subjects client))
(def subject (pc/add-subject client subject-id subject-type))
(def updated-subject (pc/update-subject client (:id subject) new-subject-id new-subject-type))
(pc/delete-subject client (:id updated-subject))

;; Resource operations.
(def resources (pc/list-resources client))
(def resource (pc/add-resource client resource-name resource-type-name))
(def updated-resource (pc/update-resource client (:id resource) new-resource-name))
(pc/delete-resource client (:id updated-resource))

;; Resource type operations.
(def resource-types (pc/list-resource-types client))
(def resource-type (pc/add-resource-type client name description))
(def updated-resource-type (pc/update-resource-type client (:id resource-type) new-name new-description)
(pc/delete-resource-type client (:id updated-resource-type))

;; Permissions operations.
(def permissions (pc/list-permissions client))
(def permission (pc/grant-permission client resource-type resource-name subject-type subject-id permission-level))
(pc/revoke-permission client resource-type resource-name subject-type subject-id)
(def permissions (pc/list-resource-permissions client resource-type resource-name))

;; List permissions by subject.
(def permissions (pc/get-subject-permissions client subject-type subject-id false))

;; Lookup the highest permission levels available to a subject. If the subject is a user then the listing may
;; also include permissions that are assigned to groups that the user belongs to.
(def permissions (pc/get-subject-permissions client subject-type subject-id true))

;; List permissions by subject and resource type.
(def permissions (pc/get-subject-permissions-for-resource-type client subject-type subject-id resource-type false))

;; Lookup the highest permission levels available to a subject for a resource type. If the subject is a user then the
;; listing may also include permissions that are assigned to groups that the user belongs to.
(def permissions (pc/get-subject-permissions-for-resource-type client subject-type subject-id resource-type true)

;; List permissions by subject and resource.
(def permissions (pc/get-subject-permissions-for-resource client subject-type subject-id resource-type resource-name
                                                          false))

;; Lookup the highest permission levels available to a subject for a resource. If the subject is a user then the
;; listing may also include permissions that are assigned to groups that the user belongs to.
(def permissions (pc/get-subject-permissions-for-resource client subject-type subject-id resource-type resource-name
                                                          true))
```

## License

http://www.cyverse.org/sites/default/files/iPLANT-LICENSE.txt
