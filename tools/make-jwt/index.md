---
layout: page
title: Test JWT Generator
---

The Test JWT generator, `make-jwt`, is a utility that can be used to test calls to authenticated Discovery Environment services.

# Synopsis

Creating a default JWT:

```
make-jwt -key-path    /path/to/rsa-private.key  \
         -key-pass    some-complicated-password \
         -username    terrainuser               \
         -email       terrainuser@example.com   \
         -given-name  Terrain                   \
         -family-name User                      \
         -name        Terrain User              \
         -entitlement a-group,another-group
```

Usage Message:

```
-email string
      email address to place in the token
-email-claim string
      claim name for email (default "email")
-entitlement string
      comma-separated list of groups
-entitlement-claim string
      claim name for entitlement (default "org.iplantc.de:entitlement")
-family-name string
      family name to place in the token
-family-name-claim string
      claim name for family name (default "family_name")
-given-name string
      given name to place in the token
-given-name-claim string
      claim name for given name (default "given_name")
-key-pass string
      password used to open private key
-key-path string
      path to private key
-lifetime int
      seconds before tokens expire (default 300)
-name string
      name to place in the token
-name-claim string
      claim name for full name (default "name")
-username string
      username to place in the token
-username-claim string
      claim name for username (default "sub")
```

# Introduction

Terrain, the primary REST API used by the Discovery Environment accepts signed JSON Web Tokens (JWTs) for authentication. JWTs are fully described in [RFC7519](https://tools.ietf.org/html/rfc7519) but it's not necessary to have all of the details in order to understand how Terrain uses them.

Service authentication requires a trust relationship to exist between the Discovery Environment UI (DE) and Terrain. That is, Terrain, has to trust all requests that come from the DE, provided that it can verify the origin of the request with some degree of certainty. The DE is expected to authenticate and obtain some information about the user. Once the information is obtained, the DE creates a JSON object containing this information. This JSON object serves as the _payload_ of the JWT, which is included in the `X-Iplant-De-Jwt` header of all HTTP requests that are sent to authenticated endpoints in Terrain. The header also contains two other pieces of information. The first component is a JOSE header, which is described in [RFC7515](https://tools.ietf.org/html/rfc7515) and contains information about how the JWT is signed. The third component is the signature, which contains a cryptographic signature that can be used to verify the origin of the JWT. Terrain only accepts JWTs that are signed by one of the cryptographic keys that it trusts, which is how it ensures that it can trust the information in the JWT payload.

# Default JWT Payload Format

As mentioned above, the JWT itself is just a JSON object. The claim names are the keys used in the JSON object. The claim values (that is, the user details) are the values in the JSON object. The default payload format looks something like this:

```json
{
    "sub": "<username>",
    "exp": "<expiration-time-as-seconds-since-epoch>",
    "email": "<email-address>",
    "given_name": "<given-name>",
    "family_name": "<family-name>",
    "name": "<full-name>",
    "org.iplantc.de:entitlement": [ "<group1>", "<group2>" ]
{
```

Most of the claims use values directly from the command line. The only exceptions are the `org.iplantc.de:entitlement` and `exp` claims. For the former, `make-jwt` converts a comma-delimited string provided on the command-line to a JSON array. For the latter, `make-jwt` determines the current unix timestamps and adds the number of seconds provided in the `lifetime` option (or 300 seconds if the `lifetime` option is not specified).

# Alternate Claim Names

Terrain currently accepts two sets of claim names. The default claim names specified above and the set of claim names used by WSO2, which is required to allow Terrain to accept authenticated API calls from Agave. The claim names can be customized using command-line arguments as follows:

```
make-jwt -key-path          /path/to/rsa-private.key            \
         -key-pass          some-complicated-password           \
         -username-claim    http://wso2.org/claims/enduser      \
         -username          terrainuser                         \
         -email-claim       http://wso2.org/claims/emailaddress \
         -email             terrainuser@example.com             \
         -given-name-claim  http://wso2.org/claims/givenname    \
         -given-name        Terrain                             \
         -family-name-claim http://wso2.org/claims/lastname     \
         -family-name       User                                \
         -name-claim        http://wso2.org/claims/fullname     \
         -name              Terrain User
```

Note that Agave does not currently send the list of groups to which the user belongs. For this reason, administrative endpoints cannot be called from Agave.

`make-jwt` does not currently validate the claim names provided on the command line, so it's possible to use any claim name for any of the claims supported by the utility. The sets of claims described above are the only ones accepted by Terrain at the time of this writing, however.

# Parameter Files

Specifying all of this information on the command line can be cumbersome, especially when specifying information that needs to be the same for every JWT that is formatted (for example, the path to the private key and the password used to access it). To make life a little bit easier, `make-jwt` supports parameter files.

Parameter files are plain text files named `.make-jwt` containing key-value pairs. The utility will search for parameter files in the current working directory and in the current user's home directory. Keys and values are separated by equal signs and optional whitespace (as in a Java properties file). Blank lines and lines beginning with a hash mark are ignored. For example:

```
# Signing key information.
key-path = /path/to/rsa-private.key
key-pass = some-complicated-password

# Claim names.
# username-claim    = http://wso2.org/claims/enduser
# email-claim       = http://wso2.org/claims/emailaddress
# given-name-claim  = http://wso2.org/claims/givenname
# family-name-claim = http://wso2.org/claims/lastname
# name-claim        = http://wso2.org/claims/fullname
```

In this parameter file, the path to the private key and the password used to access the key are present in the file. The custom claim names used by WSO2 are also present, but they're commented out so that they will be ignored unless the file is modified.

Because of a limitation in the current implementation, parameter files always take precedence over command-line arguments. For this reason, options that may need to be customized should not be included in a parameter file at this time. The order of precedence is as follows:

1. The parameter file in the current working directory, if present.
1. The parameter file in the current user's home directory, if present.
1. Options specified on the command line.

# Signing Key Format

The private key used by make-jwt must be an RSA private key in PEM format. A key pair can be generated using the following commands:

```
openssl genrsa -aes256 -out privkey.pem 2048
openssl rsa -pubout -in privkey.pem -out pubkey.pem
```

You will be prompted for a passphrase when the private key is created and when you generate the public key. This is the same passphrase that `make-jwt` needs in order to access the private key.

# Making Requests

As mentioned above, Terrain accepts JWTs in the default format in the custom HTTP header, `X-Iplant-De-Jwt`. JWTs that are sent in this header must use the default claim names for them to be accepted, for example:

```
curl -sH "X-Iplant-De-Jwt: $JWT" "https://terrain-host/apps?search=word"
```

Terrain may also accept JWTs from Agave, which requires the JWT to be included in a different header. The name of this header is configurable and can be specified in the configuration property, `terrain.wso2.jwt-header`. If the name of the header is `x-jwt-assertion-some_tenant`, for example, then a request can be made as follows:

```
curl -sH "x-jwt-assertion-some_tenant: $JWT" "https://terrain-host/apps?search-word"
```
