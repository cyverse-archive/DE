#!/bin/bash

set -x
set -e

java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d de -f /database.tar.gz
psql -h 127.0.0.1 -U $POSTGRES_USER -d postgres -c "CREATE DATABASE metadata WITH OWNER $POSTGRES_USER;"
java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d metadata -f /metadata-db.tar.gz
echo "INSERT INTO value_types (id, name) VALUES
('4cb79d83-e694-4acf-aa60-ddadee087b24', 'Timestamp'),
('8130ec25-2452-4ff0-b66a-d9d3a6350816', 'Boolean'),
('29f9f4fd-594c-493d-9560-fe8851084870', 'Number'),
('c6cb42cd-7c47-47a1-8704-f6582b510acf', 'Integer'),
('c29b0b10-d660-4582-9eb7-40c4f1699dd6', 'String'),
('127036ff-ef19-4665-a9a9-7a6878d9813a', 'Multiline Text'),
('28a1f81a-8b4f-4940-bcd4-e39241bf15dc', 'URL/URI'),
('b17ed53d-2b10-428f-b38a-c9dec3dc5127', 'Enum');" | psql -h 127.0.0.1 -U $POSTGRES_USER -d metadata
psql -h 127.0.0.1 -U $POSTGRES_USER -d postgres -c "CREATE DATABASE notifications WITH OWNER $POSTGRES_USER;"
java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d notifications -f /notification-db.tar.gz
psql -h 127.0.0.1 -U $POSTGRES_USER -d postgres -c "CREATE DATABASE permissions WITH OWNER $POSTGRES_USER"
java -jar /facepalm-standalone.jar -m init -A $POSTGRES_USER -U $POSTGRES_USER -h 127.0.0.1 -d permissions -f /permissions-db.tar.gz
