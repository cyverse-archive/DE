---
layout: page
title: DE Deployment Instructions
root: ../../
---

* [Grouper Deployment Instructions](grouper.html)

## Updating the system packages

The system packages are those in the core repos for the distribution.  Extra packages, like httpd,
need to be manually updated for now.

    ansible-playbook -K -i inventories/... systems.yaml

## Deploying to Production

    ansible-playbook -i inventories/... -K deploy-all.yaml

This playbook just encapsulates and calls other playbooks in the correct order for a deployment.
It will deploy the UI and services, update the databases, and perform system updates.
