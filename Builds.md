# Building the build environment Docker container

The Dockerfile for the build environment container image is located in the top level directory of the backend git repo.

To build on OS X, make sure you have VirtualBox and homebrew installed and then follow these directions:

1. brew install boot2docker

1. brew install docker

1. Follow any instructions printed out by the previous command

1. boot2docker init

1. boot2docker start

That will get docker up and running on your local OS X box. To actually do the build of the container, cd into the top level directory of the backend checkout and run this:

* docker build .

You should only need to rebuild the container image if you've made any changes to the Dockerfile that is used to construct it. You can reuse the already built image to do any individual builds. A pre-built image has been pushed to the Docker Hub in the quagbrain/de-backend-buildenv repo. You can pull down the prebuilt image with:

* docker pull quagbrain/de-backend-buildenv

# Populating your local .m2 using the build image

Doing this is instructive because it uses a lot of Docker's settings (though nowhere near all of them) and can also be used to build RPMs for the services and tools. In fact, you should probably do this before you try and build anything else just to make sure you have the latest versions of the libraries before attempting any of the other builds.

Here's the docker command you should run from the top level of the backend repo checkout:

* docker run --rm -i -t -v ~/.m2:/root/.m2 -v $(pwd):/build -a stdout -a stderr -w /build quagbrain/de-backend-buildenv lein exec build-all.clj lein-plugins libs

The benefit of this is that you're doing the build in the exact same environment that is used to construct the builds that go into production. Additionally, you won't have to manually install any of the build tools.

# Building a service/tool

Each backend service and tool has a build.sh script that will build the project and generate an RPM for it. The build.sh script takes a single argument: the release number.

Here's an example using the JEX. The basic process should be the same for all of the backend projects. I'm assuming that you're still in the top-level directory of the backend repository checkout.

1. cd services/JEX

1. docker run --rm -i -t -v ~/.m2:/root/.m2 -v $(pwd):/build -a stdout -a stderr -w /build quagbrain/de-backend-buildenv ./build.sh 10000

When the container exists you should have a new RPM in your local JEX directory.