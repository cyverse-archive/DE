#!/bin/sh
set -x
set -e

if [ -z "$DOCKER_USER" ]; then
	DOCKER_USER=discoenv
fi

docker run --rm -t \
	-v $(pwd):/build \
	-w /build \
	$DOCKER_USER/buildenv:latest \
	bash -c 'gb test -v | tee /dev/tty | go-junit-report > test-results.xml'
