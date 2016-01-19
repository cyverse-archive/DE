#!/bin/sh

set -e
set -x

CMD=$1

if [ -z $CMD ]; then
    CMD=test
fi

OS=$(uname)
DBCONTAINER=apps-de-db

if [ $(docker ps -qf "name=$DBCONTAINER" | wc -l) -gt 0 ]; then
    docker kill $DBCONTAINER
fi

if [ $(docker ps -aqf "name=$DBCONTAINER" | wc -l) -gt 0 ]; then
    docker rm $DBCONTAINER
fi

docker run --name $DBCONTAINER -e POSTGRES_PASSWORD=notprod -d -p 35432:5432 discoenv/de-db
sleep 5
docker run --rm --link $DBCONTAINER:postgres discoenv/de-db-loader:dev
docker run --rm -v $(pwd):/build -v ~/.m2:/root/.m2 -w /build --link $DBCONTAINER:postgres clojure lein $CMD
