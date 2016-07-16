de-TMP-RUN-ONCE-app-beta-tagger
===============================

Role to run the utility that adds "beta" metadata AVUS to apps in the DE's "Beta" apps category.

Role Variables
--------------

| Name                  | Description                     | Required | Default                           |
| docker_tag            | Docker tag name                 | No       | {{ docker.tag | default(dev) }}   |
| app_beta_tagger_repo  | App beta tagger docker repo     | No       | discoenv/apps-beta-tagger         |

Example Playbook
----------------

    ---
    - name: tag the apps in 'Beta' with 'beta' AVUs
      hosts: apps:&systems
      become: true
      gather_facts: false
      tags:
        - services
        - apps
        - metadata
      roles:
        - role: de-TMP-RUN-ONCE-app-beta-tagger

License
-------

BSD
