# sharkbait

A Clojure utility used to perform tasks in grouper that cannot be performed via the Grouper web services. The DE uses
this to create its permission definitions, among other things.

## Usage

Running `sharkbait` via an executable JAR file:

```
$ java -jar /path/to/sharkbait-standalone.jar
```

Running `sharkbait` via a Docker container:

```
$ docker run --rm -it -v /etc/grouper:/etc/grouper discoenv/sharkbait
```

## Options

```
  -?, --help                                 Show help.
  -h, --host HOST                localhost   The database hostname.
  -p, --port PORT                5432        The database port number.
  -d, --database DATABASE        de          The database name.
  -U, --user USER                de          The database username.
  -v, --version                              Show the sharkbait version.
  -e, --environment ENVIRONMENT  dev         The name of the DE environment.
  -u, --grouper-user USER        de_grouper  The username that the DE uses to authenticate to Grouper
```

## Prerequisites

This utility expects the Grouper configuration files to be present in `/etc/grouper` on the local host before it can be
executed. It is generally best to run this utility on the Grouper host itself. This utility also expects an account
with a username matching the value of the `--grouper-user` option to exist in the LDAP directory that is used as
Grouper's subject source.

## Purpose

The purpose of this utility is to initialize Grouper for use with the Discovery Environment. At this time, sharkbait
adds several initial folders to Grouper and creates some permission definitions.

Folders:

* iplant:de:analyses
* iplant:de:apps
* iplant:de:users

Permission Definitions:

* iplant:de:analyses:analysisPermissionDef
* iplant:de:apps:appPermissionDef

This utility is designed to be idempotent so that it can be run multiple times on the same Grouper deployment without
doing any damage or encountering an error.

## License

http://iplantcollaborative.org/sites/default/files/iPLANT-LICENSE.txt
