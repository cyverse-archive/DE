systemd-de-target
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
| config                |    no    | false                    | true/false     | Whether to deploy the target's unit file             |
| config_only           |    no    | false                    | true/false     | When true, will only perform configuration tasks. Configuration tasks will be performed if `config` or `config_only` is true  |
| up                    |    no    | true                     | true/false     | When true, will isolate the de target (starting all 'wanted' services), false otherwise |
| fallback_target       |    no    | "multi-user.target"      |                | The systemd target to isolate when `up` is false     |


Dependencies
------------

None.

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: all
      roles:
         - { role: systemd-de-target }

License
-------

BSD

Author Information
------------------

jstroot@iplantcollaborative.org

