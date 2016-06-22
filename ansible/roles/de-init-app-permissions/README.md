Role Name
=========

A brief description of the role goes here.

Role Variables
--------------

| Name                  | Description                     | Required | Default                           |
| docker_tag            | Docker tag name                 | No       | {{ docker.tag | default(dev) }}   |
| app_registration_repo | App registration docker repo    | No       | discoenv/app-registration         |
| config_image_name     | Configuration docker image name | No       | de-configs-{{ environment_name }} |
| config_repo           | Configuration docker repo       | No       | {{ docker.registry.base }}/{{ config_image_name }} |
| config_path           | Configuration path              | No       | /etc/iplant/de/permissions.yaml   |
| de_database_name      | Name of the DE database         | No       | {{ db_name }}                     |

Example Playbook
----------------

    ---
    - name: initialize the permissions database
      hosts: permissions:&systems
      become: true
      gather_facts: false
      tags:
        - services
        - permissions
      roles:
        - role: de-init-app-permissions

License
-------

BSD
