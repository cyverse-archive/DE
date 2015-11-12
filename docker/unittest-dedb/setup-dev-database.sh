#!/bin/bash

set -x
set -e

java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d de -f /database.tar.gz
psql -h 127.0.0.1 -U $POSTGRES_USER -d postgres -c "CREATE DATABASE metadata WITH OWNER $POSTGRES_USER;"
java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d metadata -f /metadata-db.tar.gz
psql -h 127.0.0.1 -U $POSTGRES_USER -d postgres -c "CREATE DATABASE notifications WITH OWNER $POSTGRES_USER;"
java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d notifications -f /notification-db.tar.gz
