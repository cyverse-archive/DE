templeton
=========

Templeton is a service which indexes template metadata, as stored in the
`metadata` database and interacted with ordinarily through the `metadata`
service.

Templeton operates in three modes, controlled by the `--mode` command-line flag:

| Mode          | Operation |
| ------------- | --------- |
| `periodic`    | listens on a configured AMQP queue and recieves only `index.templates` and `index.all` messages to trigger complete reindexes of the metadata template information (similar to `Infosquito` and `monkey`) |
| `incremental` | listens on a configured AMQP queue and recieves messages describing incremental changes to be indexed (similar to `dewey`) |
| `full`        | completely reindexes the metadata template information once and then exits |

It expects a configuration file in YAML format passed by the `--config`
command-line flag. In all modes it requires configuration for elasticsearch and
postgresql; in periodic and incremental modes it requires AMQP configuration.

An example configuration can be found at `src/test/test_config.yaml`.
