#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

if [ -z "$DOCKER_REPO" ]; then
	DOCKER_REPO=templeton
fi

VERSION=$(cat version | sed -e 's/^ *//' -e 's/ *$//')
GIT_COMMIT=$(git rev-parse HEAD)
BUILD_USER=$(whoami)

docker pull $DOCKER_USER/buildenv:latest

BUILDENV_GIT_COMMIT=$(docker inspect -f '{{ (index .Config.Labels "org.iplantc.de.buildenv.git-ref")}}' $DOCKER_USER/buildenv:latest)

docker run --rm \
	-v $(pwd):/build \
	-w /build \
	$DOCKER_USER/buildenv:latest \
	gb build -f -F --ldflags "-X main.appver=$VERSION -X main.gitref=$GIT_COMMIT -X main.builtby=$BUILD_USER"
docker build --build-arg git_commit=$GIT_COMMIT \
             --build-arg buildenv_git_commit=$BUILDENV_GIT_COMMIT \
             --build-arg version=$VERSION \
             --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
docker push $DOCKER_USER/$DOCKER_REPO:dev
docker rmi $DOCKER_USER/$DOCKER_REPO:dev
