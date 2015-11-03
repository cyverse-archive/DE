Elk-Data
=========

For deploying the service definitions for the ELK data container.

Future features may be ES index management tasks

All elastic search and kibana data is held in the data container. Executing
this role _should not_ destroy any data that is already in the data container.

Requirements
------------

systemd

Role Variables
--------------

TBD

Dependencies
------------

N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: servers
      roles:
         - { role: username.rolename, x: 42 }

License
-------

BSD

Author Information
------------------

Jonathan Strootman, jstroot@iplantcollaborative.org
