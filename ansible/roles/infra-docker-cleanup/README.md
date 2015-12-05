01-docker-cleanup
=================

This role deploys the `docker-cleanup` systemd service.

This is a `oneshot` service with a daily timer, which cleans up stray volumes, exited/dead 
containers older than an hour, and dangling images.

Requirements
------------

This role requires the basic iPlant group vars.

Role Variables
--------------

N/A

Dependencies
------------

cfg-service
cfg-systemd-unit

Example Playbook
----------------

    - hosts: docker-ready
      roles:
         - role: 01-docker-cleanup

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
