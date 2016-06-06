#!/bin/sh

set -e

error_exit() {
    echo 1>&2
    echo "TEST FAILED: $@" 1>&2
    exit 1
}

OS=$(uname)
DBCONTAINER=apps-de-db

if [ $(docker ps -qf "name=$DBCONTAINER" | wc -l) -eq 0 ]; then
  # Pull the build environment.
  docker pull discoenv/buildenv || error_exit 'unable to pull the build environment image'

  docker pull discoenv/unittest-dedb:dev

  docker run --name $DBCONTAINER -d discoenv/unittest-dedb:dev \
      || error_exit 'unable to start the DE database container'

  sleep 10
fi

docker run --rm -v $(pwd):/build -w /build --link $DBCONTAINER:postgres -it discoenv/buildenv /bin/bash
