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
    "requested_by": "The username of the user that submitted the Permanent ID Request",
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
