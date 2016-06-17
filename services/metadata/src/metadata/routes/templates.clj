(ns metadata.routes.templates
  (:use [common-swagger-api.schema]
        [metadata.routes.domain.common]
        [metadata.routes.domain.template]
        [ring.util.http-response :only [ok]])
  (:require [metadata.services.templates :as templates]))

(defroutes* templates
  (context* "/templates" []
    :tags ["template-info"]

    (GET* "/" []
      :query [params StandardUserQueryParams]
      :return MetadataTemplateList
      :summary "List Metadata Templates"
      :description "This endpoint lists undeleted metadata templates."
      (ok (templates/list-templates)))

    (GET* "/attr/:attr-id" []
      :path-params [attr-id :- AttrIdPathParam]
      :query [params StandardUserQueryParams]
      :return MetadataTemplateAttr
      :summary "View a Metadata Attribute"
      :description "This endpoint returns the details of a single metadata attribute."
      (ok (templates/view-attribute attr-id)))

    (context* "/:template-id" []
      :path-params [template-id :- TemplateIdPathParam]

      (GET* "/" []
        :query [params StandardUserQueryParams]
        :return MetadataTemplate
        :summary "View a Metadata Template"
        :description "This endpoint returns the details of a single metadata template."
        (ok (templates/view-template template-id)))

      (GET* "/blank-csv" []
        :query [{:keys [attachment]} CSVDownloadQueryParams]
        :summary "Get a blank CSV template file for a metadata template."
        :description "This endpoint returns a CSV file suitable for a specific template, ready to be filled in with specific values. It's intended to be downloaded to be filled out by the user, then reuploaded for use with the bulk metadata endpoints."
        (templates/csv-download-resp attachment "metadata.csv" (templates/view-template-csv template-id)))

      (GET* "/guide-csv" []
        :query [{:keys [attachment]} CSVDownloadQueryParams]
        :summary "Get a CSV guide file for a metadata template."
        :description "This endpoint returns a CSV file providing a guide for a specific template. It's intended to be downloaded to be used as a reference while filling out a file from the blank-csv endpoint for the same template."
        (templates/csv-download-resp attachment "guide.csv" (templates/view-template-guide template-id))))))

(defroutes* admin-templates
  (context* "/admin/templates" []
    :tags ["template-administration"]

    (GET* "/" []
      :query [params StandardUserQueryParams]
      :return MetadataTemplateList
      :summary "List Metadata Templates for Administrators"
      :description "This endpoint lists all metadata templates."
      (ok (templates/admin-list-templates)))

    (POST* "/" []
      :query [params StandardUserQueryParams]
      :body [body (describe MetadataTemplateUpdate "The template to add.")]
      :return MetadataTemplate
      :summary "Add a Metadata Template"
      :description "This endpoint allows administrators to add new metadata templates."
      (ok (templates/add-template params body)))

    (PUT* "/:template-id" []
      :path-params [template-id :- TemplateIdPathParam]
      :body [body (describe MetadataTemplateUpdate "The template to update.")]
      :query [params StandardUserQueryParams]
      :return MetadataTemplate
      :summary "Update a Metadata Template"
      :description "This endpoint allows administrators to update existing metadata templates."
      (ok (templates/update-template params template-id body)))

    (DELETE* "/:template-id" []
      :path-params [template-id :- TemplateIdPathParam]
      :query [params StandardUserQueryParams]
      :summary "Mark a Metadata Template as Deleted"
      :description "This endpoint allows administrators to mark existing metadata templates as
      deleted."
      (ok (templates/delete-template params template-id)))))
