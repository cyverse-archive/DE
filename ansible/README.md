# Discovery Environment Ansible Usage

## Using this Repository

This repository is organized with group_vars defined with some default values in
`inventories/group_vars/all`, with the intention that these playbooks are used with inventory files
created in this `inventories` directory (use a `.cfg` extension to prevent committing the files) and
group_vars files containing private and overriding values in a top-level `group_vars` directory.

## Setup
* [Ansible](doc/setup/Ansible.md)
* [Docker](doc/setup/Docker.md)

## Work Instructions
* [Backend Deploy/Update Instructions](doc/work_instructions/Backend.md)
* [UI Deploy/Update Instructions](doc/work_instructions/UI.md)
* [Database Instructions](doc/work_instructions/Database.md)

## Builds
* [Generating GPG keys for Donkey](doc/builds/gpg-key.md)

## Deployment Processes
* [Deployment Process](doc/deployments/Deployments.md)
* [Grouper Deployment](doc/deployments/grouper.md)
