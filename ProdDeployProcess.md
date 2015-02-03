1. Deploy the services

        ansible-playbook -i inventories/prod/prod.cfg -K deploy-backend.yaml

1. Deploy the UI WARs

        ansible-playbook -i inventories/prod/prod.cfg -K ui.yaml

1. Update AMQP broker

        ansible-playbook -K -i inventories/prod/prod.cfg amqp-brokers.yaml

1. Update elasticsearch cluster.

        ansible-playbook -K -i inventories/prod/prod.cfg elasticsearch.yaml

1. Update the system packages. This will reboot the machines, so do this task last.

        ansible-playbook -K -i inventories/prod/prod.cfg --extra-vars "reboot=true" systems.yaml

1. Rejoice
