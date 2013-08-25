package com.mwmd.aem.search.core.indexing;

import com.mwmd.aem.search.core.indexing.impl.IndexTransfer;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * POJO for one index job. Identifies an operation to be processed by {@link IndexTransfer} and is persisted in the
 * repository until then.
 *
 * @author Matthias Wermund
 */
@Data
@AllArgsConstructor
public class IndexTask {

    private IndexOperation op;
    private String path;
    private String revision;
}
