---
layout: page
title: DE Ansible Documentation
---

# Table of Contents
* [Overview](#overview)
* [Setup](#setup)
* [Design](#design)
    * [Directory Layout](#directory-layout)
    * [Role-Separated Playbooks](#role-separated-playbooks)
    * [Inventories](#inventories)
* [Playbooks](#playbooks)

# Overview
[Ansible](http://www.ansible.com/) is an open source, agentless automation tool. The DE development
team uses Ansible to provision/update our servers and deploy the DE. However, for this repository,
we only expose the Ansible scripts that we use for deploying the DE. 

If you intend to use our ansible scripts, we highly suggest that you read the 
[Ansible documentation](http://docs.ansible.com/ansible/index.html).

# Setup

* [Ansible Setup](setup/ansible.html)
* [Docker-based Ansible Setup](setup/docker.html)

# Design
We have strived to follow Ansible's 
[best practices](http://docs.ansible.com/ansible/playbooks_best_practices.html). 
However, we have slightly diverged on the topics of 
[directory layout](http://docs.ansible.com/ansible/playbooks_best_practices.html#directory-layout) and
[role-separated top level playbooks](http://docs.ansible.com/ansible/playbooks_best_practices.html#top-level-playbooks-are-separated-by-role).

## Directory Layout
The ansible best practices for directory layout suggests using a `group_vars` folder, but you may 
have noticed that the repo doesn't contain a `ansible/group_vars` folder. Our default `group_vars` 
folder resides in the `inventories` folder; 
[ansible/inventories/group_vars]({{site.github.repository_url}}/tree/master/ansible/inventories/group_vars).
Also, this folder contains a single file, `all`, which contains all of the variables used by the 
provided roles and playbooks with default values.
This is done with intent that developers will create their own `group_vars/all` file, which will 
override any of the defaults set in the `inventories/group_vars/all` file.

You only need to include the variables you wish to override.

## Role-Separated Playbooks
We maintain role-separated playbooks, but they are kept in the 
[ansible/playbooks/]({{site.github.repository_url}}/tree/master/ansible/playbooks) folder. The 
remaining playbooks in the root of the `ansible/` folder are composite (utilize more than one role)
or one off playbooks (do not use any roles). 

If you wish to use these playbooks, we have created the 
[single-role.yaml]({{site.github.repository_url}}/tree/master/ansible/single-role.yaml) playbook. 
The documentation on its use is contained within the playbook.

## Inventories
We have provided an example inventory file; 
[example.cfg]({{site.github.repository_url}}/tree/master/ansible/inventories/example.cfg). Our 
roles and playbooks are written against the host groups in this inventory. Files with a `.cfg` 
extension are ignored by git in the `inventories` folder. This is done to prevent us from 
accidentally exposing our inventories to the public.

The host groups within the example inventory reference the host machines for the DE's underlying 
architecture, as well as host groups for the application itself. Each micro-service has a 
corresponding host group in the inventory.

Refer to the 
[example.cfg]({{ site.github.repository_url }}/tree/master/ansible/inventories/example.cfg) file for
more info.

# Playbooks
* [Updating Databases](work_instructions/database.html)

