#!/bin/sh

set -e
set -x

DIR=$1
CMD=$2

if [ -z $CMD ]; then
    CMD=test2junit
fi

lein exec build-all.clj lein-plugins libs

cd $DIR
lein $CMD
