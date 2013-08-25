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
 * Base implementation for a {@link ResourceIndexer}. Can be used as the parent class for easy implementation of custom
 * indexers. No indexing is performed in the implementation of each method in this class, child classes are supposed to
 * override the methods where needed.<br>
 * Note: It's not necessary to inherit from this class. Custom indexers can directly implement {@link ResourceIndexer}.
 *
 * @author Matthias Wermund
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

    /**
     * Helper method to add values into the data collection entries, which are allowed to be multiple values. For
     * example, if there is a multi value field "text", this method can get called multiple times with each different
     * value and will create a list of values in the data collection if necessary.
     *
     * @param data the data collection
     * @param field the key of the multi value field
     * @param value the value to be added
     */
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
