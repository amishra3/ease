package com.mwmd.aem.search.core.indexing;

import java.io.InputStream;
import java.util.Map;

/**
 * Interface of an external index provider connection. This generic interface is the integration point of any specific
 * technology. There needs to be at least one implementation of this interface present in the OSGi container, to make
 * EASE working.<br>
 * To allow for bulk transfer of data, <i>add</i> and <i>remove</i> can get called multiple times. After those calls, a
 * finalizing call to <i>clear</i> or <i>rollback</i> is always made.
 *
 * @author Matthias Wermund
 */
public interface IndexServer {

    /**
     * Adds an item to the index. As most implementations will require a unique identifier, path can be used to either
     * use it as-is as ID, or generate the ID based on it.
     *
     * @param path content path (example: page path) of the indexed resource
     * @param data data collection of the indexed resource
     * @throws IndexException
     */
    void add(String path, Map<String, Object> data) throws IndexException;

    /**
     * Extended variation of <i>add</i> with support of binary data indexing.
     *
     * @param path content path (example: page path) of the indexed resource
     * @param data data collection of the indexed resource
     * @param binary descriptor and payload of the binary source data; the {@link InputStream} must get used before this
     * method returns
     * @throws IndexException
     */
    void add(String path, Map<String, Object> data, ResourceBinary binary) throws IndexException;

    /**
     * Removes one item from the index. There is no payload, but the index item must be identifiable by its path.
     *
     * @param path content path (example: page path) of the removed resource
     * @throws IndexException
     */
    void remove(String path) throws IndexException;

    /**
     * Finalizes the prior operations. Optimally, the specific implementation will only at this point commit the writing
     * transaction to the index. Alternatively, this method can be ignored and the data written immediately.
     *
     * @throws IndexException
     */
    void commit() throws IndexException;

    /**
     * Rolls back the prior operations. Optimally, the specific implementation will revoke all index modifications since
     * the last commit/rollback. Alternatively, this method can be ignored.
     *
     * @throws IndexException
     */
    void rollback() throws IndexException;

    /**
     * Method to clear the index of all data that has been indexed using the EASE implementation. This allows full
     * re-indexing operations, where the index is cleared of all AEM-data first and then subsequently gets re-added.
     * <b>Calls to this method are not followed by <i>commit</i>.</b>
     *
     * @throws IndexException
     */
    void clear() throws IndexException;
}
