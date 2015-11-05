# Generating service configs locally.

Some additional setup is required to generate local service configs.

1. First, on your local machine, create an `ansible/inventories/localhost.cfg` file (it's easiest to copy
    it from an existing environment's inventory) then add the following line to the top of the file:

        [localhost]
        localhost ansible_connection=local

1. For each service that will be running locally, change its host name to
    `localhost ansible_connection=local`, as above. For example, if generating configs for a service that
    will connect to a local data-info service, change the `[data-info]` entry to the following:

        [data-info]
        localhost ansible_connection=local

1. Next, again on your local machine, cd into this repo's `ansible/host_vars` directory and create a
    symlink to the `group_vars` environment you are developing against, but name the link `localhost`.

    So if you want to generate your configs for a `dev` environment, you would do the following:

        cd /path/to/DE/ansible/host_vars
        ln -s ../group_vars/dev localhost

    If you want your configs to point to `staging` instead, do the following:

        cd /path/to/DE/ansible/host_vars
        rm localhost
        ln -s ../group_vars/staging localhost

So, what does this do?

The first step is necessary since `local-service-config.yaml` requires a `localhost` inventory group.
The third step is necessary to populate unset and default variables in
`inventories/group_vars/all`.

__OK!! GREAT!! How do I generate the configs?__

    ansible-playbook local-service-config.yaml -i inventories/localhost.cfg --extra-vars="service=service-name"

See the comments in `local-service-config.yaml` for more options on generating configs.