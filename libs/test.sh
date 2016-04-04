#!/bin/sh

set -e
set -x

CMD=$1

if [ -z $CMD ]; then
    CMD=test2junit
fi

docker run --rm -v $(pwd)/../..:/build -w /build discoenv/buildenv ./libs/test-in-container.sh libs/$(basename $(pwd)) $CMD
