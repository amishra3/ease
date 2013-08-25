EASE for Adobe Experience Manager
====

#### [Getting started with EASE](getting_started.md)
#### [Example project implementation](example.md)

What is EASE?
----------
EASE is short for *External AEM Search Extension*. The EASE framework helps to integrate external search into AEM by simplifying the indexing process of data within AEM in a generic way. EASE allows to focus on indexing the data without
caring about any proprietary search technology. After the data is indexed, it gets translated via a technology-specific connector to the utilized search platform. At this point it's indexed
and can be used for search functionality using search clients etc. Creating queries to already indexed data is easy in most cases, the indexing part is the heavy lifting. EASE aims to help
with that. 

The approach of generic data extraction makes EASE useful in many different scenarios besides search export too (content archival etc.).

EASE is open source since 8/2013 and got introduced at the [EVOLVE'13](http://www.evolve13.com/) conference.

What is AEM?
----------
AEM is short for Adobe Experience Manager, which is a content- and asset-management platform, and part of the Adobe Marketing Cloud. AEM was earlier known as Adobe CQ5, and as Adobe WEM.

Features
-----------
* Generates structured index data
* Plugin mechanism to index custom content structures
* Binary data indexing
* Indexing of specific content versions
* Push-based data generation on AEM Author triggered by standard replication
* Full index generation
* Generic connector structure
* Bulk transfer of index data

Search platform connectors
----------
Currently the following search platforms are supported via open source connectors:
* [Apache Solr 4](http://lucene.apache.org/solr/)

EASE modules
-----------

* *ease-parent*: Root Apache Maven project
* *ease-core*: API and core implementation
* *ease-scr*: Plugin annotations processor for [Apache Felix SCR Maven Plugin](http://felix.apache.org/documentation/subprojects/apache-felix-maven-scr-plugin.html)
* *ease-solr*: Connector for [Apache Solr](http://lucene.apache.org/solr/)

Javadoc API Documentation
--------

* [ease-core](http://nexus.wmd-software.com/api/ease-core/current/)
* [ease-scr](http://nexus.wmd-software.com/api/ease-scr/current/)
* [ease-solr](http://nexus.wmd-software.com/api/ease-solr/current/)


#### [Getting started with EASE](getting_started.md)
#### [Example project implementation](example.md)