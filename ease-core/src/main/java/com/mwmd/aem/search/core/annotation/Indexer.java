package com.mwmd.aem.search.core.annotation;

import com.mwmd.aem.search.core.indexing.ResourceIndexer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an indexer for a particular resourceType. The annotation fulfills two purposes:
 * <ul>
 * <li>It creates an OSGi component via SCR annotation scanning.</li>
 * <li>It specifies which resourceType(s) the indexer is accepting.</li>
 * </ul>
 * The annotation must only be used on classes implementing {@link ResourceIndexer}.
 *
 * @author Matthias Wermund
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexer {

    /**
     * Specifies the resourceType(s) which an indexer is registered for.
     *
     */
    String[] resourceTypes();
}
