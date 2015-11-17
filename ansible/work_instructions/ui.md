---
layout: page
title: UI Ansible Instructions
root: ../../
---

## Setup

### Generating UI configs locally.
Some additional setup is required to generate local ui configs.

1. First, follow the instructions in the [local config setup docs](Local_Configs.md) for setting up
    `ansible/inventories/localhost.cfg` and `ansible/host_vars/localhost` on your dev machine.

1. Next, use the following command to generate the configs for the UI:

        ansible-playbook local-service-config.yaml -i inventories/localhost.cfg --extra-vars="@local_dev/ui.json"

## Updating the UI

There is only one playbook for deploying the UI.
The `group_vars` files determine which Docker tag the playbook obtains the UI image from.

* Be sure you have followed the instructions under `Setting Up Your Accounts` so that your ssh keys
  are setup between the server the wars are being deployed on.


    ansible-playbook -i inventories/... -K [-u <user>] ui.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional).
