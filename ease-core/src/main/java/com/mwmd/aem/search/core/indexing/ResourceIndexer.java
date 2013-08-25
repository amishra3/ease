package com.mwmd.aem.search.core.indexing;

import com.mwmd.aem.search.core.annotation.Indexer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;

/**
 * This is the central interface for custom component indexing. Each implementation of an indexer matches one or many
 * resourceType. A resourceType is a construct of Apache Sling, which identifies the type of node. The type can be part
 * of a repository node, but isn't always stored there. The index data generation uses two central methods:
 * <p><i>indexData</i><br/>
 * Fills the data collection with values based on the passed in content resource. This is the core of the index data
 * generation.
 * </p>
 * <p><i>getReferences</i><br/>
 * Returns references to linked or contained content resources. For example, a paragraph system would return references
 * to all of its contained content resources.
 * </p>
 * The other methods are important too, but far less frequently used.
 * <br/><b>To be loaded by the {@link IndexServer}, all implementations must specify the {@link Indexer} annotation;
 * also the build script must include the <i>EASE SCR</i> library during compilation.</b>
 * <br/>Every indexer implementation must be thread-safe as there is only one instance at runtime as OSGi component.
 *
 * @author Matthias Wermund
 */
public interface ResourceIndexer {

    /**
     * Empty list, to be returned by calls to <i>getReferences</i> in case this component never contains or links to
     * referenced resources, in order to not create another empty list per invocation of the indexer.
     */
    List<ResourceReference> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<ResourceReference>(0));

    /**
     * Adds values to the data collection based on the provided content resource. The structure of the data collection
     * must map the expected data structure of the utilized search technology implementation. This method is only called
     * if the indexer is already identified as appropriate for the content resource, and it accepted the resource.
     *
     * @param data data collection
     * @param resource content resource to extract data from
     * @param containerPath path of the index item where this data collection belongs to; for example a page path
     */
    void indexData(Map<String, Object> data, Resource resource, String containerPath);

    /**
     * Returns references to linked or contained resources, which should get indexed along this resource.
     *
     * @param resource content resource to extract references from
     * @return list of found references, must not return null; use <i>EMPTY_LIST</i> for an empty response
     */
    List<ResourceReference> getReferences(Resource resource);

    /**
     * Returns the binary data which should get indexed and is present on the given resource. For example, if the
     * resource is a Digital Asset, this method could return its original rendition binary.<br/>
     * Please note that only one binary can get indexed with one index item. In most cases this means that only the
     * first indexer should return a binary descriptor.
     *
     * @param resource content resource to extract binary data from
     * @return a binary descriptor or null
     */
    ResourceBinary getBinary(Resource resource);

    /**
     * Identifies if a content resource is applicable for this indexer implementation. In some cases, the resourceType
     * specified in {@link Indexer} isn't sufficient to identify if a resource really should get indexed. There can be
     * additional requirements, for example if the page is activated, has content or similar. This method is always
     * called before any data extraction happens.
     *
     * @param resource content resource to potentially index
     * @return if true, the data extraction is for the given resource is possible
     */
    boolean accepts(Resource resource);
}
