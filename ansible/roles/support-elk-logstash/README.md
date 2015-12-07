support-elk-logstash
===============

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

systemd, docker

Role Variables
--------------

TBD

Dependencies
------------

util-cfg-systemd-unit

Example Playbook
----------------

    - hosts: elk
      roles:
         - role: support-elk-logstash

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
