deploy-service-systemd
======================

A parameterized service which accepts a list of `service` hash variables and deploys each 
services' systemd unit file, then reloads systemd.

Requirements
------------
Requires sudo.

Supported services must have a group in the inventory which corresponds to their `service_name_short` 
variable (see below).

The default variables referenced in the next section are typically defined in `group_vars/all`. However, 
they can be optionally overriden, if necessary.

The variables included in the `services` list must have the following sub-items:

        service:   
          service_name_short: The shortened service name of the given service
          service_name:  The full systemd service name (e.g. my-service.service)  
          image_name: The service's docker image

Furthermore, services used with this role are expected to be deployed to host(s) within a host group
whose name correspond to ther service's `service_name_short`.

*Supported services:*
* anon-files
* apps
* clockwork
* condor-log-monitor
* data-info
* dewey
* exim-sender
* info-typer
* infosquito
* iplant-email
* iplant-groups
* jex-events
* kifshare
* metadata
* monkey
* notification-agent
* saved-searches
* terrain
* tree-urls
* user-preferences
* user-sessions

Role Variables
--------------

|Variable                | required | default                  | choices        | comments                                               |
|------------------------|----------|--------------------------|----------------|--------------------------------------------------------|
| services               |   yes    |                          |                | A list of service hash variables to deploy |
| systemd_unit_file_dir  |    no    | {{system.unit_file_dir}} |                | The docker image version user name for the image |
| systemd_enable         |    no    |  true                    | true<br/>false | If the systemd service should be enabled. |


Dependencies
------------

None.

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: all
      sudo: yes
      roles:
         - role: deploy-service-systemd
           services:
               - "{{user_preferences}}"
               - "{{user_sessions}}"

License
-------

BSD

Author Information
------------------

Contact jstroot@iplantcollaborative.org
