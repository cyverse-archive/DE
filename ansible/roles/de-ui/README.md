de-ui
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

util-cfg-logging
util-cfg-service
util-cfg-systemd-unit
util-cfg-docker-pull

Example Playbook
----------------

    - hosts: ui
      roles:
         - role: de-ui

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org


