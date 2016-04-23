#!/bin/sh

set -x
set -e

java -jar /facepalm-standalone.jar -m update -A $POSTGRES_USER -U $POSTGRES_USER -d de -h 127.0.0.1 -p 5432 -f /database.tar.gz
java -jar /facepalm-standalone.jar -m update -A $POSTGRES_USER -U $POSTGRES_USER -d metadata -h 127.0.0.1 -p 5432 -f /metadata-db.tar.gz
java -jar /facepalm-standalone.jar -m update -A $POSTGRES_USER -U $POSTGRES_USER -d notifications -h 127.0.0.1 -p 5432 -f /notification-db.tar.gz
java -jar /facepalm-standalone.jar -m update -A $POSTGRES_USER -U $POSTGRES_USER -d permissions -h 127.0.0.1 -p 5432 -f /permissions-db.tar.gz
