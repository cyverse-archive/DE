#/bin/bash

error_exit () {
    echo "$1" 1>&2
    exit 1
}

# Check the usage.
if [ $# -ne 1 ]; then
    error_exit "Usage: $(basename $0) ldif-file"
fi

# Save the argument.
ldif_file="$1"

# Verify that the file exits and is a regular file.
if [ ! -f "$ldif_file" ]; then
    error_exit "File does not exist or is not a regular file: $ldif_file"
fi

# Start LDAP up in the background.
/entrypoint.sh slapd -d 32768 -u openldap -g openldap &
SLAPD_PID=$!

# Sleep briefly to wait for LDAP to start up.
sleep 2

# Add the test DE users.
ldapadd -h localhost -xcD 'cn=admin,dc=example,dc=org' -w "$SLAPD_PASSWORD" -f "$ldif_file" \
    || error_exit "Unable to load LDAP users"

# Sleep briefly to wait for the LDAP database to be updated.
sleep 2

# Kill the LDAP server and wait for it to stop.
kill -s INT "$SLAPD_PID"
wait "$SLAPD_PID"

exit 0
