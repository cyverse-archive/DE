cfg-docker-pull
===============

A parameterized role which performs docker pulls.

Requirements
------------

Requires sudo.

Role Variables
--------------

|Variable     | required | default                           | choices        | comments                                               |
|-------------|----------|-----------------------------------|----------------|--------------------------------------------------------|
| image_name  |    yes   |                                   |                | The docker image name. |
| docker_user |    no    | {{docker.user}}                   |                | The docker user name. |
| image_tag   |    no    | {{docker.tag | default(latest) }} |                | The tag for the docker image. |
| group_name  |    yes   |                                   |                | The host group name for which the tasks will execute. |

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: servers
      roles:
         - role: cfg-docker-pull
           image_name: my-image
           group_name: my-group

License
-------

BSD

Author Information
------------------

An optional section for the role authors to include contact information, or a website (HTML is not allowed).
