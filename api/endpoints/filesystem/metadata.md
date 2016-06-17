---
layout: page
title: DE API Documentation
---

Metadata
---------------------------

The following endpoints allow the caller to set and get attributes on files and directories in both iRODS and the CyVerse metadata service.
iRODS attributes take the form of Attribute Value Unit triples associated with directories and files.
iRODS AVUs are only unique on the full triple, so AVUs with duplicate attributes may exist.
DE tools do not, in general, use the unit field.

Getting Metadata
------------------------------------

Secured Endpoint: GET /secured/filesystem/{data-id}/metadata

Delegates to data-info: GET /data/{data-id}/metadata

This endpoint is a passthrough to the data-info endpoint above.
Please see the data-info documentation for more information.


Setting Metadata
-------------------------------------

Secured Endpoint: POST /secured/filesystem/{data-id}/metadata

Delegates to data-info: PUT /data/{data-id}/metadata

This endpoint is a passthrough to the data-info endpoint above.
Please see the data-info documentation for more information.


Listing Metadata Templates
--------------------------

Secured Endpoint: GET /secured/filesystem/metadata/templates

Delegates to metadata: GET /templates

Secured Endpoint: GET /admin/filesystem/metadata/templates

Delegates to metadata: GET /admin/templates

These endpoints are passthroughs to the metadata endpoints above.
Please see the metadata documentation for more information.


Viewing a Metadata Template
---------------------------

Secured Endpoint: GET /secured/filesystem/metadata/template/{template-id}

Delegates to metadata: GET /templates/{template-id}

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Downloading a blank template
----------------------------

Secured Endpoint: GET /secured/filesystem/metadata/template/{template-id}/blank-csv

Delegates to metadata: GET /templates/{template-id}/blank-csv

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Downloading a template guide
----------------------------

Secured Endpoint: GET /secured/filesystem/metadata/template/{template-id}/guide-csv

Delegates to metadata: GET /templates/{template-id}/guide-csv

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Viewing a Metadata Attribute
----------------------------

Secured Endpoint: GET /secured/filesystem/metadata/template/attr/{attribute-id}

Delegates to metadata: GET /templates/attr/{attribute-id}

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Adding Metadata Templates
---------------------------

Secured Endpoint: POST /admin/filesystem/metadata/templates

Delegates to metadata: POST /admin/templates

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Updating Metadata Templates
---------------------------

Secured Endpoint: POST /admin/filesystem/metadata/templates/{template-id}

Delegates to metadata: PUT /admin/templates/{template-id}

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Marking a Metadata Template as Deleted
----------------------------------------------------------

Secured Endpoint: DELETE /admin/filesystem/metadata/templates/{template-id}

Delegates to metadata: DELETE /admin/templates/{template-id}

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

Adding Batch Metadata to Multiple Paths from a CSV File
-------------------------------------------------------

Secured Endpoint: POST /secured/filesystem/metadata/csv-parser

Delegates to data-info: POST /data/{data-id}/metadata/csv-parser

#### Request Query Parameters

Parameter | Required | Description
----------|----------|------------
dest | Yes | The folder path to look under for files listed in the CSV file.
src | Yes | Path to the CSV source file in IRODS.
separator | No | URL encoded separator character to use for parsing the CSV/TSV file. Comma (`%2C`) by default.

This endpoint is a passthrough to the data-info endpoint above.
Please see the data-info documentation for more information.

#### Curl Command

    curl -sH "$AUTH_HEADER" -X POST "http://localhost:3000/secured/filesystem/metadata/csv-parser?dest=/iplant/home/ipcuser/folder_1&src=/iplant/home/ipcuser/metadata.csv"

Copying all Metadata from a File/Folder
-----------------------------------------------------

Secured Endpoint: POST /secured/filesystem/{data-id}/metadata/copy

Delegates to data-info: POST /data/{data-id}/metadata/copy

This endpoint is a passthrough to the data-info endpoint above.
Please see the data-info documentation for more information.

Exporting Metadata to a File
----------------------------

Secured Endpoint: POST /secured/filesystem/{data-id}/metadata/save

Delegates to data-info: POST /data/{data-id}/metadata/save

This endpoint is a passthrough to the data-info endpoint above.
Please see the data-info documentation for more information.
