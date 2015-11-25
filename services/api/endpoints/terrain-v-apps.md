---
layout: page
title: DE API Documentation
root: ../../../
---

# Table of Contents

* [Overview](#overview)
    * [Calling Secured Terrain Endpoints](#calling-secured-terrain-endpoints)
    * [Calling "Secured" Apps Endpoints](#calling-secured-apps-endpoints)
    * [A Simple Example](#a-simple-example)

# Overview

The "secured" endpoints in apps behave a little bit differently from the
secured endpoints in Terrain. Specifically, the secured endpoints in Terrain
actually require user authentication whereas those in apps merely require
information about the user making the request.

## Calling Secured Terrain Endpoints

All secured endpoints in Terrain require a query string parameter, `proxyToken`,
whose value contains a CAS service ticket that Terrain can then use to verify
that the user has authenticated with CAS. This query string parameter must be
provided in addition to all of the other query string parameters required by the
service itself.

The utility that many DE developers use to generate CAS service tickets is a
command-line utility called `cas-ticket`, which is referenced in several
examples included in the Terrain documentation. You'll often see it embedded in
the URL in curl commands like this:

```
$ curl "http://somehost/path/to/endpoint?proxyToken=$(cas-ticket)"
```

This is simple command substitution in Bash. Bash calls the `cas-ticket` script
and inserts the output in place of `$(cas-ticket)`. The substitutions occur a
little bit differently in the regression test suites used by the Quality
Assurance deparment, but the idea is the same.

## Calling "Secured" Apps Endpoints

None of these endpoints are actually secured. They retain the "secured" label
because they're fronted by endpoints in Terrain that _are_ secure. That is, the
user interface doesn't hit the apps services directly. Instead, it sends
the request to a Terrain endpoint that forwards the request to a corresponding
endpoint in apps. The initial purpose of this separation was to provide
scalability and separation of concerns.

All secured endpoints in apps support four query-string parameters
containing user attributes:

* `user` - the username
* `email` - the user's email address
* `first-name` - the user's first name
* `last-name` - the user's last name

Not all of these parameters are required by every secured service in apps;
the only parameter that is required by all secured services is `user`. The rest
of the parameters are only required when the information in them is specifically
required. Terrain always passes all of these parameters to apps when it
forwards requests.

## A Simple Example

One of the simplest endpoints in both Terrain and apps is the /apps/categories endpoint, which
is used to obtain the list of app categories that are visible to the user.

The call to the Terrain service would look like this:

```
$ curl "http://by-tor:8888/secured/app-groups?proxyToken=$(cas-ticket)"
```

The equivalent call to the apps service would look like this:

```
$ curl "http://by-tor:9999/apps/categories?user=nobody&email=nobody@iplantcollaborative.org&first-name=Nobody&last-name=Inparticular"
```
