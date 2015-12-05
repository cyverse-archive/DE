Logrotate.d
===========

This role places a logrotate file on the host system. 

The conf file lists all logs known to our applications. So, on a given system, the conf file will list log files which do not exist, and this is ok.

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

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: all
      roles:
           - logrotate.d

License
-------

BSD

Author Information
------------------

[Jonathan Strootman](jstroot@iplantcollaborative.org)
