/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing.impl;

import com.day.cq.commons.jcr.JcrConstants;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionManager;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author matth_000
 */
public class VersioningUtil {
    
    private static final Logger LOG = LoggerFactory.getLogger(VersioningUtil.class);
    
    private VersioningUtil() {
        
    }
    
    public static Resource resolveRevision(VersionManager versionManager, Resource resource, String revision) throws RepositoryException {
        
        if (StringUtils.isBlank(revision)) {
            return resource;
        }
        Node node = resource.adaptTo(Node.class);
        if (!node.isNodeType(JcrConstants.MIX_VERSIONABLE)) {
            if (node.hasNode(JcrConstants.JCR_CONTENT)) {
                node = node.getNode(JcrConstants.JCR_CONTENT);
                if (!node.isNodeType(JcrConstants.MIX_VERSIONABLE)) {
                    node = null;
                }
            } else {
                node = null;
            }
        }
        if (node == null) {
            LOG.warn("Unable to select revision {}, node isn't versionable: {}", revision, resource.getPath());
            return null;
        }            
        if (LOG.isDebugEnabled()) {
            LOG.debug("Selecting revision {} of {}", revision, node.getPath());
        }
        VersionHistory history = versionManager.getVersionHistory(node.getPath());
        Version version = null;
        try {
            version = history.getVersion(revision);                
        } catch (VersionException e) {                
        }
        if (version == null) {
            try {
                version = history.getVersionByLabel(revision);                
            } catch (VersionException e) {                
            }
        }
        if (version == null || !version.hasNode(JcrConstants.JCR_FROZENNODE)) {
            LOG.warn("No revision {} found for node {}", revision, node.getPath());
            return null;
        }    
        return resource.getResourceResolver().getResource(version.getNode(JcrConstants.JCR_FROZENNODE).getPath());
    }
    
    public static Resource resolveReference(Resource resourceFrom, String contentPathFrom, String referenceTo) {
        
        if (referenceTo.equals(contentPathFrom)) {            
            // same resource
            return resourceFrom;
        }
        if (referenceTo.startsWith(contentPathFrom + "/")) {
            // sub-resource, navigate within revision context
            return resourceFrom.getChild(referenceTo.substring(contentPathFrom.length()+1));
        }
        // no sub-resource, leave revision context
        return resourceFrom.getResourceResolver().getResource(referenceTo);
    }
    
    
}
