#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

if [ -z "$DOCKER_TAG" ]; then
	DOCKER_TAG=dev
fi

docker pull discoenv/buildenv:latest
docker run --rm -t -a stdout -a stderr -e "GIT_COMMIT=$(git rev-parse HEAD)" -v $(pwd):/build -w /build discoenv/buildenv lein uberjar
docker build --rm -t $DOCKER_USER/template-mover:$DOCKER_TAG .
docker push $DOCKER_USER/template-mover:$DOCKER_TAG
