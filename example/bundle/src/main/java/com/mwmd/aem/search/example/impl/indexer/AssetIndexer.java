package com.mwmd.aem.search.example.impl.indexer;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.AbstractResourceIndexer;
import com.mwmd.aem.search.core.indexing.ResourceBinary;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.wcm.api.NameConstants;
import com.mwmd.aem.search.example.IndexFields;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;

/**
 *
 * @author Matthias Wermund
 */
@Indexer(resourceTypes = "dam:AssetContent")
public class AssetIndexer extends AbstractResourceIndexer {

    @Override
    public void indexData(Map<String, Object> data, Resource resource, String containerPath) {

        Asset asset = resource.adaptTo(Asset.class);
        if (asset != null) {
            data.put(IndexFields.PATH, asset.getPath());
            Object tagsArr = asset.getMetadata(NameConstants.PN_TAGS);
            if (tagsArr != null && tagsArr instanceof Object[]) {
                Object[] tags = (Object[]) tagsArr;
                for (Object tag : tags) {
                    putMultiValue(data, IndexFields.TAGS, tag.toString());
                }
            }

            String title = StringUtils.trimToNull((String) asset.getMetadataValue("dc:title"));
            if (title == null || title.trim().length() == 0) {
                title = asset.getName();
            }
            data.put(IndexFields.TITLE, title);
        }
    }

    @Override
    public ResourceBinary getBinary(Resource resource) {

        Asset asset = resource.adaptTo(Asset.class);
        ResourceBinary binary = null;
        if (asset != null) {
            binary = new ResourceBinary();
            binary.setName(asset.getName());
            binary.setPath(resource.getPath());
            binary.setContentType(asset.getMimeType());
            // get original rendition
            Rendition original = asset.getOriginal();
            if (original != null) {
                binary.setSize(original.getSize());
                binary.setStream(original.getStream());
                return binary;
            }
        }
        return binary;
    }

    @Override
    public boolean accepts(Resource resource) {

        Asset asset = resource.adaptTo(Asset.class);
        if (asset != null) {
            String mime = StringUtils.lowerCase(asset.getMimeType());
            return mime != null && (mime.contains("pdf") || mime.contains("word"));
        }
        return false;
    }
}
