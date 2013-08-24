/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;

/**
 *
 * @author matth_000
 */
public interface ResourceIndexer {
    
    List<ResourceReference> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<ResourceReference>(0));
    
    void indexData(Map<String, Object> data, Resource resource, String containerPath);
    
    List<ResourceReference> getReferences(Resource resource);
    
    ResourceBinary getBinary(Resource resource);
    
    boolean accepts(Resource resource);
    
}
