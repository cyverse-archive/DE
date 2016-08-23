util-cfg-service
================

A parameterized role which deploys service config files to specified host groups.

Requirements
------------
Requires sudo.

All source properties files are expected to be templates, and it is suggested that 
they be kept in this role's `templates/` directory.

Role Variables
--------------

|Variable                | required | default                                        | choices        | comments                                               |
|------------------------|----------|------------------------------------------------|----------------|--------------------------------------------------------|
| service_name_short     |    yes   |                                                |                | The "short" name of the service. Short refers to the systemd unit name (i.e. short-name.service). |
| service_conf_dir       |    no    | {{de_config_dir}}                              |                | The destination directory for the logging file. |
| service_cfg_file_name  |    no    | {{service_name_short}}.properties              |                | The destination name for the config file (i.e. not the src template). |
| group_name             |    no    | {{service_name_short}}                         |                | The host group name where the config file is to be placed. |
| src                    |    no    | {{service_name_short}}.properties.j2           |                | The path to the src service config template. |
| dest                   |    no    | {{service_conf_dir}}/{{service_cfg_file_name}} |                | The destination path for the service config file. |
| group                  |    no    | root                                           |                | The unix group for the destination file. |
| owner                  |    no    | root                                           |                | The unix owner for the destination file. |
| mode                   |    no    | 0644                                           |                | The unix permissions for the destination file. |


Dependencies
------------

A list of other roles hosted on Galaxy should go here, plus any details in regards to parameters that may need to be set for other roles, or variables that are used from other roles.

Example Playbook
----------------

    - hosts: some-group
      become: yes
      roles:
         - role: util-cfg-service
           service_name_short: my-service 

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
