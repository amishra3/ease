Getting started with EASE
========

Requirements
--------

Tested on AEM 5.6.1.

EASE Maven dependencies
--------

To start with your own search integration project, all you need to do is add the following dependencies to your Maven pom.xml file:
```xml
	<!-- EASE dependencies -->
	<dependency>
		<groupId>com.mwmd</groupId>
		<artifactId>ease-core</artifactId>
		<version>0.7</version>
		<scope>provided</scope>
	</dependency></scope>			
	</dependency>	
	<dependency>
		<groupId>com.mwmd</groupId>
		<artifactId>ease-scr</artifactId>
		<version>0.7</version>
		<scope>provided</scope>			
	</dependency>
	...
	<!-- Add this to your pom.xml or to your own Nexus server -->
	<repositories>
        <repository>
            <id>mwmd</id>
            <name>mwmd repository</name>
            <url>http://nexus.wmd-software.com/content/groups/public/</url>
            <layout>default</layout>
        </repository>
    </repositories>
```	
These are all necessary dependencies to have the EASE API for development. 

Creating custom indexers
--------

Now that you've the API available, you can start building your own indexers. The first step is to build at least one indexer for your page resource type. 
```java
	...
	import com.mwmd.aem.search.core.annotation.Indexer;
	import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;

	@Indexer(resourceTypes = "geometrixx/components/contentpage")
	public class GeometrixxPageIndexer extends AbstractResourceIndexer {

		@Override
		public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
			String title = resource.adaptTo(ValueMap.class).get("jcr:title", String.class);
			data.put("title", title);
		}
	}
```	
Implement *getReferences()* if to continue indexing with child components of your page. You can create indexers for all resource types which you plan to index.
	
Deploy connector bundle and dependencies
--------

Above Maven dependencies add the API to your project. To connect to search platform, you'll need also a platform-specific connector bundle. Each connector bundle brings a number of additional dependencies. All of these bundles, including the Core API bundle,
need to get deployed to the AEM OSGi container. 
There are various ways how to deploy bundles to AEM, and you can choose the way which suits your project best. The example project does it using Adobe's [*content-package-maven-plugin*](http://dev.day.com/docs/en/cq/current/core/how_to/how_to_use_the_vlttool/vlt-mavenplugin.html).
Alternatively, you can upload the bundles to CRX or the Felix OSGi console manually.

You can find the *embeddeds* configuration of the example project [here](https://github.com/mwmd/ease/blob/master/example/content/pom.xml). The *embeddeds* contains all dependencies you need to use the Apache Solr connector.

Query index data
--------

At this time, EASE doesn't provide generic interfaces to query the search platform; its primary focus is the index data generation and transfer. However, a major advantage of using EASE is that the connector already brings all
dependencies with it to query. In case of the initial Apache Solr integration, this means that the standard Solr Java client [*SolrJ*](http://wiki.apache.org/solr/Solrj) is part of the deployed bundles. It can be used in your components and servlets, which makes the
query generation very straightforward.