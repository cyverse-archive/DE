util-cfg-systemd-unit
=====================

A parameterized service which installs or updates systemd unit files.

This role is primarily used for service files, but is not restricted to 
only services. The systemd unit type can be specified via the `unit_type` var.


Requirements
------------
Requires sudo.

All source systemd unit files are expected to be templates, and it is suggested that 
they be kept in this role's `templates/` directory.

Role Variables
--------------

|Variable                | required | default                                    | choices        | comments                                               |
|------------------------|----------|--------------------------------------------|----------------|--------------------------------------------------------|
| service_name_short     |    yes   |                                            |                | The "short" name of the service. Short refers to the systemd unit name (i.e. short-name.service). |
| systemd_unit_file_dir  |    no    | {{system.unit_file_dir}}                   |                | The docker image version user name for the image. |
| group_name             |    no    | {{service_name_short}}                     |                | The host group name for which the tasks will execute. |
| unit_type              |    no    | service                                    |                | The type of the systemd unit. |
| service_name           |    no    | {{service_name_short}}.{{unit_type}}       |                | The "short" name of the service. Short refers to the systemd unit name (i.e. short-name.service). |
| systemd_enable         |    no    |  true                                      | true<br/>false | If the systemd service should be enabled. |
| src                    |    no    | {{service_name}}.j2                        |                | The path to the template file for the given unit. |
| dest                   |    no    | {{systemd_unit_file_dir}}/{{service_name}} |                | The path to the template file for the given unit. |
| group                  |    no    |  root                                      |                | The unix group for the unit file. |
| owner                  |    no    |  root                                      |                | The unix user for the unit file. |
| mode                   |    no    |  0644                                      |                | The permissions for the unit file. |


Dependencies
------------

None.

Example Playbook
----------------

    - hosts: services
      become: yes
      roles:
         - role: util-cfg-systemd-unit
           service_name_short:

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org

