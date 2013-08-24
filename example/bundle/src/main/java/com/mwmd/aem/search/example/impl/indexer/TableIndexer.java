/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author matth_000
 */
@Indexer(resourceTypes = "foundation/components/table")
public class TableIndexer extends AbstractResourceIndexer {

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
        
        ValueMap properties = resource.adaptTo(ValueMap.class);
        String text = properties.get("tableData", String.class);
        if (StringUtils.isNotBlank(text)) {
            putMultiValue(data, IndexFields.TEXT, text);
        }
    }

}
