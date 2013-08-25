EASE for Adobe Experience Manager
====


What is EASE?
----------
EASE is short for *External AEM Search Extension*. The EASE framework helps to integrate external search into AEM by simplifying the indexing process of data within AEM in a generic way. EASE allows to focus on indexing the data without
caring about any proprietary search technology. After the data is indexed, it gets translated via a technology-specific connector to the utilized search platform. At this point it's indexed
and can be used for search functionality using search clients etc. Creating queries to already indexed data is in most cases easy, the indexing part is the heavy lifting. EASE aims to help
with that. 

The generic data extraction makes EASE useful in many different scenarios besides search export (content archival etc.).

EASE is open source since 8/2013 and was introduced at the [EVOLVE'13](http://www.evolve13.com/) conference.

What is AEM?
----------
AEM is again short for Adobe Experience Manager, which is a content- and asset-management platform, and a part of the Adobe Marketing Cloud. AEM was earlier known as Adobe CQ5, and as Adobe WEM.

Features
-----------
* Generates structured index data
* Plugin mechanism for indexing of custom content structures
* Push-based data generation on AEM Author triggered by Content Replication
* Full index generation
* Binary data indexing
* Indexing of specific content versions
* Generic connector structure

Search platforms connectors
----------
Currently the following search platforms are supported via open source connectors:
* [Apache Solr 4](http://lucene.apache.org/solr/)

EASE modules
-----------

* *ease-parent*: root Apache Maven project
* *ease-core*: API and core implementation
* *ease-scr*: Plugin annotations processor for [Apache Felix SCR Maven Plugin](http://felix.apache.org/documentation/subprojects/apache-felix-maven-scr-plugin.html)
* *ease-solr*: Connector for [Apache Solr](http://lucene.apache.org/solr/) JAVADOC

Getting started
-----------

### Requirements ###

Tested on AEM 5.6.1.

### API Documentation ###

* [ease-core javadoc](http://nexus.wmd-software.com/api/ease-core/current/)
* [ease-scr javadoc](http://nexus.wmd-software.com/api/ease-scr/current/)
* [ease-solr javadoc](http://nexus.wmd-software.com/api/ease-solr/current/)

### EASE Maven dependencies ###


### Connector Maven dependencies ###


Example implementation
-----------

A fully integrated [example implementation](https://github.com/mwmd/ease/tree/master/example) based on [Adobe's Maven archtype](http://dev.day.com/docs/en/cq/current/core/how_to/how_to_use_the_vlttool/vlt-mavenplugin.html#multimodule-content-package-archetype)
is available as source on GitHub. From the same folder, you can download a pre-build CRX package for installation in your AEM instance as well.

The example project indexes default Geometrixx content and integrates with Apache Solr. It provides a simple search UI to test queries and support binary indexing. It uses facet filtering and full text search.
It's best to try out with a vanilla AEM 5.6.1 Author. Follow these steps to get it running:

1. Install a fresh AEM 5.6.1 Author on standard port (4502) by double-clicking on the quickstart JAR file.
1. Download [Apache Solr 4](http://lucene.apache.org/solr/) ZIP archive and extract it.
1. Open a command prompt, navigate to \{Solr\}/example and execute "java -jar start.jar".
1. Login (admin / admin) in CRX Package Manager at http://localhost:4502/crx/packmgr. Upload & install the example CRX package.
1. Open the search UI at http://localhost:4502/apps/ease-example/web/search.html
1. Trigger a full index.

* You can use full text search and see the facet values changing based on the search results.
* You can verify the status of the index at the Solr UI at http://localhost:8983/solr/#/collection1
* You can monitor the framework at \{AEM\}/crx-quickstart/logs/ease.log





