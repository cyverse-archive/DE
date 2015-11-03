data-container-services-deployer
================================

This role will remove any existing `iplant_data` containers, pull updates, and re-load the data container

Supported Architectures
-----------------------
CentOS 7

Requirements
------------


Role Variables
--------------
registry_host: the host of the docker registry
remote_registry: docker registry path for docker commands

Role Tags
---------
docker_pull: pull the docker containers associated with this role
stop_all_services: stops all services
update_service_files: updates all service files. 
restart_services: restarts all services.
restart_iplant_data: restarts all services.
docker_rm_iplant_data: removes the current iplant_data container.

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


