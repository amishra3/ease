package com.mwmd.aem.search.core.indexing;

import com.mwmd.aem.search.core.indexing.impl.IndexQueueWriter;
import org.apache.sling.api.resource.Resource;

/**
 * Central service of the index management in AEM. Central register of {@link ResourceIndexer indexers}. Maintains the
 * active {@link IndexServer} implementation at runtime. Provides a central place to apply OSGi configurations.
 *
 * @author Matthias Wermund
 */
public interface IndexService {

    /**
     * Adds an item to the index. The item is uniquely identified by its content path. Supports selection of a specific
     * content version.
     *
     * @param path content path
     * @param revision repository revision id; can be null
     */
    void add(String path, String revision);

    /**
     * Removes an item from the index. The item is uniquely identified by its content path.
     *
     * @param path content path
     */
    void remove(String path);

    /**
     * Triggers a full index of all configured paths. Depending on the <i>PROPERTY_INCLUDE_NON_ACTIVATED</i>
     * flag, only activated resources are exported. Otherwise, all content is exported. The configured path filters are
     * respected in either way, e.g. it will only index data in the configured branches.<br/>
     * <b>Ignores version history, always activates the HEAD version.</b>
     */
    void all();

    /**
     * Returns an indexer for a specific resourceType. Please note that each indexer can verify by the actual content
     * resource, if it's applicable or not. In case the content resource is available, always the overridden method
     * should be used.
     *
     * @param resourceType content resource type
     * @return indexer matching the resource type or null
     */
    ResourceIndexer getIndexer(String resourceType);

    /**
     * Returns an indexer for a content resource. The indexer is identified by the resource, and then asked if it
     * accepts the content resource.
     *
     * @param resource content node which has to match to the indexer
     * @return indexer matching the resource or null
     */
    ResourceIndexer getIndexer(Resource resource);

    /**
     * Returns the {@link IndexServer} which is provided by a connection to a specific external search technology. Can
     * return null in case there is no bundle implementing {@link IndexServer} active.
     *
     * @return index server or null
     */
    IndexServer getServer();
    /**
     * Root node for the index job queue. Will get automatically created by the {@link IndexQueueWriter} to persist
     * index operations.
     */
    String QUEUE_ROOT = "/var/searchindex_queue";
    /**
     * Property of the content path within a job in the index queue.
     */
    String PN_PATH = "path";
    /**
     * Property of the index operation within a job in the index queue.
     */
    String PN_ACTION = "action";
    /**
     * Property of the revision id within a job in the index queue.
     */
    String PN_REVISION = "rev";
    /**
     * OSGi property for path filters. Resources are only treated as valid index modifications, if their content path
     * matches one of these filters.
     */
    String PROPERTY_PATHFILTER = "pathfilters";
    /**
     * OSGi property to control if only activated resources should get indexed. This is only used during a full index
     * operation, because there is no further event that identifies which are the resources that should get indexed.
     */
    String PROPERTY_INCLUDE_NON_ACTIVATED = "includenonactivated";
}
