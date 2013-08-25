package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import com.mwmd.aem.search.example.IndexFields;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 *
 * @author Matthias Wermund
 */
@Indexer(resourceTypes = "foundation/components/text")
public class MyTextIndexer extends AbstractResourceIndexer {

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
        String text = resource.adaptTo(ValueMap.class).get("text", String.class);
        putMultiValue(data, IndexFields.TEXT, text);
    }
}
