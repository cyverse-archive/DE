#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

if [ -z "$DOCKER_REPO" ]; then
	DOCKER_REPO=jex-events
fi

if [ -d "pkg/" ]; then
	rm -r pkg/
fi

if [ -d "bin/" ]; then
	rm -r bin/
fi

VERSION=$(cat version | sed -e 's/^ *//' -e 's/ *$//')
GIT_COMMIT=$(git rev-parse HEAD)
BUILD_USER=$(whoami)

docker pull $DOCKER_USER/buildenv:latest

BUILDENV_GIT_COMMIT=$(docker inspect -f '{{ (index .Config.Labels "org.iplantc.de.buildenv.git-ref")}}' $DOCKER_USER/buildenv:latest)

docker run --rm \
	-v $(pwd):/jex-events \
	-w /jex-events \
	$DOCKER_USER/buildenv:latest \
	gb build --ldflags "-X main.appver=$VERSION -X main.gitref=$GIT_COMMIT -X main.builtby=$BUILD_USER"
docker build --build-arg git_commit=$GIT_COMMIT \
             --build-arg buildenv_git_commit=$BUILDENV_GIT_COMMIT \
             --build-arg version=$VERSION \
             --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
docker push $DOCKER_USER/$DOCKER_REPO:dev
docker rmi $DOCKER_USER/$DOCKER_REPO:dev
