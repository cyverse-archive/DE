(ns apps.routes.admin
  (:use [common-swagger-api.routes]
        [common-swagger-api.schema]
        [common-swagger-api.schema.ontologies]
        [apps.metadata.reference-genomes :only [add-reference-genome
                                                delete-reference-genome
                                                replace-reference-genomes
                                                update-reference-genome]]
        [apps.metadata.tool-requests]
        [apps.routes.middleware :only [wrap-metadata-base-url]]
        [apps.routes.params]
        [apps.routes.schemas.app]
        [apps.routes.schemas.app.category]
        [apps.routes.schemas.reference-genome]
        [apps.routes.schemas.integration-data :only [IntegrationData]]
        [apps.routes.schemas.tool]
        [apps.user :only [current-user]]
        [apps.util.coercions :only [coerce!]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.apps :as apps]
            [apps.service.apps.de.admin :as admin]
            [apps.service.apps.de.listings :as listings]
            [apps.util.config :as config]))

(defroutes* admin-tool-requests
  (GET* "/" []
        :query [params ToolRequestListingParams]
        :return ToolRequestListing
        :summary "List Tool Requests"
        :description "This endpoint lists high level details about tool requests that have been submitted.
        Administrators may use this endpoint to track tool requests for all users."
        (ok (list-tool-requests params)))

  (GET* "/:request-id" []
        :path-params [request-id :- ToolRequestIdParam]
        :query [params SecuredQueryParams]
        :return ToolRequestDetails
        :summary "Obtain Tool Request Details"
        :description "This service obtains detailed information about a tool request. This is the service
        that the DE support team uses to obtain the request details."
        (ok (get-tool-request request-id)))

  (POST* "/:request-id/status" []
         :path-params [request-id :- ToolRequestIdParam]
         :query [params SecuredQueryParams]
         :body [body (describe ToolRequestStatusUpdate "A Tool Request status update.")]
         :return ToolRequestDetails
         :summary "Update the Status of a Tool Request"
         :description "This endpoint is used by Discovery Environment administrators to update the status
         of a tool request."
         (ok (update-tool-request request-id (config/uid-domain) current-user body))))

(defroutes* admin-apps
  (POST* "/" []
         :query [params SecuredQueryParams]
         :body [body (describe AppCategorizationRequest "An App Categorization Request.")]
         :summary "Categorize Apps"
         :description "This endpoint is used by the Admin interface to add or move Apps to into multiple
         Categories."
         (ok (apps/categorize-apps current-user body)))

  (POST* "/shredder" []
         :query [params SecuredQueryParams]
         :body [body (describe AppDeletionRequest "List of App IDs to delete.")]
         :summary "Permanently Deleting Apps"
         :description "This service physically removes an App from the database, which allows
         administrators to completely remove Apps that are causing problems."
         (ok (apps/permanently-delete-apps current-user body)))

  (DELETE* "/:app-id" []
           :path-params [app-id :- AppIdPathParam]
           :query [params SecuredQueryParams]
           :summary "Logically Deleting an App"
           :description "An app can be marked as deleted in the DE without being completely removed from
           the database using this service. This endpoint is the same as the non-admin endpoint,
           except an error is not returned if the user does not own the App."
           (ok (apps/admin-delete-app current-user app-id)))

  (PATCH* "/:app-id" []
          :path-params [app-id :- AppIdPathParam]
          :query [params SecuredQueryParams]
          :body [body (describe AdminAppPatchRequest "The App to update.")]
          :return AppDetails
          :middlewares [wrap-metadata-base-url]
          :summary "Update App Details and Labels"
          :description (str
"This service is capable of updating high-level information of an App,
 including 'deleted' and 'disabled' flags, as well as just the labels within a single-step app that has
 already been made available for public use.
<b>Note</b>: Although this endpoint accepts all App Group and Parameter fields within the 'groups' array,
 only their 'description', 'label', and 'display' (only in parameter arguments)
 fields will be processed and updated by this endpoint."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/filter")
"Please see the metadata service documentation for information about the `hierarchies` response field.")
          (ok (coerce! AppDetails
                (apps/admin-update-app current-user (assoc body :id app-id)))))

  (GET* "/:app-id/details" []
        :path-params [app-id :- AppIdPathParam]
        :query [params SecuredQueryParams]
        :return AppDetails
        :middlewares [wrap-metadata-base-url]
        :summary "Get App Details"
        :description (str
"This service allows administrative users to view detailed informaiton about private apps."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/filter")
"Please see the metadata service documentation for information about the `hierarchies` response field.")
        (ok (coerce! AppDetails
               (apps/admin-get-app-details current-user app-id))))

  (PATCH* "/:app-id/documentation" []
          :path-params [app-id :- AppIdPathParam]
          :query [params SecuredQueryParams]
          :body [body (describe AppDocumentationRequest "The App Documentation Request.")]
          :return AppDocumentation
          :summary "Update App Documentation"
          :description "This service is used by DE administrators to update documentation for a single
          App"
          (ok (coerce! AppDocumentation
                (apps/admin-edit-app-docs current-user app-id body))))

  (POST* "/:app-id/documentation" []
         :path-params [app-id :- AppIdPathParam]
         :query [params SecuredQueryParams]
         :body [body (describe AppDocumentationRequest "The App Documentation Request.")]
         :return AppDocumentation
         :summary "Add App Documentation"
         :description "This service is used by DE administrators to add documentation for a single App"
         (ok (coerce! AppDocumentation
                      (apps/admin-add-app-docs current-user app-id body))))

  (PUT* "/:app-id/integration-data/:integration-data-id" []
        :path-params [app-id :- AppIdPathParam integration-data-id :- IntegrationDataIdPathParam]
        :query [params SecuredQueryParams]
        :return IntegrationData
        :summary "Update the Integration Data Record for an App"
        :description "This service allows administrators to change the integration data record
        associated with an app."
        (ok (apps/update-app-integration-data current-user app-id integration-data-id))))

