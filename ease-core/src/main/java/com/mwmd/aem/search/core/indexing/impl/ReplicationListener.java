package com.mwmd.aem.search.core.indexing.impl;

import com.mwmd.aem.search.core.indexing.IndexService;
import com.day.cq.replication.ReplicationAction;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Matthias Wermund
 */
@Service
@Property(name = "event.topics", value = ReplicationAction.EVENT_TOPIC)
@Component
public class ReplicationListener implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ReplicationListener.class);
    @Reference
    private SlingSettingsService slingSettings;
    @Reference
    private IndexService indexService;
    private boolean isAuthor;

    @Activate
    protected void activate() {

        this.isAuthor = slingSettings.getRunModes().contains("author");
    }

    @Override
    public void handleEvent(Event event) {

        if (!isAuthor) {
            return;
        }

        ReplicationAction action = ReplicationAction.fromEvent(event);
        String path = action.getPath();
        switch (action.getType()) {
            case ACTIVATE:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Activation invoked for {}", path);
                }
                indexService.add(path, action.getRevision());
                break;

            case DEACTIVATE:
            case DELETE:
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Removal received for {}", path);
                }
                indexService.remove(path);
                break;
        }
    }
}
