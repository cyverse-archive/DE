---
layout: page
title: DE API Documentation
---

This document describes the tags resource.

A _tag_ is a user-defined label that can be attached to files and folders to relate them to each other.

# Endpoints

## Creating a tag

`POST /secured/tags/user`

Delegates to metadata: `POST /tags/user`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

## Update a tag's label and/or description

`PATCH /secured/tags/user/{tag-id}`

Delegates to metadata: `PATCH /tags/user/{tag-id}`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

## Delete a tag

`DELETE /secured/tags/user/{tag-id}`

Delegates to metadata: `DELETE /tags/user/{tag-id}`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

## Suggest a tag

`GET /secured/tags/suggestions`

Delegates to metadata: `GET /tags/suggestions`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

## Attaching or detaching multiple tags to a file or folder

`PATCH /secured/filesystem/entry/{entry-id}/tags`

Delegates to metadata: `PATCH /filesystem/entry/{entry-id}/tags`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

### Response

In addition to the metadata service responses, this service may also return the following responses:

| Status Code | Cause |
| ----------- | ----- |
| 404         | The `{entry-id}` UUID doesn't belong to a known file or folder or the file or folder isn't readable by the authenticated user. |


Error responses may include a `reason` field, providing a short, human readable explanation of the failure.

## Listing attached tags

`GET /secured/filesystem/entry/{entry-id}/tags`

Delegates to metadata: `GET /filesystem/entry/{entry-id}/tags`

This endpoint is a passthrough to the metadata endpoint above.
Please see the metadata documentation for more information.

### Response

In addition to the metadata service responses, this service may also return the following responses:

| Status Code | Cause |
| ----------- | ----- |
| 404         | The `{entry-id}` UUID doesn't belong to a known file or folder or the file or folder isn't readable by the authenticated user. |

Error responses may include a `reason` field, providing a short, human readable explanation of the failure.
