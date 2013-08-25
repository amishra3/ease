package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import com.mwmd.aem.search.example.IndexFields;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 *
 * @author Matthias Wermund
 */
@Indexer(resourceTypes = "geometrixx/components/title")
public class TitleIndexer extends AbstractResourceIndexer {

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
        ValueMap properties = resource.adaptTo(ValueMap.class);
        String title = properties.get("jcr:title", String.class);
        if (StringUtils.isNotBlank(title)) {
            putMultiValue(data, IndexFields.TEXT, title);
        }
    }
}
