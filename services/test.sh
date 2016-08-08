#!/bin/sh

set -e
set -x

CMD=$1

if [ -z $CMD ]; then
    CMD=test2junit
fi

docker pull discoenv/buildenv
docker run --rm -v $(pwd):/build -w /build discoenv/buildenv lein do clean, $CMD
