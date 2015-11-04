#!/bin/sh
set -x
set -e

#################################################################
# Performs jenkins build, builds docker container, and pushes it.
# This script is intended to be run from Jenkins. If it is not,
# then the environment variables below need to be set manually.
#
# The jenkins build command shall set the 'DOCKER_REPO' env 
# variable.
#################################################################

GIT_COMMIT=$(git rev-parse HEAD)

docker pull $DOCKER_USER/buildenv:latest

BUILDENV_GIT_COMMIT=$(docker inspect -f '{{ (index .Config.Labels "org.iplantc.de.buildenv.git-ref")}}' $DOCKER_USER/buildenv:latest)

docker run --rm -t -a stdout -a stderr \
    -v $(pwd):/build \
    -v ${WORKSPACE}/.gradle:/root/.gradle \
    -w /build \
    discoenv/buildenv \
    /build/gradlew clean test createProdWar \
    -PBUILD_TAG=${BUILD_TAG} \
    -PBUILD_ID=${BUILD_ID} \
    -PBUILD_NUMBER=${BUILD_NUMBER} \
    -PGIT_COMMIT=${GIT_COMMIT} \
    -PGIT_BRANCH=${GIT_BRANCH} 

if [ -f Dockerfile.template ]
then
  sed -e "s/%%GIT_COMMIT%%/$GIT_COMMIT/g" \
      -e "s/%%BUILDENV_GIT_COMMIT%%/$BUILDENV_GIT_COMMIT/g" \
      Dockerfile.template > Dockerfile.$GIT_COMMIT
  docker build --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" -f Dockerfile.$GIT_COMMIT .
  rm Dockerfile.$GIT_COMMIT
else
  docker build --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
fi
docker push $DOCKER_USER/$DOCKER_REPO:dev
