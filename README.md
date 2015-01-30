# DiscoveryEnvironment
The Discovery Environment is a web portal for life science research, giving access to the 
data store and compute of the iPlant cyber-infrastructure.

This project is built with [Gradle](http://www.gradle.org/). Please refer to the Gradle documentation for an intro to the [build script basics](http://www.gradle.org/docs/current/userguide/tutorial_using_tasks.html).
The top level is itself a gradle project, but there are multiple sub-projects defined within.

In addition to the basic gradle build files, here is an explanation of our additions:

* __versions.gradle__: This contains common library version definitions for libraries used throughout the subprojects.
* __iplant/webapps.gradle__: Contains the sub-project definitions for the DE webapps.
* __iplant/modules.gradle__: Contains the sub-project definitions for the non-webapp projects used by the webapps.
* __iplant/eclipse.gradle__: Contains the gradle eclipse plugin configuration used to set up this repo for eclipse developement.
* __iplant/idea.gradle__: Contains the gradle idea plugin configuration used to set up this repo for intellij idea development.
* __iplant/webappBootstrap.gradle__: Helper script for creating webapp configuration files.
* __iplant/code-style__: Contains code style definitions for the eclipse and idea environments.

## Gradle basics

To find the list of tasks you can execute:

    ./gradlew tasks
    
## Building everything

To build and run the DE in jetty:

    ./gradlew :de:runDraftJetty
    
To generate production war file:

    ./gradlew :de:createProdWar
    
To generate a GWT draft war file:

    ./gradlew :de:createDraftWar
    
    
## GWT Super Dev Mode

To start the Super Dev Mode code-server:

    ./gradlew :de:sdm
    
Once this code-server is started, you can use it to debug any of our wars which have source-maps enabled. Only the draft wars have source-maps enabled. For instructions on how to use Super Dev Mode, please refer to the [GWT project site](http://www.gwtproject.org/articles/superdevmode.html).


