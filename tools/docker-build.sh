#!/bin/sh
set -x
set -e

GIT_COMMIT=$(git rev-parse HEAD)

docker run --rm -e "GIT_COMMIT=$GIT_COMMIT" -v $(pwd):/build -w /build $DOCKER_USER/buildenv lein uberjar
if [ -f Dockerfile.template ]
then
  sed -e "s/%%GIT_COMMIT%%/$GIT_COMMIT/g" Dockerfile.template > Dockerfile.$GIT_COMMIT
  docker build --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" -f Dockerfile.$GIT_COMMIT .
  rm Dockerfile.$GIT_COMMIT
else
  docker build --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:dev" .
fi
docker push $DOCKER_USER/$DOCKER_REPO:dev
