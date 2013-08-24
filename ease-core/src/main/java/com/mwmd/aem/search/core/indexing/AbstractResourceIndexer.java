/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;

/**
 *
 * @author matth_000
 */
public abstract class AbstractResourceIndexer implements ResourceIndexer {    

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
        
    }

    @Override
    public List<ResourceReference> getReferences(Resource resource) {
        
        return EMPTY_LIST;
    }

    @Override
    public ResourceBinary getBinary(Resource resource) {
        
        return null;
    }

    @Override
    public boolean accepts(Resource resource) {
        
        return true;
    }
    
    protected void putMultiValue(Map<String, Object> data, String field, Object value) {
        
        if (value != null) {
            Object currentValue = data.get(field);
            if (currentValue == null) {
                data.put(field, value);
            } else if (currentValue instanceof Collection) {
                Collection collection = (Collection) currentValue;
                collection.add(value);         
            } else {
                List list = new ArrayList();
                list.add(currentValue);
                list.add(value);
                data.put(field, list);
            }            
        }
    }
}
