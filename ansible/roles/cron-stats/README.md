Role Name
=========

This role will set up all cron jobs required for the Discovery environment.

Requirements
------------

This role uses a GO application to obtain system stats, and is built with Jenkins.
https://everdene.iplantcollaborative.org/jenkins/job/build-docker-host-stats-go-app/

Role Variables
--------------
dockerHostStats_url: the location of the latest `dockerHostStats` file from Jenkins.

Dependencies
------------
N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: docker-ready
      roles:
         - { role: cron-stats }

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
