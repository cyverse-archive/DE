# integrationator

A Clojure utility designed to add user IDs to the integration_data table of the DE apps database.

## Usage

```
Usage: java -jar integrationator-standalone.jar [options]

Options:
  -l, --ldap-host HOST                                             LDAP host name or IP address, optionally with a
                                                                   colon and a port number
  -b, --ldap-base BASE    ou=People,dc=iplantcollaborative,dc=org  LDAP search base
  -D, --user-domain NAME  iplantcollaborative.org                  user domain name
  -h, --db-host HOST      localhost                                database host name
  -p, --db-port PORT      5432                                     database port number
  -d, --db-name NAME      de                                       database name
  -U, --db-user USER      de                                       database user
  -?, --help                                                       display the help message
```

## License

http://www.cyverse.org/sites/default/files/iPLANT-LICENSE.txt
