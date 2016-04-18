#!/bin/sh
set -x
set -e

GIT_COMMIT=$(git rev-parse HEAD)

docker build --build-arg git_commit=$GIT_COMMIT --pull --no-cache --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
docker push $DOCKER_USER/$DOCKER_REPO:dev
