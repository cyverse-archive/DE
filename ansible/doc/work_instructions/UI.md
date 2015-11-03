# UI Instructions

## Setup

### Generating UI configs locally.
Some additional setup is required to generate local ui configs.

1. First, on your local machine, add the following lines to your default ansible
hosts file:

        [ui]
        localhost ansible_connection=local
        [services]
        localhost ansible_connection=local

    On Linux, this file should be in `/etc/ansible/hosts`, or an a Mac it may be in
    `/usr/local/etc/ansible/hosts`. Use `ansible --help` and check the
    `-i INVENTORY, --inventory-file=INVENTORY` help text to see where the default is on your machine.

1. Next, again on your local machine, cd into this repo's `host_vars` directory and create a symlink
to the `group_vars` environment of the services where you want your UI to connect, but name the link
`localhost`.

So, if you want to generate your configs for the `de-2` environment, you would do
the following:

    cd host_vars
    ln -s ../group_vars/de-2 localhost

If you want your configs to point to `gargery` instead, do the following

    cd host_vars
    rm localhost
    ln -s ../group_vars/gargery localhost

So, what does this do?

The first step is necessary so we don't have to include an inventory when
running our play locally. The second step is necessary to populate default variables
set in `group_vars`.

Also, it is no longer necessary to manually set your local IP address. Again,
take a look in `local_dev/ui.json`, and you will see this line;
`"app_server_base_url": "http://{{ ansible_default_ipv4.address }}:8080"`

Be sure to comment out the lines in `/etc/ansible/hosts` which do not apply.

__OK!! GREAT!! How do I generate the configs?__

    ansible-playbook ui.yaml -K --tags "local_config" --extra-vars="@local_dev/ui.json"

Use `--extra-vars "@vars_file.json"` for configs using local service overrides. For example, if you
are testing both the UI and Donkey locally, but want to use all other de-2 services:

    ansible-playbook ui.yaml -K --tags "local_config" --extra-vars="@local_dev/ui.json" --extra-vars="@local_dev/donkey.json"

## Updating the UI

There is only one playbook for deploying the UI.
The `group_vars` files determine which Docker tag the playbook obtains the UI image from.

* Be sure you have followed the instructions under `Setting Up Your Accounts` so that your ssh keys
  are setup between the server the wars are being deployed on.


    ansible-playbook -i inventories/... -K [-u <user>] ui.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional).
