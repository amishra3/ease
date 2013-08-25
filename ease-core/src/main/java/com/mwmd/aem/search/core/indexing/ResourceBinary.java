package com.mwmd.aem.search.core.indexing;

import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;

/**
 * Descriptor of binary source data to index. Can for example be used to link to a Digital Asset. Provides standard
 * information about a file, including the mimeType, and access to the binary data using an {@link InputStream}.
 *
 * @author Matthias Wermund
 */
@Getter
@Setter
public class ResourceBinary {

    private String name;
    private String contentType;
    private String path;
    private Long size;
    private InputStream stream;
}
