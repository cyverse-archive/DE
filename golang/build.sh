#!/bin/sh

set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

VERSION=$(cat version | sed -e 's/^ *//' -e 's/ *$//')
GIT_COMMIT=$(git rev-parse HEAD)
BUILD_USER=$(whoami)

docker pull $DOCKER_USER/buildenv:latest

if [ -d ./bin ]; then
	docker run --rm -v $(pwd):/work -w /work $DOCKER_USER/buildenv rm -r bin
fi

docker run --rm \
	-v $(pwd):/build \
	-w /build \
	$DOCKER_USER/buildenv:latest \
	gb vendor restore

docker run --rm \
	-v $(pwd):/build \
	-w /build \
	$DOCKER_USER/buildenv:latest \
	gb build -f -F --ldflags "-X main.appver=$VERSION -X main.gitref=$GIT_COMMIT -X main.builtby=$BUILD_USER"
