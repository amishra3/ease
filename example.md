Example project implementation
=========

A fully integrated [example implementation](https://github.com/mwmd/ease/tree/master/example) based on [Adobe's Maven archtype](http://dev.day.com/docs/en/cq/current/core/how_to/how_to_use_the_vlttool/vlt-mavenplugin.html#multimodule-content-package-archetype)
is available as source on GitHub. From the same folder, you can download a pre-build CRX package for installation in your AEM instance as well.

The example project indexes default Geometrixx content and integrates with Apache Solr. It provides a simple search UI to test queries and support binary indexing. It uses facet filtering and full text search.
It's best to try out with a vanilla AEM 5.6.1 Author. Follow these steps to get it running:

1. Install a fresh AEM 5.6.1 Author on standard port (4502) by double-clicking on the quickstart JAR file.
1. Download [Apache Solr 4](http://lucene.apache.org/solr/) ZIP archive and extract it.
1. Open a command prompt, navigate to \{Solr-directory\}/example and execute "java -jar start.jar".
1. Login (admin / admin) in CRX Package Manager at http://localhost:4502/crx/packmgr. Upload & install the example CRX package.
1. Open the search UI at http://localhost:4502/apps/ease-example/web/search.html
1. Trigger a full index.

* You can use full text search and see the facet values changing based on the search results.
* You can verify the status of the index at the Solr UI at http://localhost:8983/solr/#/collection1
* You can monitor the framework at \{AEM-directory\}/crx-quickstart/logs/ease.log
