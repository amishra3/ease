package com.mwmd.aem.search.core.indexing.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.mwmd.aem.search.core.indexing.IndexOperation;
import static com.mwmd.aem.search.core.indexing.IndexService.PN_ACTION;
import static com.mwmd.aem.search.core.indexing.IndexService.PN_PATH;
import static com.mwmd.aem.search.core.indexing.IndexService.PN_REVISION;
import static com.mwmd.aem.search.core.indexing.IndexService.QUEUE_ROOT;
import com.mwmd.aem.search.core.indexing.IndexTask;
import com.mwmd.aem.search.core.indexing.ResourceIndexer;
import java.util.Calendar;
import java.util.Iterator;
import java.util.UUID;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthias Wermund
 */
public class IndexQueueWriter implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IndexQueueWriter.class);
    private IndexServiceImpl indexService;
    public boolean terminated;

    public void stop() {
        this.terminated = true;
    }

    public IndexQueueWriter(IndexServiceImpl indexService) {

        this.indexService = indexService;
    }

    @Override
    public void run() {

        ResourceResolver resolver = null;
        try {
            resolver = indexService.getResolverFactory().getAdministrativeResourceResolver(null);
            while (!terminated) {
                boolean relevant = true;
                IndexTask task = indexService.getTasks().take();
                String path = task.getPath();

                /**
                 * AEM will deactivate all child resources within this branch , so remove them too.
                 */
                if (IndexOperation.REMOVE.equals(task.getOp())) {
                    Resource resource = resolver.getResource(path);
                    if (resource != null) {
                        Iterator<Resource> children = resource.listChildren();
                        while (children.hasNext()) {
                            Resource child = children.next();
                            // jcr:content nodes are never indexed standalone in the first place
                            if (!JcrConstants.JCR_CONTENT.equals(child.getName())) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Triggered automatic removal of child {}", child.getPath());
                                }
                                indexService.getTasks().add(new IndexTask(IndexOperation.REMOVE, child.getPath(), null));
                            }
                        }
                    }
                    /**
                     * Only in case it's ADD, filter further, because it has to be indexable content.
                     */
                } else if (IndexOperation.ADD.equals(task.getOp())) {
                    Resource resource = resolver.getResource(path);

                    if (resource == null) {
                        LOG.warn("Resource not found: {})", path);
                        relevant = false;
                    } else {
                        // drill into jcr:content
                        Resource contentRes = resource.getChild(JcrConstants.JCR_CONTENT);
                        if (contentRes != null) {
                            resource = contentRes;
                        }
                        // filter by known ResourceIndexer                        
                        ResourceIndexer indexer = indexService.getIndexer(resource);
                        if (indexer == null || !indexer.accepts(resource)) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Didn't find valid indexer for {} {}", resource.getResourceType(), resource.getPath());
                            }
                            relevant = false;
                        }
                    }
                }

                if (relevant) {
                    /*
                     * At this point the index operation is considered valid and should get written to persistant queue in the repository.
                     */
                    try {
                        Resource queueRes = resolver.getResource(QUEUE_ROOT);
                        Node queueNode;
                        if (queueRes == null) {
                            // create queue root
                            Node parent = resolver.getResource(ResourceUtil.getParent(QUEUE_ROOT)).adaptTo(Node.class);
                            queueNode = parent.addNode(ResourceUtil.getName(QUEUE_ROOT), JcrResourceConstants.NT_SLING_ORDERED_FOLDER);
                        } else {
                            queueNode = queueRes.adaptTo(Node.class);
                        }
                        Node itemNode = queueNode.addNode(UUID.randomUUID().toString(), JcrConstants.NT_UNSTRUCTURED);
                        itemNode.setProperty(PN_PATH, task.getPath());
                        itemNode.setProperty(PN_ACTION, task.getOp().toString());
                        itemNode.setProperty(PN_REVISION, task.getRevision());
                        itemNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
                        resolver.adaptTo(Session.class).save();
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Added index queue node {}", task);
                        }
                        indexService.notifyTransfer();
                    } catch (RepositoryException e) {
                        LOG.error("Error during index queue modification.", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            if (this.terminated) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Writer interrupted during shutdown.");
                }
            } else {
                LOG.error("Interrupted writer without termination flag.");
            }
        } catch (LoginException e) {
            LOG.error("Error creating resource resolver.", e);
        } catch (Exception e) {
            LOG.error("Error during queue writing.", e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Queue writer stopped");
        }
    }
}
