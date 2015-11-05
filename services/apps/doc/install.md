# Table of Contents

* [Installing and Configuring apps](#installing-and-configuring-apps)
    * [Primary Configuration](#primary-configuration)
    * [Logging Configuration](#logging-configuration)

# Installing and Configuring apps

apps is packaged as an RPM and published in iPlant's YUM repositories.
It can be installed using `yum install apps` and upgraded using
`yum upgrade apps`.

## Primary Configuration

apps gets its configuration settings from a configuration file. The path
to the configuration file is given with the --config command-line setting.

Here's an example configuration file:

```properties
# Connection details.
apps.app.listen-port = 60000

# Route-independent feature flags.
apps.features.agave      = true
apps.features.agave.jobs = true

# Database settings.
apps.db.driver      = org.postgresql.Driver
apps.db.subprotocol = postgresql
apps.db.host        = localhost
apps.db.port        = 5432
apps.db.name        = de
apps.db.user        = de
apps.db.password    = somepassword

# JEX connection settings.
apps.jex.base-url = http://localhost:8889

# Data Info connection settings.
apps.data-info.base-url = http://localhost:8890

# Workspace app group names.
apps.workspace.root-app-group            = Workspace
apps.workspace.default-app-groups        = ["Apps under development","Favorite Apps"]
apps.workspace.dev-app-group-index       = 0
apps.workspace.favorites-app-group-index = 1
apps.workspace.beta-app-category-id      = 665F28B8-2336-4780-A26D-29F608082FD2
apps.workspace.public-id                 = 00000000-0000-0000-0000-000000000000

# The domain name to append to the user id to get the fully qualified user id.
apps.uid.domain = example.org

# The path to the home directory in iRODS.
apps.irods.home = /example/home

# Batch job settings.
apps.batch.group               = batch_processing
apps.batch.path-list.info-type = ht-analysis-path-list
apps.batch.path-list.max-paths = 16
apps.batch.path-list.max-size  = 1048576

# Agave connection settings.
apps.agave.base-url             = https://localhost/agave
apps.agave.key                  = D381A69F-7EF8-4BA4-BB21-4C13722E2355
apps.agave.secret               = ED9FA012-8A1A-4EFB-9122-27BAF8CD2B1A
apps.agave.oauth-base           = https://localhost/agave/oauth2
apps.agave.oauth-refresh-window = 5
apps.agave.redirect-uri         = https://localhost/de/oauth/callback/agave
apps.agave.storage-system       = localhost

# Agave callback settings.
apps.agave.callback-base = https://localhost/de/agave-cb

# PGP Settings
apps.pgp.keyring-path = /path/to/secring.gpg
apps.pgp.key-password = C7E70F82-66F1-4213-B95F-03B31519B9D8

# Notification agent connection settings.
apps.notificationagent.base-url = http://localhost:8891
```

Generally, the database and service connection settings will have to be
updated for each deployment.

## Logging Configuration

The logging settings are stored in `/etc/apps/log4j.properties`.  The file
looks like this by default:

```properties
log4j.rootLogger=WARN, A

# Uncomment these lines to enable debugging for iPlant classes.
# log4j.category.org.iplantc=DEBUG, A
# log4j.additivity.org.iplantc=false

# Uncomment these lines to enable debugging in apps itself.
# log4j.category.apps=DEBUG, A
# log4j.additivity.apps=false

# Uncomment these lines to enable debugging in iPlant Clojure Commons.
# log4j.category.clojure-commons=DEBUG, A
# log4j.additivity.clojure-commons=false

# Either comment these lines out or change the appender to B when running
# apps in the foreground.
log4j.logger.apps.util.json=debug, JSON
log4j.additivity.apps.util.json=false

log4j.logger.clojure-commons.config = INFO

# Use this appender for logging JSON when running apps in the background.
log4j.appender.JSON=org.apache.log4j.RollingFileAppender
log4j.appender.JSON.File=/var/log/apps/json.log
log4j.appender.JSON.layout=org.apache.log4j.PatternLayout
log4j.appender.JSON.layout.ConversionPattern=%d{MM-dd@HH:mm:ss} %-5p (%13F:%L) %3x - %m%n
log4j.appender.JSON.MaxFileSize=10MB
log4j.appender.JSON.MaxBackupIndex=1

# Use this appender when running apps in the background.
log4j.appender.A=org.apache.log4j.RollingFileAppender
log4j.appender.A.File=/var/log/apps/apps.log
log4j.appender.A.layout=org.apache.log4j.PatternLayout
log4j.appender.A.layout.ConversionPattern=%d{MM-dd@HH:mm:ss} %-5p (%13F:%L) %3x - %m%n
log4j.appender.A.MaxFileSize=10MB
log4j.appender.A.MaxBackupIndex=1
```

The most useful configuration change here is to enable debugging for iPlant
classes, which can be done by uncommenting two lines.  In rare cases, it may
be helpful to enable debugging in apps and iPlant Clojure Commons.
Most of the logic in apps is implemented in Java classes that are
underneath the org.iplantc package, however, so enabling debugging for those
classes will be the most helpful.

See the [log4j documentation](http://logging.apache.org/log4j/1.2/manual.html)
for additional logging configuration instructions.
