infra-systemd-tmp-files
=======================

Places a systemd temp files configuration file on a host. See 
[tmpfiles.d|http://www.freedesktop.org/software/systemd/man/tmpfiles.d.html]  
for more info.

Requirements
------------

Requires sudo and systemd.

Role Variables
--------------

N/A

Dependencies
------------

N/A

Example Playbook
----------------

    - hosts: condor|condor-submission
      roles:
         - role: infra-systemd-tmp-files

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org