(defroutes* admin-categories
  (GET* "/" []
        :query [params SecuredQueryParams]
        :return AppCategoryListing
        :summary "List App Categories"
        :description "This service is used by DE admins to obtain a list of public app categories along
        with the 'Trash' virtual category."
        (ok (apps/get-admin-app-categories current-user params)))

  (POST* "/" []
         :query [params SecuredQueryParams]
         :body [body (describe AppCategoryRequest "The details of the App Category to add.")]
         :return AppCategoryAppListing
         :summary "Add an App Category"
         :description "This endpoint adds an App Category under the given parent App Category, as long as
         that parent Category doesn't already have a subcategory with the given name and it doesn't
         directly contain its own Apps."
         (ok (apps/admin-add-category current-user body)))

  (POST* "/shredder" []
         :query [params SecuredQueryParams]
         :body [body (describe AppCategoryIdList "A List of App Category IDs to delete.")]
         :return AppCategoryIdList
         :summary "Delete App Categories"
         :description "App Categories can be deleted using this endpoint. The App Category and all of its
         subcategories will be deleted by this service, but no Apps will be removed. The response
         contains a list of Category IDs for which the deletion failed (including any subcategories
         of a Category already included in the request)."
         (ok (apps/admin-delete-categories current-user body)))

  (DELETE* "/:category-id" []
           :path-params [category-id :- AppCategoryIdPathParam]
           :query [params SecuredQueryParams]
           :summary "Delete an App Category"
           :description "This service physically removes an App Category from the database, along with all
           of its child Categories, as long as none of them contain any Apps."
           (ok (apps/admin-delete-category current-user category-id)))

  (PATCH* "/:category-id" []
          :path-params [category-id :- AppCategoryIdPathParam]
          :query [params SecuredQueryParams]
          :body [body (describe AppCategoryPatchRequest "Details of the App Category to update.")]
          :summary "Update an App Category"
          :description "This service renames or moves an App Category to a new parent Category, depending
          on the fields included in the request."
          (ok (apps/admin-update-category current-user (assoc body :id category-id)))))

