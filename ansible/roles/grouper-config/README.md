grouper-config
==============

Generates configuration files for a typical Grouper deployment used by the
Cyverse Discovery Environment. This configuration is unique to Cyverse's
deployments, and your configuration is at least somewhat likely to be different.
For example, the HTTP server fronting Grouper may be Apache rather than nginx
or you may want to configure Grouper to obtain user information from a database
rather than from LDAP. These configuration templates do provide a decent starting
point, however.

Requirements
------------

Ansible 2.x

Role Variables
--------------

|   Variable         | required | default | choices | comments                                |
|--------------------|----------|---------|---------|-----------------------------------------|
| grouper_config_dir | yes      |         |         | The directory to place the files under. |

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: grouper
      vars:
          grouper_config_dir: /path/to/config/base
      roles:
        - role: grouper-config

License
-------

BSD

Author Information
------------------

Dennis Roberts <dennis@cyverse.org>
