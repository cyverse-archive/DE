# test-ldap

This image is largely a copy of https://github.com/dinkel/docker-openldap. Because this is a test server, however,
the following changes have been made.

1. The directories, `/etc/ldap` and `/var/lib/ldap`, are not exposed as volumes so that the image itself may
   contain test LDAP entries.

1. The administrative password defaults to `notprod`.

1. The LDAP domain defaults to `example.org`.

1. The additional schema, `openldap`, is loaded automatically.

1. Several test groups and users are loaded by default.
