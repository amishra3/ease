/**
 * Core implementation of EASE. Provides the central {@link com.mwmd.aem.search.core.indexing.IndexServer}
 * implementation and various listeners. Queue maintenance and index data transfer happens asynchronously, which is why
 * this package contains a number of Runnable implementations.
 */
package com.mwmd.aem.search.core.indexing.impl;
