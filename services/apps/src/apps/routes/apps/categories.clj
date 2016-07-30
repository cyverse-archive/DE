(ns apps.routes.apps.categories
  (:use [common-swagger-api.routes]
        [common-swagger-api.schema]
        [common-swagger-api.schema.ontologies]
        [apps.routes.middleware :only [wrap-metadata-base-url]]
        [apps.routes.params]
        [apps.routes.schemas.app :only [AppListing]]
        [apps.routes.schemas.app.category]
        [apps.user :only [current-user]]
        [apps.util.coercions :only [coerce!]]
        [ring.util.http-response :only [ok]])
  (:require [apps.service.apps :as apps]
            [apps.service.apps.de.listings :as listings]
            [apps.util.service :as service]
            [compojure.route :as route]))

(defroutes* app-categories
  (GET* "/" []
        :query [params CategoryListingParams]
        :return AppCategoryListing
        :summary "List App Categories"
        :description "This service is used by the DE to obtain the list of app categories that
         are visible to the user."
        (ok (apps/get-app-categories current-user params)))

  (GET* "/:category-id" []
        :path-params [category-id :- AppCategoryIdPathParam]
        :query [params AppListingPagingParams]
        :middlewares [wrap-metadata-base-url]
        :return AppCategoryAppListing
        :summary "List Apps in a Category"
        :description "This service lists all of the apps within an app category or any of its
         descendents. The DE uses this service to obtain the list of apps when a user
         clicks on a category in the _Apps_ window.
         This endpoint accepts optional URL query parameters to limit and sort Apps,
         which will allow pagination of results."
        (ok (coerce! AppCategoryAppListing
                 (apps/list-apps-in-category current-user category-id params))))

  (route/not-found (service/unrecognized-path-response)))

(defroutes* app-hierarchies

  (GET* "/" []
        :query [params SecuredQueryParams]
        :middlewares [wrap-metadata-base-url]
        :summary "List App Hierarchies"
        :description (str
"Lists all hierarchies saved for the active ontology version."
(get-endpoint-delegate-block
  "metadata"
  "GET /ontologies/{ontology-version}")
"Please see the metadata service documentation for response information.")
        (listings/list-hierarchies current-user))

  (GET* "/:root-iri" []
        :path-params [root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr]} OntologyHierarchyFilterParams]
        :middlewares [wrap-metadata-base-url]
        :summary "List App Category Hierarchy"
        :description (str
"Gets the list of app categories that are visible to the user for the active ontology version,
 rooted at the given `root-iri`."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter")
"Please see the metadata service documentation for response information.")
        (listings/get-app-hierarchy current-user root-iri attr))

  (GET* "/:root-iri/apps" []
        :path-params [root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr] :as params} OntologyAppListingPagingParams]
        :middlewares [wrap-metadata-base-url]
        :return AppListing
        :summary "List Apps in a Category"
        :description (str
"Lists all of the apps under an app category hierarchy that are visible to the user."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter-targets"))
        (ok (coerce! AppListing (apps/list-apps-under-hierarchy current-user root-iri attr params))))

  (GET* "/:root-iri/unclassified" []
        :path-params [root-iri :- OntologyClassIRIParam]
        :query [{:keys [attr] :as params} OntologyAppListingPagingParams]
        :return AppListing
        :middlewares [wrap-metadata-base-url]
        :summary "List Unclassified Apps"
        :description (str
"Lists all of the apps that are visible to the user that are not under the given app category or any of
 its subcategories."
(get-endpoint-delegate-block
  "metadata"
  "POST /ontologies/{ontology-version}/{root-iri}/filter-unclassified"))
        (ok (coerce! AppListing (listings/get-unclassified-app-listing current-user root-iri attr params))))

  (route/not-found (service/unrecognized-path-response)))
