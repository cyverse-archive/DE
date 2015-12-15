---
layout: page
title: DE Architecture 
---

# Infrastructure

The following sections describe the components of the infrastructure on which the DE operates.

## Data Store
The DE is provides access and management of data via the CyVerse 
[Data Store](http://www.cyverse.org/ci/data-store), which is built on top of 
[iRODS](http://irods.org/).

## Compute Platform(s)
The DE integrates the Data Store with [HTCondor](https://research.cs.wisc.edu/htcondor/) and the
[Agave Platform](http://agaveapi.co/) to provide a large set of tools for performing resource
intense analyses.

## PostgreSQL
Nearly all application use a database. The DE uses [PostgreSQL](http://www.postgresql.org/). The 
schemas can be found 
[here]({{site.github.repository_url}}/tree/master/databases).

## RabbitMQ
[RabbitMQ](https://www.rabbitmq.com/) is used throughout our services, but primarily to integrate 
our services with iRODS.

## Elasticsearch
The DE uses [Elasticsearch](https://www.elastic.co/products/elasticsearch) to provide search and 
other capabilities.

## Docker
[Docker](https://www.docker.com/) is used throughout the DE architecture. Most importantly, all of 
the tools used on the compute platform are packaged as Docker images. 

Additionally, the components of the DE application are packaged as Docker containers.

All of these images can be accessed through our organization page on 
[Docker Hub](https://hub.docker.com/r/discoenv/).

# Application Architecture
The DE is composed of backend services and a user interface (UI).

## Backend Services
The DE backend is built as a micro-services architecture. Each of these services are contained in 
the [services/]({{site.github.repository_url}}/tree/master/services) folder.

The functionality of the micro-service architecture is aggregated in the `Terrain` service, and
exposed as a RESTful [api]({{site.github.url}}/api).

More information about the backend micro-service architecture and implementation may be found
[here]({{site.github.url}}/services).

## UI
All of the UI services are provided by the DE api. The application itself is built with 
[GWT](http://www.gwtproject.org/). 

More information about the UI design and implementation may be found [here]({{site.github.url}}/ui).

