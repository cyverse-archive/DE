de-deploy-service
=================

A parameterized role which deploys a docker-compose service to a set of hosts, potentially with zero downtime.

Requirements
------------
Requires sudo/become.
Does not require facts.

Role Variables
--------------

|Variable                | required | default                                        | choices        | comments                                               |
|------------------------|----------|------------------------------------------------|----------------|--------------------------------------------------------|
| service_name           |    yes   |                                                |                | The name of the service. This should be a docker-compose service name, i.e., underscores not hyphens. |
| deploy_use_color       |    no    | false                                          | false/true     | Whether or not to deploy using a zero-downtime blue-green strategy to deploy. Given a group_var for "use_color" which designates that the inventory is set up for this, services set up for it would be called with something like 'use_color|default(false)' for this, where services which do not support zero-downtime deployment at all could use the default or explicitly pass false. |
| has_configs            |    no    | true                                           | false/true     | whether this service uses a configuration image |
| has_iplant_data        |    no    | false                                          | false/true     | whether this service uses an iplant_data container |
| force_recreate         |    no    | true                                           | false/true     | whether to pass the --force-recreate flag to docker-compose up |
| docker_compose_path    |    no    | docker.compose_path from group_vars            |                | the docker-compose file path to use. Generally just /etc/docker-compose.yml |

Example Playbook
----------------

    - hosts: some-group
      become: yes
      gather_facts: false
      roles:
         - role: de-deploy-service
           service_name: my_service
           has_iplant_data: true
           deploy_use_color: {{use_color|default(false)}}

License
-------

BSD

Author Information
------------------

Ian McEwen - mian@cyverse.org
