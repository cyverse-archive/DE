#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

if [ -z "$DOCKER_REPO" ]; then
	DOCKER_REPO=nginx-consul-template
fi

if [ -z "$DOCKER_TAG" ]; then
	DOCKER_TAG=dev
fi

GIT_COMMIT=$(git rev-parse HEAD)

docker build --build-arg git_commit=$GIT_COMMIT --pull --no-cache --rm -t "$DOCKER_USER/$DOCKER_REPO:$DOCKER_TAG" .
docker push $DOCKER_USER/$DOCKER_REPO:$DOCKER_TAG
