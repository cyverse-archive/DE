util-cfg-docker-pull
====================

A parameterized role which performs docker pulls.

Requirements
------------

Requires sudo.

Role Variables
--------------

|Variable     | required | default                           | choices        | comments                                               |
|-------------|----------|-----------------------------------|----------------|--------------------------------------------------------|
| service_name |    yes   |                                   |                | The name of the service in the docker-compose.yml. |
| docker.tag  |    yes   | The value of docker.tag in the group_vars| | Passed into the docker-compose command as the DE_TAG environment variable. |
| environment_name | yes | The value of the environment_name variable in the group_vars | | Passed into the docker-compose command as the DE_ENV environment variable.|

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: servers
      roles:
         - role: util-cfg-docker-pull
           service_name: my_service

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
