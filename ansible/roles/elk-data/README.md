Elk-Data
=========

For deploying the service definitions for the ELK data container.

Future features may be ES index management tasks

All elastic search and kibana data is held in the data container. Executing
this role _should not_ destroy any data that is already in the data container.

Requirements
------------

Requires sudo.

Role Variables
--------------

N/A

Dependencies
------------

cfg-systemd-unit

Example Playbook
----------------

    - hosts: elk
      roles:
         - role: elk-data

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
