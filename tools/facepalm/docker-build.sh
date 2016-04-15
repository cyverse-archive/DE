#!/bin/sh

set -e
set -x

if [ -z "$DOCKER_USER" ]; then
  DOCKER_USER=discoenv
fi

if [ -z "$DOCKER_REPO" ]; then
  DOCKER_REPO=facepalm
fi

if [ -z "$DOCKER_BRANCH" ]; then
  DOCKER_BRANCH=dev
fi

DB_DIR=../../databases

DE_DB_DIR=$DB_DIR/de-database-schema
JEX_DB_DIR=$DB_DIR/jex-db
META_DB_DIR=$DB_DIR/metadata
NOTIF_DB_DIR=$DB_DIR/notification-db
PERMS_DB_DIR=$DB_DIR/permissions

DE_DB_GZ=$DE_DB_DIR/database.tar.gz
JEX_DB_GZ=$JEX_DB_DIR/jex-db.tar.gz
META_DB_GZ=$META_DB_DIR/metadata-db.tar.gz
NOTIF_DB_GZ=$NOTIF_DB_DIR/notification-db.tar.gz
PERMS_DB_GZ=$PERMS_DB_DIR/permissions-db.tar.gz

CURR=$(pwd)

cd $DE_DB_DIR
./build.sh
cd $CURR

cd $JEX_DB_DIR
./build.sh
cd $CURR

cd $META_DB_DIR
./build.sh
cd $CURR

cd $NOTIF_DB_DIR
./build.sh
cd $CURR

cd $PERMS_DB_DIR
./build.sh
cd $CURR

cp $DE_DB_GZ .
cp $JEX_DB_GZ .
cp $META_DB_GZ .
cp $NOTIF_DB_GZ .
cp $PERMS_DB_GZ .

VERSION=$(cat version | sed -e 's/^ *//' -e 's/ *$//')
GIT_COMMIT=$(git rev-parse HEAD)

docker pull $DOCKER_USER/buildenv:latest

BUILDENV_GIT_COMMIT=$(docker inspect -f '{{ (index .Config.Labels "org.iplantc.de.buildenv.git-ref")}}' $DOCKER_USER/buildenv:latest)

docker run --rm -e "GIT_COMMIT=$GIT_COMMIT" -v $(pwd):/build -w /build $DOCKER_USER/buildenv:latest lein uberjar
docker build --build-arg git_commit=$GIT_COMMIT \
             --build-arg buildenv_git_commit=$BUILDENV_GIT_COMMIT \
             --build-arg version=$VERSION \
             --pull --rm -t "$DOCKER_USER/$DOCKER_REPO:$DOCKER_BRANCH" .
docker push $DOCKER_USER/$DOCKER_REPO:$DOCKER_BRANCH
