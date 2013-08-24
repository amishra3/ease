/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.ResourceReference;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import com.mwmd.aem.search.example.IndexFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

/**
 *
 * @author matth_000
 */
@Indexer(resourceTypes = {"geometrixx/components/contentpage", "geometrixx/components/homepage", 
    "foundation/components/page", "geometrixx/components/page"})
public class PageIndexer extends AbstractResourceIndexer {

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {
        
        ValueMap properties = resource.adaptTo(ValueMap.class);
        data.put(IndexFields.PATH, containerPath + ".html");
        data.put(IndexFields.TITLE, properties.get("jcr:title", String.class));
        Matcher language = Pattern.compile("\\/content\\/.*?\\/(.*?)/.*").matcher(containerPath);
        if (language.matches()) {
            data.put(IndexFields.LANGUAGE, language.group(1));
        }
        String description = properties.get("jcr:description", String.class);            
        if (description != null) {
            data.put(IndexFields.TEXT, description);
        }
    }
  
    @Override
    public List<ResourceReference> getReferences(Resource contentRes) {
        
        List<ResourceReference> references = new ArrayList<ResourceReference>();        
        if (contentRes != null) {
            Resource parRes = contentRes.getChild("par");
            if (parRes != null) {
                references = new ArrayList<ResourceReference>();
                references.add(new ResourceReference(parRes, "foundation/components/parsys"));
            }
        }
        return references;
    }
        
}
