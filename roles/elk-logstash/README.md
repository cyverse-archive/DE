Elk-Logstash
=========

For deploying and configuring the logstash instance for the DE ELK stack.

NOTE: Logstash conf files
* 0x - _inputs_
* 1x - _field cleanup_
* 2x - _clones and xforms_
* 3x - _secondary cleaup_
* 4x - _reserved_
* 5x - _outputs_

Requirements
------------

systemd

Role Variables
--------------

TBD

Dependencies
------------

N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: servers
      roles:
         - { role: username.rolename, x: 42 }

License
-------

BSD

Author Information
------------------

Jonathan Strootman, jstroot@iplantcollaborative.org
