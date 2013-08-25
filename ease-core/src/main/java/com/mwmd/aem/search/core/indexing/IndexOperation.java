package com.mwmd.aem.search.core.indexing;

/**
 * Identifies the operation of an index job. Currently the only operations are addition or removal of index data.
 * Updates of data are currently treated as <i>add</i>.
 *
 * @author Matthias Wermund
 */
public enum IndexOperation {

    ADD, REMOVE;
}
