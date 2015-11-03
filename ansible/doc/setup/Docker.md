# Install Docker

## OSX

To build on OS X, see [Get Started with Docker for Mac OS X](http://docs.docker.com/mac/started/)
for instructions on installing [Docker Toolbox](https://www.docker.com/toolbox)

## Running ansible inside a Docker container

The Dockerfile at the top-level of the de-ansible checkout can be used to create a personal container that is able to run the de-ansible playbooks.

DO NOT PUSH YOUR PERSONAL de-ansible CONTAINER!

To build the container, first run the create-ssh-configs.sh script at the top-level of the de-ansible checkout:

    de-ansible> ./create-ssh-configs.sh

Then run docker build:

    de-ansible> docker build .

Make a note of the image ID and use it in a docker run command:

    de-ansible> docker run --rm -it -v $(pwd):/de-ansible -w /de-ansible <image ID> /bin/bash

You should be able to run the ansible commands inside the container you created.
