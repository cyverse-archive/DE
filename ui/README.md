# DiscoveryEnvironment
The Discovery Environment is a web portal for life science research, giving access to the 
data store and compute of the CyVerse cyber-infrastructure.

This project is built with [Gradle](http://www.gradle.org/). Please refer to the Gradle 
documentation for an intro to the 
[build script basics](http://www.gradle.org/docs/current/userguide/tutorial_using_tasks.html).
The top level is itself a gradle project, but there are multiple sub-projects defined within.

In addition to the basic gradle build files, here is an explanation of our additions:

* __versions.gradle__: This contains common library version definitions for libraries used throughout the subprojects.
* __iplant/webapps.gradle__: Contains the sub-project definitions for the DE webapps.
* __iplant/modules.gradle__: Contains the sub-project definitions for the non-webapp projects used by the webapps.
* __iplant/code-style__: Contains code style definitions for the eclipse and idea environments.

## Gradle basics

To find the list of tasks you can execute:

    ./gradlew tasks
    
## Building everything
The webapp requires that the `de.properties` file be located in `/etc/iplantc/de/`.

To build and run the DE draft or prod self-executing war:

    ./gradlew runDraftWar
    ./gradlew runWar
    
To generate production war file:

    ./gradlew createProdWar # Resulting war will be in target/
    
To generate a GWT draft war file:

    ./gradlew draftBootRepackage
    
    
## GWT Super Dev Mode

To start the Super Dev Mode code-server:

    ./gradlew :de:sdm
    
Once this code-server is started, you can use it to debug any of our wars which have source-maps 
enabled. Only the draft wars have source-maps enabled. For instructions on how to use 
Super Dev Mode, please refer to 
the [GWT project site](http://www.gwtproject.org/articles/superdevmode.html).


