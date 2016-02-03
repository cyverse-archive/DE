#!/bin/sh

set -e
set -x

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

docker pull discoenv/buildenv
docker run --rm -v $(pwd):/build -w /build discoenv/buildenv lein eastwood '{:exclude-namespaces [apps.protocols :test-paths] :linters [:wrong-arity :wrong-ns-form :wrong-pre-post :wrong-tag :misplaced-docstrings]}'

docker pull discoenv/de-db
docker run --name $DBCONTAINER -e POSTGRES_PASSWORD=notprod -d -p 35432:5432 discoenv/de-db
sleep 10
docker pull discoenv/de-db-loader:dev
docker run --rm --link $DBCONTAINER:postgres discoenv/de-db-loader:dev
docker run --rm -v $(pwd):/build -w /build --link $DBCONTAINER:postgres discoenv/buildenv lein $CMD
