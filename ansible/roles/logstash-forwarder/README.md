Logstash-forwarder is a docker image that contains the logstash-forwarder app.

It is meant to be deployed to every host machine within the inventory.

All of our applications publish their logs to stdout/stderror. We previously used the docker
`--log-driver=syslog` parameter to forward all logs produced by our containers to the host machine's
syslog daemon, but there is a bug which causes the docker logging driver to stop logging the
container's STDOUT.  https://github.com/docker/docker/issues/13333

In light of this issue, we use the systemd service definition files to place the STDOUT/STDERR into
designated files. The Logstash-Forwarder config is set up to individually annotate each log type as
it is forwarded, which includes the host machine's syslog file (`/var/log/messages` for CENTOS) and
forward the messages to the configured remote logstash instance. The configuration file is kept in
the `data-container-services-create` role's _templates_ directory,
[logstash-forwarder-conf.json.j2](../data-container/templates/logstash-forwarder-conf.json.j2).

All log messages will have a `de_version` and `drop_number` field added to them.  These will default
to the `app_version` var from [group_vars/all](../../group_vars/all) and `ansible_date_time.date`
respectively.
