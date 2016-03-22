#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

docker run --rm \
	-v $(pwd):/build \
	-w /build \
	$DOCKER_USER/buildenv:latest \
	gb test -v
