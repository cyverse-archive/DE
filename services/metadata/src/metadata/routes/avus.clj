(ns metadata.routes.avus
  (:use [common-swagger-api.schema]
        [metadata.routes.domain.common]
        [metadata.routes.domain.avus]
        [ring.util.http-response :only [ok]])
  (:require [metadata.services.avus :as avus]))

(defroutes* avus
  (context* "/avus" []
    :tags ["avus"]

    (POST* "/filter-targets" []
           :query [{:keys [attr value user]} FilterByAvuParams]
           :body [{:keys [target-types target-ids]} TargetFilterRequest]
           :return TargetIDList
           :summary "Filter Targets by AVU"
           :description
           "Filters the given target IDs by returning a list of any that have metadata with the given
            `attr` and `value`."
           (ok (avus/filter-targets-by-attr-value attr value target-types target-ids)))

    (GET* "/:target-type/:target-id" []
          :path-params [target-id :- TargetIdPathParam
                        target-type :- TargetTypeEnum]
          :query [{:keys [user]} StandardUserQueryParams]
          :return AvuList
          :summary "View all Metadata AVUs on a Target"
          :description "Lists all AVUs associated with the target item."
          (ok (avus/list-avus target-type target-id)))

    (POST* "/:target-type/:target-id" []
           :path-params [target-id :- TargetIdPathParam
                         target-type :- TargetTypeEnum]
           :query [{:keys [user]} StandardUserQueryParams]
           :body [body (describe SetAvuRequest "The Metadata AVU save request")]
           :return AvuList
           :summary "Set Metadata AVUs on a Target"
           :description "
Sets Metadata AVUs on the given target item.

Any AVUs not included in the request will be deleted. If the AVUs are omitted, then all AVUs for the
given target ID will be deleted.

Including an existing AVU’s ID in its JSON in the POST body will update its values and `modified` fields,
only if that AVU does not already match the given `attr`, `value`, and `unit` values.
AVUs included without an ID will be added to the target item, only if an AVU does not already exist with
matching `attr`, `value`, `unit`, `target`, and `type`."
           (ok (avus/set-avus user target-type target-id body)))

    (PUT* "/:target-type/:target-id" []
          :path-params [target-id :- TargetIdPathParam
                        target-type :- TargetTypeEnum]
          :query [{:keys [user]} StandardUserQueryParams]
          :body [body (describe AvuListRequest "The Metadata AVU update request")]
          :return AvuList
          :summary "Add/Update Metadata AVUs"
          :description "
Adds or updates Metadata AVUs on the given target item.

Including an existing AVU’s ID in its JSON in the PUT body will update its values and `modified` fields,
only if that AVU does not already match the given `attr`, `value`, and `unit` values.
AVUs included without an ID will be added to the target item, only if an AVU does not already exist with
matching `attr`, `value`, `unit`, `target`, and `type`."
          (ok (avus/update-avus user target-type target-id body)))

    (POST* "/:target-type/:target-id/copy" []
           :path-params [target-id :- TargetIdPathParam
                    target-type :- TargetTypeEnum]
           :query [{:keys [user]} StandardUserQueryParams]
           :body [body (describe TargetItemList "The destination targets.")]
           :summary "Copy all Metadata AVUs from a Target"
           :description "
Copies all Metadata Template AVUs from the data item with the ID given in the URL to other data
items sent in the request body."
           (ok (avus/copy-avus user target-type target-id body)))

    (DELETE* "/:target-type/:target-id/:avu-id" []
             :path-params [target-id :- TargetIdPathParam
                           target-type :- TargetTypeEnum
                           avu-id :- AvuIdPathParam]
             :query [{:keys [user]} StandardUserQueryParams]
             :summary "Remove Metadata AVU from a Target"
             :description "Removes the AVU associated with the given ID and target item."
             (ok (avus/remove-avu user target-type target-id avu-id)))))
