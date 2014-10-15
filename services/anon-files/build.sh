#!/bin/sh
set -x

ITERATION=$1
USER=iplant
GROUP=iplant
BINNAME=anon-files
BUILDDIR=anon-files-build
BINDIR=/usr/local/lib/anon-files
LOGDIR=/var/log/anon-files

VERSION=$(cat version | sed -e 's/^ *//' -e 's/ *$//')
if [ -d "$BUILDDIR" ]; then
  rm -r $BUILDDIR
fi
mkdir -p $BUILDDIR/$BINDIR
mkdir -p $BUILDDIR/$LOGDIR
lein clean
lein deps
lein uberjar
cp target/$BINNAME-*-standalone.jar $BUILDDIR/$BINDIR
fpm -s dir -t rpm --directories $LOGDIR --version $VERSION --iteration $ITERATION --epoch 0 --prefix / --name $BINNAME --verbose -C $BUILDDIR --rpm-user $USER --rpm-group $GROUP -f .
