# Grouper Deployment

This document assumes that Grouper will be deployed on a host that is running
Docker. Grouper will be deployed in a Docker container running Apache Tomcat. A
separate Docker container running nginx as a reverse proxy will be used to
provide the SSL.

## Deploying Grouper

Two Ansible playbooks are required for this step. First, the `iplant_data`
container needs to be deployed:

```
ansible-playbook -i inventories/some/host.cfg -K data-container-services.yaml
```

Next, the `iplant-grouper` and `grouper-nginx` containers need to be deployed:

```
ansible-playbook -i inventories/some/host.cfg -K grouper.yaml
```
