Role Name
=========

This role deploys the `docker-cleanup` systemd service.

This is a `oneshot` service with an optional timer, which cleans up stray volumes, exited/dead 
containers older than an hour, and dangling images.

Requirements
------------

This role requires the basic iPlant group vars.

Role Variables
--------------

|Variable      | required | default | choices        | comments                                                 |
|--------------|----------|---------|----------------|----------------------------------------------------------|
|config        |    no    |  true   | true<br/>false | Whether the systemd and config files should be deployed. |
|run_cleanup   |    no    |  false  | true<br/>false | Whether to start the service to perform cleanup, or not. |
|install_timer |    no    |  false  | true<br/>false | Whether to deploy the corresponding systemd timer file   |

If you have already deployed the service, and just wish to start it without redeploying all of the service and configuration
files, set `run_cleanup` to _true_, and either skip the `config` tag, or set the `config` variable to _false_.

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

To deploy the service without starting it:
    - hosts: docker-ready
      roles:
         - { role: docker-cleanup }

To start the service w/o re-deploying files (already deployed):
    - hosts: docker-ready
      roles:
         - { role: docker-cleanup, run_cleanup: true, config: false }

To deploy service and start the service:
    - hosts: docker-ready
      roles:
         - { role: docker-cleanup, run_cleanup: true }

To deploy the service with the timer without starting it:
    - hosts: docker-ready
      roles:
         - { role: docker-cleanup, install_timer: true }

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
