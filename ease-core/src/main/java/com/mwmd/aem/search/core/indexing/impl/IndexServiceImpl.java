/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing.impl;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.IndexOperation;
import com.mwmd.aem.search.core.indexing.IndexServer;
import com.mwmd.aem.search.core.indexing.IndexService;
import com.mwmd.aem.search.core.indexing.ResourceIndexer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.replication.ReplicationStatus;
import com.day.cq.replication.Replicator;
import com.day.cq.wcm.api.NameConstants;
import com.mwmd.aem.search.core.indexing.IndexTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.jcr.Session;
import javax.jcr.Value;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyUnbounded;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author matth_000
 */
@Service
@Properties({
    @Property(name = IndexService.PROPERTY_PATHFILTER, unbounded = PropertyUnbounded.ARRAY, 
        label = "Path filters", description = "Will only index resources whose path start with an item in this list."),
    @Property(name = IndexService.PROPERTY_INCLUDE_NON_ACTIVATED, label = "Include non-activated", 
        description = "If active, a full index operation will ignore the activation status of the resources.", boolValue = false)
})
@Component(label = "External Search Index Service", description = "Maintains indexing tasks for external search.", metatype = true)
public class IndexServiceImpl implements IndexService {
    
    private static final Logger LOG = LoggerFactory.getLogger(IndexServiceImpl.class);
    
    @Reference @Getter(AccessLevel.PACKAGE)
    private ResourceResolverFactory resolverFactory;
    
    @Reference(cardinality= ReferenceCardinality.OPTIONAL_MULTIPLE, bind="registerResourceIndexer", 
            unbind="deregisterResourceIndexer", referenceInterface = ResourceIndexer.class, policy = ReferencePolicy.DYNAMIC)
    private Map<String, ResourceIndexer> indexerMap = new HashMap<String, ResourceIndexer>();
    
    @Reference
    private SlingSettingsService slingSettings;
    
    @Reference(cardinality = ReferenceCardinality.OPTIONAL_UNARY, policy = ReferencePolicy.DYNAMIC)
    private IndexServer indexServer;
    
    @Reference
    private Replicator replicator;
       
    private ResourceResolver resolver;

    private List<String> pathFilter;
    
    private boolean includeNonActivated;
    
    private Thread queueWriterThread;
    
    private IndexQueueWriter queueWriter;
    
    @Getter(AccessLevel.PACKAGE)
    private BlockingQueue<IndexTask> tasks;
    
    private Thread queueTransferThread;
    
    private IndexTransfer queueTransfer;
    
    @Activate
    protected void activate(Map<String, Object> properties) {
        
        // create backgroud resolver
        try {
            this.resolver = resolverFactory.getAdministrativeResourceResolver(null);
        } catch (LoginException e) {
            LOG.error("Error creating resource resolver.", e);
            this.resolver = null;
        }
        // path filters
        String[] paths = (String[]) properties.get(PROPERTY_PATHFILTER);
        if (paths != null) {
            this.pathFilter = Arrays.asList(paths);
        } else {
            this.pathFilter = new ArrayList<String>();
        }
        Boolean nonActivated = (Boolean) properties.get(PROPERTY_INCLUDE_NON_ACTIVATED);
        this.includeNonActivated = nonActivated != null && nonActivated.booleanValue();
        
        boolean isAuthor = slingSettings.getRunModes().contains("author");   
        if (isAuthor) {
            this.tasks = new LinkedBlockingQueue<IndexTask>();
            this.queueWriter = new IndexQueueWriter(this);
            this.queueWriterThread = new Thread(this.queueWriter);
            this.queueWriterThread.start();
            this.queueTransfer = new IndexTransfer(this);
            this.queueTransferThread = new Thread(this.queueTransfer);
            this.queueTransferThread.start();
        }
    }
    
    @Deactivate
    protected void deactivate() {
        
        if (resolver != null) {
            resolver.close();
        }    
        if (queueWriterThread != null) {
            queueWriter.stop();
            queueWriterThread.interrupt();
        }
        if (queueTransferThread != null) {
            queueTransfer.stop();
            queueTransferThread.interrupt();
        }
    }

