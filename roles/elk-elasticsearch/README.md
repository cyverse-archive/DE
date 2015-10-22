elk-elasticsearch
=================

This role deploys the elastic search instance for the DE ELK stack. 
The ELK stack refers to the use of ElasticSearch, Kibana, and Logstash together.


Requirements
------------

systemd

Role Variables
--------------
dockerHostStats_url: the location of the latest `dockerHostStats` file from Jenkins.

Role Tags
---------

config: deploys all config files.
stop_services: stops all services
update_service_files: updates all service files. CentOS 7, systemd files.
restart_services: restarts all services. 

stop_elasticsearch: stops the elasticsearch service. CentOS 7 only.
stop_kibana: stops the kibana service. CentOS 7 only.
stop_logstash: stops the logstash service. CentOS 7 only.
restart_data: resarts the data container service. CentOS 7 only.
restart_elasticsearch: restarts the elasticsearch service. CentOS 7 only.
restart_kibana: restarts the kibana service. CentOS 7 only.
restart_logstash: restarts the logstash service. CentOS 7 only.

Dependencies
------------
N/A

Example Playbook
----------------

Including an example of how to use your role (for instance, with variables passed in as parameters) is always nice for users too:

    - hosts: elk
      roles:
         - { role: elk-stack }

License
-------

BSD

Author Information
------------------

Jonathan Strootman - jstroot@iplantcollaborative.org


