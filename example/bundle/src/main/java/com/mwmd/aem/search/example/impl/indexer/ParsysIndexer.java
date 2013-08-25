package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import com.mwmd.aem.search.core.indexing.ResourceReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.sling.api.resource.Resource;

/**
 *
 * @author Matthias Wermund
 */
@Indexer(resourceTypes = "foundation/components/parsys")
public class ParsysIndexer extends AbstractResourceIndexer {

    @Override
    public List<ResourceReference> getReferences(Resource resource) {
        Iterator<Resource> resources = resource.listChildren();
        List<ResourceReference> children = new ArrayList<ResourceReference>();
        while (resources.hasNext()) {
            children.add(new ResourceReference(resources.next()));
        }
        return children;
    }
}
