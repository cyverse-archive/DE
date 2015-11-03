services
========

This role will update configs and deploy all backend DE services
TODO: Define parasitic environment

Supported Architectures
-----------------------
CentOS 7

Requirements
------------


Role Variables
--------------
service_names: list of all service names
parasitics: list of services that are not to be deployed to parasitic environments
extras: list of extra services that should be restarted
enable_services: whether or not to enable services, defaults to `yes`. Set to `no` to disable services

Role Tags
---------
config:
config_anon-files:
config_saved-searches:
config_user-preferences:
config_user-sessions:
config_tree-urls:
config_notificationagent:
config_metadactyl:
config_kifshare:
config_jex-events:
config_iplant-email:
config_donkey:
config_data-info:
config_metadata:
config_condor-log-monitor:
config_clockwork:

docker_pull: pull the docker containers associated with this role
docker_rm: 
docker_rm_exim: 
                                     
update_service_files: updates all service files. 
stop_services:
stop_[service name]:
restart_services: restarts all services.
restart_[service_name]:
restart_exim:

Dependencies
------------
N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: docker-ready
      roles:
         - { role: data-container-services-deployer }

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org





## Assumptions
* This role assumes that the [data-container-services-create](../data-container-services-create/README.md) role has been executed.
