This role will deploy a data container to a host that supports docker.
It's used by the `deploy-data-container-condor.yaml` playbook to install private data containers to the
Condor nodes for use by authorized tool containers.

### extra-vars

* data_image_name (required)
* data_image_tag (optional, 'latest' by default)
* registry_host (optional, parsed from the first host listed in the 'docker-registry' inventory section by default)
* docker.registry.port (optional, '5000' by default)

### Example playbook commands

    ansible-playbook -i inventories/... -K <playbook> --extra-vars "data_image_name=ncbi-ssh-key"

    ansible-playbook -i inventories/... -K <playbook> --extra-vars "remote_registry=discoenv data_image_name=ncbi-sra-properties data_image_tag=test data_container_name=ncbi-sra-test-props"
