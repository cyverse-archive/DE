Discovery Environment (DE) ELK Design
=====================================

# Roles
The DE's ELK stack deployment is contained in the following roles:
1. elk-elasticsearch
1. elk-logstash
1. elk-kibana
1. logstash-forwarder

# Design

## User Info
TODO

## DE HTTP Access logging
The DE generates logs for each request and subsequent response made or received.
The only exclusion at this point is the UI servlet container (requests from browser
client to the servlet). These requests are restructured and forwarded to the
DE API (which is ```donkey``` at the moment).

The points where HTTP transactions (request/response pairs) are logged 
are shown in the list below:

1. _Outgoing_ *->>* Requests/responses from the UI to the the DE API
1. *->>* _Incoming_ Requests/responses to the DE API 
1. _Outgoing_ *->>* Requests/responses from the DE API to micro-service APIs
1. *->>* _Incoming_ Requests/responses to the micro-service APIs
1. _Outgoing_ *->>* Requests/responses from the micro-service APIs (when applicable)

### HTTP Request IDs
All HTTP transactions in the DE have a unique ID of the format, ```[SOURCE]-[UUID]```, where 
```SOURCE``` is the name of the component (e.g. _UI_, _Donkey_, etc). This ID is kept in the 
following HTTP header: 

    X-DE-request-id
    
When a service receives a request which contains this header, its value is placed 
in the following header for all requests made from that service:

    X-DE-forwarded-request-id
    
When a response is received, the receiver is responsible for copying the original request's
```X-DE-request-id``` header to the response's headers.

The purpose of this is to provide traceability of actions through the DE's many
micro-services. The uniqueness also allows logstash to combine request/response pairs
into single events within elasticsearch.

### Request/Response Log Structure
TODO