    @Override
    public void add(String path, String revision) {
        
        enqueue(new IndexTask(IndexOperation.ADD, path, revision));
    }

    @Override
    public void remove(String path) {
        
        enqueue(new IndexTask(IndexOperation.REMOVE, path, null));
        
    }    
    
    private void enqueue(IndexTask task) {
        
        String path = task.getPath();
        if (path == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Empty path");           
            }
            return;
        }

        boolean matchesPath = false;

        // filter by path
        for (String filter: this.pathFilter) {
            if (path.startsWith(filter)) {
                matchesPath = true;
                break;
            }
        }     
        if (!matchesPath) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Item not matching path filter, ignoring: {})", path);           
            }
            return;
        }

        // operation seems valid by first filters, hand over async to queueWriter
        this.tasks.add(task);
    }

    protected void registerResourceIndexer(ResourceIndexer indexer) {
                
        String[] resourceTypes = getResourceTypes(indexer);
        if (resourceTypes != null) {
            for (String resourceType: resourceTypes) {
                ResourceIndexer oldIndexer = this.indexerMap.put(resourceType, indexer);
                if (oldIndexer != null) {
                    LOG.warn("Duplicate indexer registration ({},{}) ignoring {}", 
                            new Object[] {
                                indexer.getClass().getName(),
                                oldIndexer.getClass().getName(),
                                oldIndexer.getClass().getName() 
                            });
                }
            }
            LOG.debug("Registering Indexer for {} , {} registered", Arrays.toString(resourceTypes), this.indexerMap.size());
        }
    }
    
    protected void deregisterResourceIndexer(ResourceIndexer indexer) {
                
        String[] resourceTypes = getResourceTypes(indexer);
        if (resourceTypes != null) {
            for (String resourceType: resourceTypes) {
                this.indexerMap.remove(resourceType);
            }
            LOG.debug("Deregistering Indexer for {} , {} registered", Arrays.toString(resourceTypes), this.indexerMap.size());
        }
    }

    @Override
    public ResourceIndexer getIndexer(String resourceType) {
        
        return this.indexerMap.get(resourceType);
    }
    
    @Override
    public ResourceIndexer getIndexer(Resource resource) {
        
        String resourceType = resource.getResourceType();
        if (JcrConstants.NT_FROZENNODE.equals(resourceType)) {
            resourceType = resource.adaptTo(ValueMap.class).get(JcrConstants.JCR_FROZENPRIMARYTYPE, String.class);
        }
        return getIndexer(resourceType);
    }
    
    @Override
    public IndexServer getServer() {
        
        return this.indexServer;
    }

    /**
     * Indexes all active content matching the path filter. Currently limited to Pages and Assets.
     */
    @Override
    public void all() {
        
        if (LOG.isInfoEnabled()) {
            LOG.info("Starting full index");
        }
        try {
            indexServer.clear();
            
            Session session = resolver.adaptTo(Session.class);

            for (String path: this.pathFilter) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Starting full index at {}", path);
                }
                Resource res = resolver.getResource(path);
                if (res != null) {
                    all(session, res.listChildren());
                }
            }        
        } catch (Exception e) {
            LOG.error("Error during full index.", e);
        }
        
    }
    
    
    private void all(Session session, Iterator<Resource> items) {
        
        while (items.hasNext()) {
            Resource res = items.next();                
            if (NameConstants.NT_PAGE.equals(res.getResourceType()) || DamConstants.NT_DAM_ASSET.equals(res.getResourceType())) {
                if (!this.includeNonActivated) {
                    ReplicationStatus status = replicator.getReplicationStatus(session, res.getPath());
                    if (status != null && (status.isActivated())) {
                        add(res.getPath(), null);
                    }                
                } else {
                    add(res.getPath(), null);
                }
            }
            all(session, res.listChildren());
        }
    }
    
    
    private static String[] getResourceTypes(ResourceIndexer indexer) {
        
        Indexer annotation = indexer.getClass().getAnnotation(Indexer.class);
        return annotation == null ? null : annotation.resourceTypes();
    }
    
    void notifyTransfer() {
        if (this.queueTransfer != null) {
            synchronized (this.queueTransfer) {
                this.queueTransfer.notify();
            }
        }
    }
    
}
