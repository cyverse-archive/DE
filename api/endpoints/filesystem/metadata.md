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

This endpoint will parse a CSV/TSV file, where the first column is absolute or relative paths to files in the data store, the remaining columns are metadata, attributes are listed in the first row, and filenames and attribute values are listed in the remaining rows. If a `template-id` parameter is provided, then any parsed AVUs with attributes that match the given template's attributes will be added as template AVUs, otherwise all other AVUs will be added as IRODS metadata AVUs.

__URL Path__: /secured/filesystem/metadata/csv-parser

__HTTP Method__: POST

__Delegates to data-info__: `PATCH /data/{data-id}/metadata`

__Error Codes__: ERR_NOT_READABLE, ERR_NOT_WRITEABLE, ERR_DOES_NOT_EXIST, ERR_NOT_A_USER, ERR_BAD_OR_MISSING_FIELD, ERR_NOT_UNIQUE

__Request Query Parameters__:

Parameter | Required | Description
----------|----------|------------
dest | Yes | The folder path to look under for files listed in the CSV file.
src | Yes | Path to the CSV source file in IRODS.
force | No | If omitted or set to `false`, then existing IRODS AVUs will be checked for attributes matching those parsed from the CSV file. If a match is found, then an `ERR_NOT_UNIQUE` is returned and metadata is not saved.
template-id | No | The UUID of the Metadata Template with which to associate the parsed metadata.
separator | No | URL encoded separator character to use for parsing the CSV/TSV file. Comma (`%2C`) by default.

__Request File Format__:

filename | template_item | template_postal_code | template_department | template_institution | test-attr-1 | test-attr-2 | test-attr-3
---------|---|---|---|---|---|---|---
library1/fake.1.fastq.gz | fake-1 | 85719 | BIO5 | iPlant | test-val-1 | test-val-2 | test-val-3
/iplant/home/ipcuser/folder_2/library2/fake.2.fastq.gz | fake-2 | 85719 | BIO5 | iPlant | test-val-1 | test-val-2 | test-val-3
library1 | lib-1 | 85719 | BIO5 | iPlant | test-val-1 | test-val-2 | test-val-3

__Response__:

```json
{
    "path-metadata": [
        {
            "path": "/iplant/home/ipcuser/folder_1/library1/fake.1.fastq.gz",
            "metadata": [
                {
                    "attr": "template_item",
                    "value": "fake-1",
                    "unit": ""
                },
                {
                    "attr": "template_postal_code",
                    "value": "85719",
                    "unit": ""
                },
                {
                    "attr": "template_department",
                    "value": "BIO5",
                    "unit": ""
                },
                {
                    "attr": "template_institution",
                    "value": "iPlant",
                    "unit": ""
                }
            ],
            "irods-avus": [
                {
                    "attr": "test-attr-1",
                    "value": "test-val-1",
                    "unit": ""
                },
                {
                    "attr": "test-attr-2",
                    "value": "test-val-2",
                    "unit": ""
                },
                {
                    "attr": "test-attr-3",
                    "value": "test-val-3",
                    "unit": ""
                }
            ]
        },
        {
            "path": "/iplant/home/ipcuser/folder_2/library2/fake.2.fastq.gz",
            "metadata": [
                {
                    "attr": "template_item",
                    "value": "fake-2",
                    "unit": ""
                },
                {
                    "attr": "template_postal_code",
                    "value": "85719",
                    "unit": ""
                },
                {
                    "attr": "template_department",
                    "value": "BIO5",
                    "unit": ""
                },
                {
                    "attr": "template_institution",
                    "value": "iPlant",
                    "unit": ""
                }
            ],
            "irods-avus": [
                {
                    "attr": "test-attr-1",
                    "value": "test-val-1",
                    "unit": ""
                },
                {
                    "attr": "test-attr-2",
                    "value": "test-val-2",
                    "unit": ""
                },
                {
                    "attr": "test-attr-3",
                    "value": "test-val-3",
                    "unit": ""
                }
            ]
        },
        {
            "path": "/iplant/home/ipcuser/folder_1/library1",
            "metadata": [
                {
                    "attr": "template_item",
                    "value": "lib-1",
                    "unit": ""
                },
                {
                    "attr": "template_postal_code",
                    "value": "85719",
                    "unit": ""
                },
                {
                    "attr": "template_department",
                    "value": "BIO5",
                    "unit": ""
                },
                {
                    "attr": "template_institution",
                    "value": "iPlant",
                    "unit": ""
                }
            ],
            "irods-avus": [
                {
                    "attr": "test-attr-1",
                    "value": "test-val-1",
                    "unit": ""
                },
                {
                    "attr": "test-attr-2",
                    "value": "test-val-2",
                    "unit": ""
                },
                {
                    "attr": "test-attr-3",
                    "value": "test-val-3",
                    "unit": ""
                }
            ]
        }
    ]
}
```

__Curl Command__:

    curl -sH "$AUTH_HEADER" -X POST "http://localhost:3000/secured/filesystem/metadata/csv-parser?template-id=e7e19316-dc88-11e4-a49a-77c52ae8901a&dest=/iplant/home/ipcuser/folder_1&src=/iplant/home/ipcuser/metadata.csv"

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
