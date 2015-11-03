# Discovery Environment

The Discovery Environment is a web portal for life science research, giving
access to the data store and compute of the iPlant cyber-infrastructure.

This project contains all of the public code used in the Discovery Environment.
That includes internally developed libraries, tools, services, database schemas
and migrations, build tool plugins, GWT/GXT UI, and Ansible scripts.

All of the code in this project was previously available as separate git
repositories at various granularities, but we decided to consolidate everything
to make it easier to navigate, package, and manage. You can find the old
repositories under the iPlantArchive organization, should you need them.

The top-level directories __lein-plugins__, __libs__, __services__, __tools__,
__databases__, __ui__, and __ansible__ are the categories that the code falls
into. Each direct subdirectory contains a separate project. For instance,
__services/Terrain/__ contains the service that the Discovery Environment UI
interacts with directly. Clojure project subfolders are Leiningen subprojects
in and of themselves, and the UI subdirectory is a Gradle project.

Many subdirectories have their own README.md files which can be consulted if
more specific information is desired.

## UI

The Discovery Environment UI is the front-end GWT/GXT code with which a user
directly interacts. This code lives under the __ui__ subdirectory, and this
section assumes that subdirectory is your present working directory.

This project is built with [Gradle](http://www.gradle.org/). Please refer to the Gradle
documentation for an intro to the
[build script basics](http://www.gradle.org/docs/current/userguide/tutorial_using_tasks.html).
The top level is itself a gradle project, but there are multiple sub-projects defined within.

In addition to the basic gradle build files, here is an explanation of our additions:

* __versions.gradle__: This contains common library version definitions for libraries used throughout the subprojects.
* __iplant/webapps.gradle__: Contains the sub-project definitions for the DE webapps.
* __iplant/modules.gradle__: Contains the sub-project definitions for the non-webapp projects used by the webapps.
* __iplant/code-style__: Contains code style definitions for the eclipse and idea environments.

### Gradle basics

To find the list of tasks you can execute:

    ./gradlew tasks

### Building everything
The webapp requires that the `de.properties` file be located in `/etc/iplantc/de/`.

To build and run the DE draft or prod self-executing war:

    ./gradlew runDraftWar
    ./gradlew runWar

To generate production war file:

    ./gradlew createProdWar # Resulting war will be in target/

To generate a GWT draft war file:

    ./gradlew draftBootRepackage


### GWT Super Dev Mode

To start the Super Dev Mode code-server:

    ./gradlew :de:sdm

Once this code-server is started, you can use it to debug any of our wars which
have source-maps enabled. Only the draft wars have source-maps enabled. For
instructions on how to use Super Dev Mode, please refer to the [GWT project
site](http://www.gwtproject.org/articles/superdevmode.html).

## Backend

### Development environment setup

You will need the following tools and plugins to develop for the DE backend.

* __JDK 7__: The backend code should work fine on either Oracle's JDK or on the OpenJDK.
* __Leiningen__: Clojure multi-tool. See http://leiningen.org/.
* __Maven 3__: Unfortunately not all of the backend code is in Clojure.
* __lein-exec__: A plugin for running stand-alone Clojure scripts. See https://github.com/kumarshantanu/lein-exec.
* __lein-midje__: A Clojure test framework. See https://github.com/marick/Midje.

For kifshare development, you will also need the following tools:

* __node.js__: A javascript runtime environment.
* __npm__: The node.js package manager
* __grunt__: A build tool for javascript projects.

The easiest way to get the above on OS X is with homebrew:

    brew install npm
    npm install -g grunt-cli
    npm install -g grunt

If you intend to build the services with the build-all.clj script, you __will__ need those tools installed.

### Building everything

We've provided a Clojure script that will go through all of the projects, build
them, and move the builds to a builds/ directory. You run it like this from the
top level of this project:

    lein exec build-all.clj

If you just want to create the Leiningen-compatible checkout symlinks, run the
following command:

    lein exec build-all.clj symlinks

To build just the services, run:

    lein exec build-all.clj services

The rest:

    lein exec build-all.clj tools
    lein exec build-all.clj lein-plugins
    lein exec build-all.clj libs
    lein exec build-all.clj databases

To archive the builds into the builds/ directory, add the --archive option to
the above commands.

    lein exec build-all.clj tools --archive

### Building a specific project

For the Leiningen projects, you'll want to create the checkouts symlinks first.
They help make sure that you're developing against the latest version of our
libraries.

    lein exec build-all.clj symlinks

If you want to just build a specific project, go into the project's directory
and call the appropriate build tool. For example:

    cd services/Terrain
    lein clean
    lein uberjar


(For libraries, you probably want lein install rather than uberjar.)

### Setting and getting a version for everything

We've hacked together a bash script that will iterate over all of the Clojure
projects and will set their versions to the same version, including the
versions of the iPlant developed dependencies (aside from metadactyl).

    ./set-version 2.0.10

Please, run that against a branch and submit a pull request.

We've also hacked a script together to report the versions of Clojure projects.

    ./get-versions

Again, these were hacked together extremely quickly and still have sharp edges.
Be careful and do all modifications in a branch.

## Ansible

These scripts are used to deploy all the other pieces of the Discovery
Environment. Please consult the in-folder documentation for more details.
