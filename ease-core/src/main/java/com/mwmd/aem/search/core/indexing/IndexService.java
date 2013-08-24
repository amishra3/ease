/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import org.apache.sling.api.resource.Resource;

/**
 *
 * @author matth_000
 */
public interface IndexService {
    
    void add(String path, String revision);
    
    void remove(String path);
    
    /**
     * Triggers a full index of all configured paths. Depending on the includeNonActivated
     * flag, only activated resources are exported. 
     * Ignores version history, always activates the HEAD version.
     */
    void all();
    
    ResourceIndexer getIndexer(String resourceType);
    
    ResourceIndexer getIndexer(Resource resourceType);
    
    IndexServer getServer();
    
    String QUEUE_ROOT = "/var/searchindex_queue";
    
    String PN_PATH = "path";
    
    String PN_ACTION = "action";
    
    String PN_REVISION = "rev";
    
    String PROPERTY_PATHFILTER = "pathfilters";
    
    String PROPERTY_INCLUDE_NON_ACTIVATED = "includenonactivated";
    
}
