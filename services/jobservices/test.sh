#!/bin/sh

set -e
set -x
docker-compose -f test.yml up -d --force-recreate dedb rabbit dind
docker-compose -f test.yml up --force-recreate test
docker-compose -f test.yml stop
docker-compose -f test.yml rm -v -f
