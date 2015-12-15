---
layout: page
title: DE API Documentation
---

# Table of Contents

* [EZID Requests](#ezid-requests)
    * [List all EZID Requests](#list-all-ezid-requests)
    * [Get any EZID Request Details](#get-any-ezid-request-details)
    * [Update the Status of an EZID Request](#update-the-status-of-an-ezid-request)
    * [List EZID Requests](#list-ezid-requests)
    * [Create an EZID Requests](#create-an-ezid-requests)
    * [List EZID Request Status Codes](#list-ezid-request-status-codes)
    * [List EZID Request Types](#list-ezid-request-types)
    * [List EZID Request Details](#list-ezid-request-details)

# EZID Requests

These endpoints create and manage user requests for a variety of persistent identifiers,
including ARKs and DataCite DOIs, for data the user wishes to make public.

The persistent identifiers will be created and managed using the [EZID](http://ezid.cdlib.org/) API.

## List all EZID Requests

`GET /admin/ezid-requests`

Delegates to data-info: `POST /stat-gatherer`

Allows administrators to list EZID Requests from all users.

### Response

```json
{
    "ezid_requests": [
        {
            "id": "The EZID Requests's UUID",
            "type": "The type of persistent identifier requested",
            "folder": {},
            "requested_by": "The username of the user that submitted the EZID Request",
            "date_submitted": 123456789, // The timestamp of the EZID Request submission
            "status": "The current status of the EZID Request",
            "date_updated": 123456789, // The timestamp of the last EZID Request status update
            "updated_by": "The username of the user that last updated the EZID Request status"
        }
    ]
}
```

The `folder` object is populated by the corresponding data-info endpoint.
Please see the data-info endpoint's documentation for more details.

## Get any EZID Request Details

`GET /admin/ezid-requests/{request-id}`

Delegates to data-info: `POST /stat-gatherer`

Allows administrators to retrieve details for an EZID Request from any user.

### Response

```json
{
    "id": "The EZID Requests's UUID",
    "type": "The type of persistent identifier requested",
    "folder": {},
    "requested_by": "The username of the user that submitted the EZID Request",
    "history": [
        {
            "status": "(optional) The status code of the EZID Request update",
            "status_date": 123456789, // The timestamp of the EZID Request status update
            "updated_by": "The username that updated the EZID Request status",
            "comments": "(optional) The curator comments of the EZID Request status update"
        }
    ]
}
```

The `folder` object is populated by the corresponding data-info endpoint.
Please see the data-info endpoint's documentation for more details.

## Update the Status of an EZID Request

`POST /admin/ezid-requests/{request-id}/status`

Allows administrators to update the status of an EZID Request from any user.

### Request

```json
{
    "status": "(optional) The status code of the EZID Request update.
                          The status code is case-sensitive,
                          and if it isn't defined in the database
                          already then it will be added to the list of
                          known status codes",
    "comments": "(optional) The administrator comments of the
                            EZID Request status update"
}
```

### Response

Same as [GET /admin/ezid-requests/{request-id}](#get-any-ezid-request-details)

## List EZID Requests

`GET /ezid-requests`

Lists all EZID Requests submitted by the requesting user.

### Response

Same as [GET /admin/ezid-requests](#list-all-ezid-requests)

## Create an EZID Requests

`POST /ezid-requests`

Creates an EZID Request for the requesting user.

### Request

```json
{
    "type": "The type of persistent ID requested",
    "folder": "The UUID of the data folder for which the persistent ID is being requested"
}
```

### Response

Same as [GET /admin/ezid-requests/{request-id}](#get-any-ezid-request-details)

## List EZID Request Status Codes

`GET /ezid-requests/status-codes`

Lists all EZID Request Status Codes that have been assigned to a request status update.
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

## List EZID Request Types

`GET /ezid-requests/types`

Lists the allowed EZID Request Types the user can select when submitting a new request.

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

## List EZID Request Details

`GET /ezid-requests/{request-id}`

Allows a user to retrieve details for one of their EZID Request submissions.

### Response

Same as [GET /admin/ezid-requests/{request-id}](#get-any-ezid-request-details)
