#!/bin/sh
set -x
set -e

GIT_COMMIT=$(git rev-parse HEAD)

if [ -f Dockerfile.template ]
then
  sed -e "s/%%GIT_COMMIT%%/$GIT_COMMIT/g" Dockerfile.template > Dockerfile.$GIT_COMMIT
  docker build --pull --no-cache --rm -t "$DOCKER_USER/$DOCKER_REPO:latest" -f Dockerfile.$GIT_COMMIT .
  rm Dockerfile.$GIT_COMMIT
else
  docker build --pull --no-cache --rm -t "$DOCKER_USER/$DOCKER_REPO:latest" .
fi
docker push $DOCKER_USER/$DOCKER_REPO:latest
