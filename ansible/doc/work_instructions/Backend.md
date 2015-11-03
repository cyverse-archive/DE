# DE Backend Instructions


## Updating the entire backend

    ansible-playbook -i inventories/... -K deploy-backend.yaml

The playbook will:

* Load configuration files
* Deploy database changes
* Update services
* Update the condor nodes

The -K option will force a prompt for your sudo password. It needs to be the same across all hosts.

## Updating Services, excluding anon-files
__Development__

    ansible-playbook -i inventories/... -K [-u <user>] [--extra-vars "service_name=<name>"] update-services.yaml

The -K will force a prompt for your sudo password. -i points to the inventory to use and -u
indicates which user to ssh into the server as (optional). The optional --extra-vars argument allows
you to specify the name of a single service to update, rather than updating all services.
    
## Updating the AMQP brokers

    ansible-playbook -K -i inventories/... amqp-brokers.yaml
