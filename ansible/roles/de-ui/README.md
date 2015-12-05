10-ui
=====

This role is responsible for deploying the Discovery Environment UI, which includes an http server.


Requirements
------------

Requires sudo.

Role Variables
--------------
N/A


Dependencies
------------

cfg-logging
cfg-service
cfg-systemd-unit
cfg-docker-pull

Example Playbook
----------------

    - hosts: ui
      roles:
         - role: 10-ui

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org


