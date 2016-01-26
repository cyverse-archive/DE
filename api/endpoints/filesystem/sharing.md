---
layout: page
title: DE API Documentation
---

Sharing & Unsharing
-------------------

Please see the /secured/share and /secured/unshare endpoints.

Sharing files with the anonymous user
-------------------------------------

Shares files with the anonymous user. It gives the anonymous user read access. All paths must be files.

__URL Path__: /secured/filesystem/anon-files

__HTTP Method__: POST

__ERROR CODE__: ERR_NOT_A_USER, ERR_BAD_OR_MISSING_FIELD, ERR_DOES_NOT_EXIST, ERR_NOT_OWNER, ERR_NOT_A_FILE

__Request Body__:

```json
{
    "paths" :["/path/to/a/file"]
}
```

__Curl Command__:

    curl -H "$AUTH_HEADER" -d '{"paths" : ["/path/to/a/file"]}' http://example.org/secured/filesystem/anon-files

__Response Body__:

```json
{
    "paths" : {
      "/path/to/a/file" : "http://URL-to-file/"
    },
    "user" : "username"
}
```
