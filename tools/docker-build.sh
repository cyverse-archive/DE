#!/bin/sh
set -x
set -e

GIT_COMMIT=$(git rev-parse HEAD)

docker pull $DOCKER_USER/buildenv:latest

BUILDENV_GIT_COMMIT=$(docker inspect -f '{{ (index .Config.Labels "org.iplantc.de.buildenv.git-ref")}}' $DOCKER_USER/buildenv:latest)

docker run --rm -e "GIT_COMMIT=$GIT_COMMIT" -v $(pwd):/build -w /build $DOCKER_USER/buildenv:latest lein uberjar
docker build --build-arg git_commit=$GIT_COMMIT --build-arg buildenv_git_commit=$BUILDENV_GIT_COMMIT --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
docker push $DOCKER_USER/$DOCKER_REPO:dev
