This role will remove a data container from a host that supports docker.
It can be used to remove data containers and their images that are no longer in use from Condor nodes.

### extra-vars

* data_image_name (required)
* data_image_tag (optional, 'latest' by default)
* registry_host (optional, parsed from the first host listed in the 'docker-registry' inventory section by default)
* docker.registry.port (optional, '5000' by default)
* remove_image (optional, 'True' by default, to also remove the data container's image from the host)

### Example playbook commands

    ansible-playbook -i inventories/... -K <playbook> --extra-vars "data_image_name=ncbi-ssh-key"

    ansible-playbook -i inventories/... -K <playbook> --extra-vars "remote_registry=discoenv data_image_name=ncbi-sra-properties data_image_tag=test data_container_name=ncbi-sra-test-props"
