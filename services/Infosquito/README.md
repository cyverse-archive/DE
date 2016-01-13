# Infosquito

Infosquito is the indexer for the Discovery Environment. It reads the contents 
of the iRODS home folders and summarizes their interesting bits for 
Elastic Search. 

Infosquito is designed for scalability and availability. Indexing is broken up 
into discrete tasks and managed by a Beanstalk work queue. Tasks may be 
injected into the work queue by other services as well.

* [Installation](INSTALL.md)

