# Starting and stopping services

To start a service managed by supervisord do this:

    sudo supervisorctl start <service>

Example

    sudo supervisorctl start jex

To restart all services managed by supervisord:

    sudo supervisorctl restart all

To start a supervisorctl shell:

    sudo supervisorctl

To reload (and restart) supervisord configs:

    sudo supervisorctl reload

# Configuration

The configuration files for the supervisored services are in the following locations:

    /etc/supervisord.conf

That contains the main, non-service specific configuration for supervisord. It's been configured to grab all of the .ini files out of this directory for service configurations:

    /etc/iplant/de/supervisord/