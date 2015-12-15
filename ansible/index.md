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

* [Setup](setup)

# Design
We have strived to follow Ansible's 
[best practices](http://docs.ansible.com/ansible/playbooks_best_practices.html). 
However, we have slightly diverted on the topics of 
[directory layout](http://docs.ansible.com/ansible/playbooks_best_practices.html#directory-layout) and
[role-separated top level playbooks](http://docs.ansible.com/ansible/playbooks_best_practices.html#top-level-playbooks-are-separated-by-role).

## Directory Layout
This repository is organized with group_vars defined with some default values in
`inventories/group_vars/all`, with the intention that these playbooks are used with inventory 
created in this `inventories` directory (use a `.cfg` extension to prevent committing the files) and
group_vars files containing private and overriding values in a top-level `group_vars` directory.

## Role-Separated Playbooks
We maintain role-separated playbooks, but they are kept in the 
[ansible/playbooks/]({{ site.github.repository_url }}/tree/master/ansible/playbooks) folder. If you wish to use
these playbooks, we have created the 
[single-role.yaml]({{ site.github.repository_url }}/tree/master/ansible/single-role.yaml) playbook. 
The documentation on its use is contained within the playbook.

## Inventories
We have provided an example inventory file; 
[example.cfg]({{ site.github.repository_url }}/tree/master/ansible/inventories/example.cfg). Our 
roles and playbooks are written against the host groups in this inventory. 

The host groups in the inventory reference the host machines for the DE's underlying architecture,
as well as host groups for the application itself. Each micro-service has a corresponding host 
group in the inventory.

Refer to the 
[example.cfg]({{ site.github.repository_url }}/tree/master/ansible/inventories/example.cfg) file for
more info.

# Playbooks
* [Updating Databases](work_instructions/database.html)


Infrastructure vs Application
