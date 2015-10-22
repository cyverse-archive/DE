ui
==

This role is responsible for deploying the Discovery Environment UI, which includes an http server.

Supported Architectures
-----------------------
CentOS 7

Requirements
------------
N/A

Role Variables
--------------
N/A

Role Tags
---------
config: updates all configs
docker_pull: pull the docker containers associated with this role
docker_rm: removes the de & nginx containers.
stop_all_services: stops all services
update_service_files: updates all service files. 
stop_de-ui: Stops the de-ui service
stop_de-ui-nginx: Stops the nginx service. 
restart_services: restarts all services.
restart_de-ui: restarts de-ui service.
restart_de-ui-nginx: restarts de-ui-nginx service.

Dependencies
------------
N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: ui
      roles:
         - { role: ui }

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org


