package com.mwmd.aem.search.core.indexing.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.mwmd.aem.search.core.indexing.IndexException;
import com.mwmd.aem.search.core.indexing.IndexOperation;
import com.mwmd.aem.search.core.indexing.IndexServer;
import com.mwmd.aem.search.core.indexing.IndexService;
import com.mwmd.aem.search.core.indexing.ResourceBinary;
import com.mwmd.aem.search.core.indexing.ResourceIndexer;
import com.mwmd.aem.search.core.indexing.ResourceReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthias Wermund
 */
public class IndexTransfer implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(IndexTransfer.class);
    private IndexServiceImpl indexService;
    public boolean terminated;

    public void stop() {
        this.terminated = true;
    }

    public IndexTransfer(IndexServiceImpl indexService) {
        this.indexService = indexService;
    }

    @Override
    public void run() {

        ResourceResolver resolver = null;
        IndexServer server = indexService.getServer();
        if (server == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("No index server available.");
            }
            return;
        }

        boolean modifiedIndex = false;

        try {
            resolver = indexService.getResolverFactory().getAdministrativeResourceResolver(null);
            VersionManager versionManager = resolver.adaptTo(Session.class).getWorkspace().getVersionManager();

            while (!this.terminated) {

                boolean modifiedQueue = false;
                modifiedIndex = false;

                Resource queueRes = resolver.getResource(IndexService.QUEUE_ROOT);
                if (queueRes != null) {
                    Iterator<Resource> jobs = queueRes.listChildren();
                    while (jobs.hasNext()) {
                        Resource jobRes = jobs.next();
                        try {
                            ValueMap jobData = jobRes.adaptTo(ValueMap.class);
                            String jobPath = jobData.get(IndexService.PN_PATH, String.class);
                            String jobAction = jobData.get(IndexService.PN_ACTION, String.class);
                            String jobRevision = jobData.get(IndexService.PN_REVISION, String.class);
                            if (IndexOperation.ADD.toString().equals(jobAction)) {
                                Resource targetRes = resolver.getResource(jobPath);
                                if (targetRes != null) {
                                    // resolve any selected content version at this point
                                    targetRes = VersioningUtil.resolveRevision(versionManager, targetRes, jobRevision);
                                    if (targetRes != null) {
                                        // drill into jcr:content
                                        Resource contentRes = targetRes.getChild(JcrConstants.JCR_CONTENT);
                                        if (contentRes != null) {
                                            targetRes = contentRes;
                                        }
                                        if (indexService.getIndexer(targetRes) != null) {
                                            try {
                                                add(server, targetRes, jobPath);
                                                modifiedIndex = true;
                                                if (LOG.isDebugEnabled()) {
                                                    LOG.debug("Indexed {} at node {}", jobPath, targetRes.getPath());
                                                }
                                            } catch (Exception e) {
                                                LOG.error("Error transferring item to index: " + jobPath, e);
                                            }
                                        } else {
                                            if (LOG.isDebugEnabled()) {
                                                LOG.debug("Ignoring {}", jobPath);
                                            }
                                        }
                                    } else {
                                        LOG.warn("Ignoring due to failed revision resolution: {}", jobPath);
                                    }
                                } else {
                                    LOG.warn("Ignoring due to content not found: {}", jobPath);
                                }
                            } else if (IndexOperation.REMOVE.toString().equals(jobAction)) {
                                try {
                                    server.remove(jobPath);
                                } catch (Exception e) {
                                    LOG.error("Error removing item from index: " + jobPath, e);
                                }
                                modifiedIndex = true;
                            }
                            jobRes.adaptTo(Node.class).remove();
                            modifiedQueue = true;
                        } catch (Exception e) {
                            LOG.error("Error processing index job " + jobRes.getName(), e);
                        }
                    }

                    if (modifiedQueue) {
                        resolver.adaptTo(Session.class).save();
                    }
                    if (modifiedIndex) {
                        server.commit();
                    }

                }
                // if no change to queue, wait until notification or timeout until next attempt
                if (!modifiedQueue) {
                    try {
                        synchronized (this) {
                            this.wait(5000);
                        }
                    } catch (InterruptedException e) {
                        if (!this.terminated) {
                            LOG.error("Interrupted transfer without termination flag.");
                            break;
                        }
                    }
                }
            }
        } catch (LoginException e) {
            LOG.error("Error creating resource resolver.", e);
        } catch (RepositoryException e) {
            LOG.error("Error during repository access.", e);
            if (modifiedIndex) {
                try {
                    server.rollback();
                } catch (Exception e1) {
                    LOG.error("Error rolling back index changes.", e1);
                }
            }
        } catch (Exception e) {
            LOG.error("Error during queue processing.", e);
        } finally {
            if (resolver != null) {
                resolver.close();
            }
        }
    }

    private void add(IndexServer server, Resource resource, String containerPath) throws IndexException, RepositoryException {

        Map<String, Object> data = new HashMap<String, Object>();
        // start content parsing
        ResourceBinary binary = readContent(data, resource, containerPath, null, true);
        if (binary != null) {
            server.add(containerPath, data, binary);
        } else {
            server.add(containerPath, data);
        }
    }

    private ResourceBinary readContent(Map<String, Object> data, Resource resource, String containerPath, String forceResourceType, boolean extractBinary) {

        String resourceType = StringUtils.defaultIfEmpty(forceResourceType, resource.getResourceType());
        ResourceIndexer indexer = indexService.getIndexer(resourceType);
        if (indexer == null) {
            indexer = indexService.getIndexer(resource);
        }
        ResourceBinary binary = null;
        if (indexer != null && indexer.accepts(resource)) {
            indexer.indexData(data, resource, containerPath);
            if (extractBinary) {
                binary = indexer.getBinary(resource);
            }
            List<ResourceReference> references = indexer.getReferences(resource);
            for (ResourceReference ref : references) {
                if (binary == null && extractBinary) {
                    binary = readContent(data, ref.getResource(), containerPath, ref.getForceResourceType(), true);
                } else {
                    readContent(data, ref.getResource(), containerPath, ref.getForceResourceType(), false);
                }
            }
        }
        return binary;
    }
}
