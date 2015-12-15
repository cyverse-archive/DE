---
layout: page
title: DE API Documentation
---

Listing the config for Terrain
-----------------------------

__URL Path__: /admin/config

__HTTP Method__: GET

__Response Body__:

```json
{
    "terrain.app.environment-name": "de-2",
    "terrain.app.listen-port": "31325",
    <...>
}
```

You can get the general idea of the format for the response body from the above.


Status Check
------------

__URL Path__: /admin/status

__HTTP Method__: GET

__Response Body__:

```json
{
    "iRODS": true,
    "jex": true,
    "apps": true,
    "notificationagent": true
}
```

This check only checks to see if the services are responding to HTTP requests. It does not prove that the services are completely functional or configured correctly.