(defroutes* admin-ontologies

  (GET* "/" []
        :query [params SecuredQueryParams]
        :return ActiveOntologyDetailsList
        :middlewares [wrap-metadata-base-url]
        :summary "List Ontology Details"
        :description (str
"Lists Ontology details saved in the metadata service."
(get-endpoint-delegate-block
  "metadata"
  "GET /ontologies"))
        (ok (admin/list-ontologies current-user)))

  (DELETE* "/:ontology-version" []
           :path-params [ontology-version :- OntologyVersionParam]
           :query [params SecuredQueryParams]
           :middlewares [wrap-metadata-base-url]
           :summary "Delete an Ontology"
           :description (str
"Marks an Ontology as deleted in the metadata service.
 Returns `ERR_ILLEGAL_ARGUMENT` when attempting to delete the active `ontology-version`."
(get-endpoint-delegate-block
  "metadata"
  "DELETE /admin/ontologies/{ontology-version}"))
           (admin/delete-ontology current-user ontology-version))

  (POST* "/:ontology-version" []
         :path-params [ontology-version :- OntologyVersionParam]
         :query [params SecuredQueryParams]
         :return AppCategoryOntologyVersionDetails
         :summary "Set Active Ontology Version"
         :description
         "Sets the active `ontology-version` to use in non-admin endpoints when querying the ontology
          endpoints of the metadata service."
         (ok (admin/set-category-ontology-version current-user ontology-version)))

  (GET* "/:ontology-version/:root-iri" []
        :path-params [ontology-version :- OntologyVersionParam
                      root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr] :as params} OntologyHierarchyFilterParams]
        :middlewares [wrap-metadata-base-url]
        :summary "Get App Category Hierarchy"
        :description (str
"Gets the list of app categories that are visible to the user for the given `ontology-version`,
 rooted at the given `root-iri`."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter")
"Please see the metadata service documentation for response information.")
        (listings/get-app-hierarchy current-user ontology-version root-iri attr))

  (GET* "/:ontology-version/:root-iri/apps" []
        :path-params [ontology-version :- OntologyVersionParam
                      root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr] :as params} OntologyAppListingPagingParams]
        :middlewares [wrap-metadata-base-url]
        :return AppListing
        :summary "List Apps in a Category"
        :description (str
"Lists all of the apps under an app category hierarchy, for the given `ontology-version`,
 that are visible to the user."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter-targets"))
        (ok (coerce! AppListing
                     (apps/admin-list-apps-under-hierarchy current-user ontology-version root-iri attr params))))

  (GET* "/:ontology-version/:root-iri/unclassified" [root-iri]
        :path-params [ontology-version :- OntologyVersionParam
                      root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr] :as params} OntologyAppListingPagingParams]
        :return AppListing
        :middlewares [wrap-metadata-base-url]
        :summary "List Unclassified Apps"
        :description (str
"Lists all of the apps that are visible to the user that are not under the given `root-iri`, or any of
 its subcategories, for the given `ontology-version`."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter-unclassified"))
        (ok (coerce! AppListing
                     (listings/get-unclassified-app-listing current-user ontology-version root-iri attr params)))))

(defroutes* reference-genomes
  (POST* "/" []
         :query [params SecuredQueryParams]
         :body [body (describe ReferenceGenomeRequest "The Reference Genome to add.")]
         :return ReferenceGenome
         :summary "Add a Reference Genome."
         :description "This endpoint adds a Reference Genome to the Discovery Environment."
         (ok (add-reference-genome body)))

  (PUT* "/" []
            :query [params SecuredQueryParams]
            :body [body (describe ReferenceGenomesSetRequest "List of Reference Genomes to set.")]
            :return ReferenceGenomesList
            :summary "Replace Reference Genomes."
            :description "This endpoint replaces ALL the Reference Genomes in the Discovery Environment,
            so if a genome is not listed in the request, it will not show up in the DE."
            (ok (replace-reference-genomes body)))

  (PATCH* "/:reference-genome-id" []
          :path-params [reference-genome-id :- ReferenceGenomeIdParam]
          :query [params SecuredQueryParams]
          :body [body (describe ReferenceGenomeRequest "The Reference Genome fields to update.")]
          :return ReferenceGenome
          :summary "Update a Reference Genome."
          :description "This endpoint modifies the name, path, and deleted fields of a Reference Genome in
          the Discovery Environment."
          (ok (update-reference-genome (assoc body :id reference-genome-id))))

  (DELETE* "/:reference-genome-id" []
           :path-params [reference-genome-id :- ReferenceGenomeIdParam]
           :query [params SecuredQueryParams]
           :summary "Delete a Reference Genome."
           :description "A Reference Genome can be marked as deleted in the DE without being completely
           removed from the database using this service. <b>Note</b>: an attempt to delete a
           Reference Genome that is already marked as deleted is treated as a no-op rather than an
           error condition. If the Reference Genome doesn't exist in the database at all, however,
           then that is treated as an error condition."
           (ok (delete-reference-genome reference-genome-id))))
