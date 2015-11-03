# DE Database Instructions

## Updating the DE database

    ansible-playbook -i inventories/... -K [-u <user>] db-migrations.yaml

The -i, -K, and -u options are the same as in the other ansible commands.
