10-iplant-data-cfg
==================

This role will remove any existing `iplant_data` containers, pull updates, and re-load the data container

Requirements
------------

Requires sudo


Role Variables
--------------
docker.registry.host: the host of the docker registry
remote_registry: docker registry path for docker commands

Dependencies
------------

cfg-systemd-unit

Example Playbook
----------------

    - hosts: docker-ready
      roles:
         - role: 10-iplant-data-cfg

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org


