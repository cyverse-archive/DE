grouper-config
==============

Generates configuration files for a typical Grouper deployment used by the
Cyverse Discovery Environment.

Requirements
------------

Ansible 2.x

Role Variables
--------------

|   Variable              | required | default                 | choices | comments                                |
|-------------------------|----------|-------------------------|---------|-----------------------------------------|
| grouper_config_base_dir | no       | ""                      |         | The directory to place the files under. |

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: grouper
      vars:
          grouper_config_base_dir: /path/to/config/base
      roles:
        - role: grouper-config

License
-------

BSD

Author Information
------------------

Dennis Roberts <dennis@cyverse.org>
