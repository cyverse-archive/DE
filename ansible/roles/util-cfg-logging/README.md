util-cfg-logging
================

A parameterized role which deploys log files to specified host group.

Requirements
------------
Requires sudo.

All source log files are expected to be templates, and it is suggested that 
they be kept in this role's `templates/` directory.

Role Variables
--------------

|Variable                | required | default                                | choices        | comments                                               |
|------------------------|----------|----------------------------------------|----------------|--------------------------------------------------------|
| service_name_short     |    yes   |                                        |                | The "short" name of the service. Short refers to the systemd unit name (i.e. short-name.service). |
| logging_conf_dir       |    no    | {{logging.conf_dir}}                   |                | The destination directory for the logging file. |
| log_file_name          |    no    | {{service_name_short}}-logging.xml     |                | The destination name for the logfile (i.e. not the src template). |
| group_name             |    no    | {{service_name_short}}                 |                | The host group name where the config file is to be placed. |
| src                    |    no    | templates/logback_template.xml.j2      |                | The path to the src logging template file. The default is in this role's templates dir. |
| dest                   |    no    | {{logging_conf_dir}}/{{log_file_name}} |                | The destination path for the logging config file. |
| group                  |    no    | root                                   |                | The unix group for the destination file. |
| owner                  |    no    | root                                   |                | The unix owner for the destination file. |
| mode                   |    no    | 0644                                   |                | The unix permissions for the destination file. |


Dependencies
------------

None.

Example Playbook
----------------

    - hosts: some-group
      sudo: yes
      roles:
         - role: util-cfg-logging
           log_file_name: my-service-logging.xml
           group_name: my-group

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org

