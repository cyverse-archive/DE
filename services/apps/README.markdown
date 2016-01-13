# apps

apps is a platform for hosting App Services for the Discovery Environment web application.

Once running, endpoint documentation may be viewed by navigating a web browser to the server and
port this service is configured to run on. For example, if the service were running on the local
host and configured to listen to port 65007, then viewing http://localhost:65007/docs in a browser
would display the documentation of all available endpoints.

## Installation

Please see [DE Installation Instructions](http://cyverse.github.io/DE/ansible/).

## Unit Testing And Development

You'll need to have Docker installed for this stuff to work.

The test.sh script uses the discoenv/de-db and discoenv/de-db-loader images to get a PostgreSQL
container running locally (listening on local port 5432) and then runs the Apps service's unit tests
from within a Clojure container created with the official Docker image.

test.sh will kill and remove any containers named 'de-db' when it first starts up. If you need to
manually kill and remove the de-db container:

    docker kill de-db
    docker rm de-db

test.sh will also run 'boot2docker shellinit' if you're running it on OS X.

The repl.sh script is a variation of test.sh which will start up an interactive REPL instead of
running the unit tests.

The psql.sh script will use the official PostgreSQL Docker image to create a container that links to
the de-db container and fires up psql. The password is 'notprod' (without the quotes).

The file test.properties is a config file for Apps that is set up to assume that everything will be
running locally. To get Apps up and running with de-db as the database, fire off test.sh and then
run the following:

    lein run -- --config test.properties
