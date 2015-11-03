Condor Executable Jar
=====================

This role places a systemd tmp file which runs at boot on systemd CentOS 7 machines which registers executable jars so they will run.

For more information, see the documentation on systemd's tmpfiles.d

    man tmpfiles.d

Requirements
------------

N/A

Role Variables
--------------

N/A

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: condor
      roles:
         - condor-exec-jar

License
-------

BSD

Author Information
------------------

[Jonathan Strootman](jstroot@iplantcollaborative.org)
