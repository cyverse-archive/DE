deploy-systemd-target
=====================

This role deploys a common systemd target which is used to group all
Discovery Environment systemd services.

Requirements
------------

Requires sudo.

Role Variables
--------------


|Variable               | required | default                  | choices        | comments                                             |
|-----------------------|----------|--------------------------|----------------|------------------------------------------------------|
| systemd_unit_file_dir |    no    | {{system.unit_file_dir}} |                | The directory where systemd unit files are installed |
| systemd_target        |    no    | {{system.target}}        |                | The name of the Discovery Environment systemd target |


Dependencies
------------

None.

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: all
      roles:
         - { role: deploy-systemd-target }

License
-------

BSD

Author Information
------------------

jstroot@iplantcollaborative.org
