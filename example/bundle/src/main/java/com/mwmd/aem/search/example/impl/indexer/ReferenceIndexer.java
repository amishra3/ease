/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import static com.mwmd.aem.search.core.indexing.ResourceIndexer.EMPTY_LIST;
import com.mwmd.aem.search.core.indexing.ResourceReference;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 *
 * @author matth_000
 */
@Indexer(resourceTypes = "foundation/components/reference")
public class ReferenceIndexer extends AbstractResourceIndexer {

    @Override
    public List<ResourceReference> getReferences(Resource resource) {
        
        ValueMap properties = resource.adaptTo(ValueMap.class);
        String path = properties.get("path", String.class);
        List<ResourceReference> references;
        if (StringUtils.isNotBlank(path)) {
            references = new ArrayList<ResourceReference>(1);
            references.add(new ResourceReference(resource.getResourceResolver().getResource(path)));            
        } else {
            references = EMPTY_LIST;
        }
        return references;
    }
    
    
    
}
