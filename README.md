EASE for Adobe Experience Manager
====


What is EASE?
----------
EASE is short for *External AEM Search Extension*. AEM is again short for Adobe Experience Manager, which is a content- and asset-management platform, and a part of the Adobe Marketing Cloud.
The EASE framework helps to integrate external search into AEM by simplifying the indexing process of data within AEM in a generic way. EASE allows to focus on indexing the data without
caring about any proprietary search technology. After the data is indexed, it gets translated via a technology-specific connector to the utilized search platform. At this point it's indexed
and can be used for search functionality using search clients etc. Creating queries to already indexed data is in most cases easy, the indexing part is the heavy lifting. EASE aims to help
with that. 

The generic data extraction makes EASE useful in many different scenarios besides search export (content archival etc.).

EASE is open source since 8/2013 and was introduced at the [EVOLVE'13](http://www.evolve13.com/) conference.

Features
-----------
* Generates structured index data
* Push-based data generation on AEM Author triggered by Content Replication
* Full index generation
* Binary data indexing
* Indexing of specific content versions
* Generic connector structure

Search platforms connectors
----------
Currently the following search platforms are supported via open source connectors:
* [Apache Solr 4.4](http://lucene.apache.org/solr/)

EASE modules
-----------

* *ease-parent*: root maven project
* *ease-core*: API and core implementation
* *ease-scr*: Plugin annotations processor for [Apache Felix SCR Maven Plugin](http://felix.apache.org/documentation/subprojects/apache-felix-maven-scr-plugin.html)
* *ease-solr*: Connector for [Apache Solr](http://lucene.apache.org/solr/)

Getting started
-----------

### Requirements ###

Tested on AEM 5.6.1.



Example implementation
-----------
adasd






