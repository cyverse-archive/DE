deploy-service-cfg
==================

A parameterized service which accepts a list of `service` hash variables and deploys each 
services' service configuration file, logging configuration file, then pulls each 
services' docker image.

As long as the requirements are met, services will only be deployed to their respective host 
machines.

Requirements
------------
Requires sudo.

Supported services must have a group in the inventory which corresponds to their `service_name_short` 
variable (see below).

The default variables referenced in the next section are typically defined in `group_vars/all`. However, 
they can be optionally overriden, if necessary.

The variables included in the `services` list must have the following sub-items:

        service:   
          service_name_short: The shortened service name of the given service
          image_name: The service's docker image

The docker image that will be pulled is of the form `{{docker_user}}/{{item.image_name}}:{{docker_tag}}`. 

*Supported services:*
* anon-files
* apps
* clockwork
* condor-log-monitor
* data-info
* dewey
* exim-sender
* info-typer
* infosquito
* iplant-email
* iplant-groups
* jex-events
* kifshare
* metadata
* monkey
* notification-agent
* saved-searches
* terrain
* tree-urls
* user-preferences
* user-sessions

Role Variables
--------------

|Variable                | required | default                  | choices        | comments                                               |
|------------------------|----------|--------------------------|----------------|--------------------------------------------------------|
| services               |   yes    |                          |                | A list of service hash variables to deploy |
| docker_user            |    no    | {{docker.user}}          |                | The docker user name for the image |
| docker_tag             |    no    | {{docker.tag}}           |                | The docker image version user name for the image |
| service_conf_dir       |    no    | {{de_config_dir}}        |                | The docker image version user name for the image |
| logging_conf_dir       |    no    | {{logging.conf_dir}}     |                | The docker image version user name for the image |
| config                 |    no    |  true                    | true<br/>false | If the service config file should be deployed. |
| logging_config         |    no    |  true                    | true<br/>false | If the service logging config file should be deployed. |
| docker_pull            |    no    |  true                    | true<br/>false | If the service's docker image should be pulled. |


Dependencies
------------

None.

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: all
      sudo: yes
      roles:
         - role: deploy-service-cfg
           services:
               - "{{user_preferences}}"
               - "{{user_sessions}}"

License
-------

BSD

Author Information
------------------

Contact jstroot@iplantcollaborative.org
