/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.solr.impl;

import com.mwmd.aem.search.core.indexing.IndexException;
import com.mwmd.aem.search.core.indexing.IndexServer;
import com.mwmd.aem.search.core.indexing.ResourceBinary;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author matth_000
 */
@Service
@Properties({
    @Property(name = "url", label = "Solr URL", description = "Address of Solr instance where to send update requests to."),
    @Property(name = "id_prefix", label = "ID prefix", description = "Prefix for path to generate unique ID.", value = "AEM:"),
    @Property(name = "id_field", label = "ID field name", description = "Field name of ID field.", value = "id")
        
})
@Component(policy = ConfigurationPolicy.REQUIRE)
public class SolrIndexServer implements IndexServer {
    
    private static final Logger LOG = LoggerFactory.getLogger(SolrIndexServer.class);
    
    private static final DateFormat FORMAT_TIMESTAMP = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
    
    private static final NumberFormat FORMAT_NUMBER = NumberFormat.getInstance();
    
    static {
        FORMAT_NUMBER.setGroupingUsed(false);
        FORMAT_NUMBER.setMinimumFractionDigits(0);
    }
    
    private ConcurrentUpdateSolrServer solrServer;
    
    private String idPrefix;
    
    private String idField;
    
    @Activate
    protected void activate(Map<String, Object> properties) {
        
        String url = (String) properties.get("url");
        if (url != null) {
            this.solrServer = new ConcurrentUpdateSolrServer(url, 25, 2);
            try {
                this.solrServer.commit();
            } catch (Exception e) {
                LOG.error("Error creating connection to index server.", e);
            }
        }
        this.idPrefix = (String) properties.get("id_prefix");
        this.idField = (String) properties.get("id_field");
    }
    
    @Deactivate
    protected void deactivate() {
        
        if (this.solrServer != null) {
            this.solrServer.shutdown();
        }
    }

    @Override
    public void add(String path, Map<String, Object> data) throws IndexException {
        
        SolrInputDocument document = new SolrInputDocument();
        document.addField(this.idField, buildId(path));
        Set<String> fields = data.keySet();
        for (String field: fields) {            
            if (field.equals(this.idField)) {
                throw new IndexException("ID field must not get populated through data fields.");                
            }
            Object fieldValue = data.get(field);
            if (fieldValue != null) {
                // should work with both Collection and single values
                document.addField(field, fieldValue);
            }
        }
        try {
            solrServer.add(document);
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    @Override
    public void add(String path, Map<String, Object> data, ResourceBinary binary) throws IndexException {                
        
        if (binary == null) {
            add(path, data);
            return;
        }
                
        ContentStreamUpdateRequest request = new ContentStreamUpdateRequest("/update/extract"); 
        request.addContentStream(new AssetContentStream(binary));
        request.setParam("literal.id", buildId(path));
        Set<String> fields = data.keySet();
        for (String field: fields) {            
            if (field.equals(this.idField)) {
                throw new IndexException("ID field must not get populated through data fields.");                
            }
            Object fieldValue = data.get(field);
            if (fieldValue != null) {
                if (fieldValue instanceof Collection) {
                    Collection<?> fieldValues = (Collection) fieldValue;
                    for (Object value: fieldValues) {
                        // TODO deal with numeric & date type values (format to string)
                        request.setParam("literal." + field, valueToString(value));                        
                    }                    
                } else {
                    // assume it's one of the supported data types
                    request.setParam("literal." + field, valueToString(fieldValue));
                }
            }
        }
        request.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
        try {
            solrServer.request(request);
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }
    
    private static String valueToString(Object value) {
        
        if (value instanceof Date) {
            return FORMAT_TIMESTAMP.format(value);
        }
        if (value instanceof Calendar) {
            return FORMAT_TIMESTAMP.format(((Calendar)value).getTime());
        }
        if (value instanceof Number) {
            return FORMAT_NUMBER.format(value);
        }
        return value.toString();
    }
    
    private String buildId(String path) {
        
        return StringUtils.defaultIfEmpty(this.idPrefix, "AEM:") + path;
    }

    @Override
    public void remove(String path) throws IndexException {
        
        try {
            solrServer.deleteById(buildId(path));
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    @Override
    public void commit() throws IndexException {
        
        try {
            solrServer.commit();
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    @Override
    public void rollback() throws IndexException {
        
        try {
            solrServer.rollback();
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }

    @Override
    public void clear() throws IndexException {
        
        try {
            UpdateRequest request = new UpdateRequest("/update");
            request.deleteByQuery(this.idField + ":" + ClientUtils.escapeQueryChars(this.idPrefix) + "*");            
            solrServer.request(request);
            solrServer.commit();
        } catch (Exception e) {
            throw new IndexException(e);
        }
    }
    
    
    
}
