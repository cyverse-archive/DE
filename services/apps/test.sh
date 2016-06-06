#!/bin/sh

set -e

error_exit() {
    echo 1>&2
    echo "TEST FAILED: $@" 1>&2
    exit 1
}

CMD=$1

if [ -z $CMD ]; then
    CMD=test2junit
fi

OS=$(uname)
DBCONTAINER=apps-de-db

if [ $(docker ps -qf "name=$DBCONTAINER" | wc -l) -gt 0 ]; then
    docker kill $DBCONTAINER
fi

if [ $(docker ps -aqf "name=$DBCONTAINER" | wc -l) -gt 0 ]; then
    docker rm $DBCONTAINER
fi

# Pull the build environment.
docker pull discoenv/buildenv || error_exit 'unable to pull the build environment image'

# Check for syntax errors.
docker run --rm -v $(pwd):/build -w /build discoenv/buildenv lein eastwood \
    || error_exit 'lint errors were found'

# Pull the DE database image.
docker pull discoenv/unittest-dedb:dev || error_exit 'unable to pull the DE database image'

# Start the DE database container.
docker run --name $DBCONTAINER -d discoenv/unittest-dedb:dev \
    || error_exit 'unable to start the DE database container'

# Wait for the DE database container to start up.
sleep 10

# Run the tests.
docker run --rm -v $(pwd):/build -w /build --link $DBCONTAINER:postgres discoenv/buildenv lein $CMD \
    || error_exit 'there were unit test failures'

# Display a success message.
echo 1>&2
echo "TEST SUCCEEDED" 1>&2
