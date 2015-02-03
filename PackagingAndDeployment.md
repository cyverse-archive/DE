# Changes

 * RPMs are now built with [fpm](https://github.com/jordansissel/fpm) through a project-specific build.sh script.

 * RPMs no longer stop and start services on installs, uninstalls, or upgrades. This gives us more control over service restarts and decreases the amount of time needed for deployments (for some reason it was really slow when done through RPM).

 * The uberjars installed by the RPMs no longer contain versioning information in their filenames. Versioning information can be obtained from the RPMs (<code>rpm -q rpm-name</code>)or from the --version that most services support (the rest will support it in the future). This makes creating and maintaining build scripts much, much easier.

 * We now use [supervisord](http://supervisord.org/) to control the daemonization of processes. This is more cross-platform than init scripts and will come in handy when we move to docker containers. Maintaining supervisord .ini configuration files is easier than maintaining init scripts as well. The supervisord configs are located in __/etc/iplant/de/supervisord/__, while the top-level supervisord.conf is located at __/etc/supervisord.conf__.
    * To start a service: `sudo supervisorctl start <service-name>`
    * To check the status of a service: `sudo supervisorctl status <service-name>`
    * To restart a service: `sudo supervisorctl restart <service-name>`
    * To start all services: `sudo supervisorctl start all`
    * To check all of the statuses: `sudo supervisorctl status`
    * To restart all services: `sudo supervisorctl restart`
    * To force supervisord to reload configs and restart services: `sudo supervisorctl reload`
