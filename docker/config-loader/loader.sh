#!/bin/sh

set -e
set -x

mkdir -p /loader/etc/iplant/de/logging
mkdir -p /loader/etc/nginx
mkdir -p /loader/etc/logstash-forwarder
mkdir -p /loader/etc/docker-gc

if [ -d /etc/iplant/de/logging ]; then
  cp -r /etc/iplant/de/logging/ /loader/etc/iplant/de/
fi

if [ -d /etc/iplant/de ]; then
  cp -r /etc/iplant/de/ /loader/etc/iplant/
fi

if [ -d /etc/nginx ]; then
  cp -r /etc/nginx/ /loader/etc/
fi

if [ -d /etc/docker-gc ]; then
  cp -r /etc/docker-gc/ /loader/etc/
fi
