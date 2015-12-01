---
layout: page
title: DE API Documentation
root: ../../../../
---

Emptying a User's Trash Directory
---------------------------------
__URL Path__: /secured/filesystem/trash

__HTTP Method__: DELETE

__Error Codes__: ERR_NOT_A_USER

__Request Query Parameters__:

__Response__:

    {
        "trash" : "/path/to/user's/trash/dir/",
        "paths" : [
                "/path/to/deleted/file",
        ]
    }

__Curl Command__:

    curl -H "$AUTH_HEADER" -X DELETE http://127.0.0.1:3000/secured/filesystem/trash
