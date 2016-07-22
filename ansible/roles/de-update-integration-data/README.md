de-update-integration-data
==========================

Links existing integration data records to existing users in the DE database. The purpose of this is to allow users
to continue to see apps that they've integrated even if their email address changes. This process works by comparing
the email address in each integration data record to the email addresses in LDAP. If the email address is found in
LDAP then the username is retrieved from LDAP and used to associate the integration data record with the user table
entry in the database.

Role Variables
--------------

| Name                  | Description                     | Required | Default                           |
| --------------------- | ------------------------------- | -------- | --------------------------------- |
| docker_tag            | Docker tag name                 | No       | {{ docker.tag | default(dev) }}   |
| integrationator_repo  | Utility docker repo             | No       | discoenv/integrationator          |
| ldap_host             | LDAP host and, optionally, port | No       | {{ ldap.host }}                   |
| db_host               | DE apps database host           | Yes      |                                   |
| db_port               | DE apps database port           | Yes      |                                   |
| db_name               | DE apps database name           | Yes      |                                   |
| db_user               | DE apps database username       | Yes      |                                   |
| db_password           | DE apps database password       | Yes      |                                   "

Example Playbook
----------------

    ---
    - hosts: servers
      become: yes
      roles:
         - role: de-update-integration-data

License
-------

BSD
