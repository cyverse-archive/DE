---
layout: page
title: DE Permanent ID Request API Documentation
---

# Table of Contents

* [Permanent ID Requests](#permanent-id-requests)
    * [List all Permanent ID Requests](#list-all-permanent-id-requests)
    * [Get any Permanent ID Request Details](#get-any-permanent-id-request-details)
    * [Update the Status of a Permanent ID Request](#update-the-status-of-a-permanent-id-request)
    * [Create a Permanent ID](#create-a-permanent-id)
    * [List Permanent ID Requests](#list-permanent-id-requests)
    * [Create a Permanent ID Request](#create-a-permanent-id-request)
    * [List Permanent ID Request Status Codes](#list-permanent-id-request-status-codes)
    * [List Permanent ID Request Types](#list-permanent-id-request-types)
    * [List Permanent ID Request Details](#list-permanent-id-request-details)

# Permanent ID Requests

These endpoints create and manage user requests for a variety of persistent identifiers,
including ARKs and DataCite DOIs, for data the user wishes to make public.

The persistent identifiers will be created and managed using the [EZID](http://ezid.cdlib.org/) API.

# Workflow Overview

1. User reviews [DOI FAQ page on wiki](https://pods.iplantcollaborative.org/wiki/display/DC/Requesting+a+Permanent+Identifier+in+the+CyVerse+Data+Commons+Repository)
   and determines that a CyVerse DOI is appropriate for their data.
2. User organizes their data in a single folder per Permanent ID, according to very general CyVerse guidelines.
3. Based on what they learned from tutorial, user will complete DataCite metadata template on that folder.
4. User [creates a Permanent ID Request](#create-a-permanent-id-request)
   for this folder.
    * Request must be for one of the [available Permanent ID Request types](#list-permanent-id-request-types).
    * Request triggers the validation check within the DE of the metadata and folder name
      (must not conflict with any folder names in the data commons repo `staging` or `curated` folders).
    * If pass:
        * Results of request are emailed to curation team.
        * Folder automatically moved to data commons repo `staging` folder.
            * Curators automatically given `own` permission.
            * User automatically given `write` permission.
        * User may [view all Permanent ID Requests](#list-permanent-id-requests) they have submitted.
        * User may [view details for any of their Permanent ID Requests](#list-permanent-id-request-details).
    * If fail, user returns to Step 3.
5. Curator finds request with [Permanent ID Request listing](#list-all-permanent-id-requests).
    * Curator may [view the Permanent ID Request details](#get-any-permanent-id-request-details),
      which includes the request's status history (initially only `Submitted`).
    * Curator checks metadata and data structure.
    * Curator may [update the status of the Permanent ID Request](#update-the-status-of-a-permanent-id-request)
      in this or any subsequent step.
        * Curator may use any previously created
          [Permanent ID Request Status Codes](#list-permanent-id-request-status-codes),
          or add a new status (which is saved for future reuse).
    * If pass or minor changes that can be made by curator: go to Step 6
    * If changes needed by user: Curator emails user and asks them to make changes
    * Once corrections are made, go to Step 6.
6. Curator [creates Permanent ID](#create-a-permanent-id) for this request.
    * Folder name must not conflict with any folder names in the data commons repo `curated` folder.
    * Metadata from folder is submitted to [EZID](http://ezid.cdlib.org/) API
      in order to create the requested Permanent ID.
    * Fails:
        * Permanent ID not generated and error reported to curator.
        * [Request status automatically updated](#update-the-status-of-a-permanent-id-request) to `Failed`.
        * Curator corrects any errors.
        * Repeat Step 6 until pass.
    * Passes:
        * Permanent ID is generated and notification sent to curator and user.
        * [Request status automatically updated](#update-the-status-of-a-permanent-id-request) to `Completion`.
        * Folder metadata automatically updated to include new Permanent ID and current date.
        * Folder automatically moved to data commons repo `curated` folder.
            * Curators automatically given `own` permission.
            * Public automatically given `read` permission.
        * Curator checks that data is public and visible on mirrors,
          that metadata appears correct on the [EZID](http://ezid.cdlib.org/) landing page,
          and that the Permanent ID redirect works.

## List all Permanent ID Requests

`GET /admin/permanent-id-requests`

Delegates to data-info: `POST /stat-gatherer`

Allows administrators to list Permanent ID Requests from all users.

### Response

```json
{
    "requests": [
        {
            "id": "The Permanent ID Requests's UUID",
            "type": "The type of persistent identifier requested",
            "folder": {},
            "requested_by": "The username of the user that submitted the Permanent ID Request",
            "date_submitted": 123456789, // The timestamp of the Permanent ID Request submission
            "status": "The current status of the Permanent ID Request",
            "date_updated": 123456789, // The timestamp of the last Permanent ID Request status update
            "updated_by": "The username of the user that last updated the Permanent ID Request status"
        }
    ]
}
```

The `folder` object is populated by the corresponding data-info endpoint.
Please see the data-info endpoint's documentation for more details.

## Get any Permanent ID Request Details

`GET /admin/permanent-id-requests/{request-id}`

Delegates to data-info: `POST /stat-gatherer`

Allows administrators to retrieve details for a Permanent ID Request from any user.

### Response

```json
{
    "id": "The Permanent ID Requests's UUID",
    "type": "The type of persistent identifier requested",
    "folder": {},
    "requested_by": {
        "username": "The username of the user that submitted the Permanent ID Request",
        "firstname": "The first name of the user that submitted the Permanent ID Request",
        "lastname": "The last name of the user that submitted the Permanent ID Request",
        "email": "The email of the user that submitted the Permanent ID Request",
        "institution": "The institution of the user that submitted the Permanent ID Request"
    },
    "history": [
        {
            "status": "(optional) The status code of the Permanent ID Request update",
            "status_date": 123456789, // The timestamp of the Permanent ID Request status update
            "updated_by": "The username that updated the Permanent ID Request status",
            "comments": "(optional) The curator comments of the Permanent ID Request status update"
        }
    ]
}
```

The `folder` object is populated by the corresponding data-info endpoint.
Please see the data-info endpoint's documentation for more details.

## Update the Status of a Permanent ID Request

`POST /admin/permanent-id-requests/{request-id}/status`

Allows administrators to update the status of a Permanent ID Request from any user.

### Request

```json
{
    "status": "(optional) The status code of the Permanent ID Request update.
                          The status code is case-sensitive,
                          and if it isn't defined in the database
                          already then it will be added to the list of
                          known status codes",
    "comments": "(optional) The administrator comments of the
                            Permanent ID Request status update"
}
```

### Response

Same as [GET /admin/permanent-id-requests/{request-id}](#get-any-permanent-id-request-details)

## Create a Permanent ID

`POST /admin/permanent-id-requests/{request-id}/ezid`

Delegates to metadata: `POST /admin/permanent-id-requests/{request-id}/status`

This endopint will mint a permanent ID using the
[EZID API](http://ezid.cdlib.org/doc/apidoc.html#operation-mint-identifier)
and the requested folder's metadata,
add the new ID(s) to the folder's metadata,
move the folder to a curated directory,
then set the Permanent ID Request's status to "Completed".
If an error is encountered during this process,
then the Permanent ID Request's status will be set to "Failed".

### Response

Same as [GET /admin/permanent-id-requests/{request-id}](#get-any-permanent-id-request-details)

## List Permanent ID Requests

`GET /permanent-id-requests`

Lists all Permanent ID Requests submitted by the requesting user.

### Response

Same as [GET /admin/permanent-id-requests](#list-all-permanent-id-requests)

## Create a Permanent ID Request

`POST /permanent-id-requests`

Creates a Permanent ID Request for the requesting user.

### Request

```json
{
    "type": "The type of persistent ID requested",
    "folder": "The UUID of the data folder for which the persistent ID is being requested"
}
```

### Response

Same as [GET /admin/permanent-id-requests/{request-id}](#get-any-permanent-id-request-details)

## List Permanent ID Request Status Codes

`GET /permanent-id-requests/status-codes`

Lists all Permanent ID Request Status Codes that have been assigned to a request status update.
This allows a status to easily be reused by admins in future status updates.

### Response

```json
{
    "status_codes": [
        {
            "id": "The Status Code's UUID",
            "name": "The Status Code",
            "description": "A brief description of the Status Code"
        }
    ]
}
```

## List Permanent ID Request Types

`GET /permanent-id-requests/types`

Lists the allowed Permanent ID Request Types the user can select when submitting a new request.

### Response

```json
{
    "status_codes": [
        {
            "id": "The Request Type's UUID",
            "type": "The Request Type",
            "description": "A brief description of the Request Type"
        }
    ]
}
```

## List Permanent ID Request Details

`GET /permanent-id-requests/{request-id}`

Allows a user to retrieve details for one of their Permanent ID Request submissions.

### Response

Same as [GET /admin/permanent-id-requests/{request-id}](#get-any-permanent-id-request-details)
