/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.solr.impl;

import com.mwmd.aem.search.core.indexing.ResourceBinary;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.apache.solr.common.util.ContentStream;

/**
 *
 * @author matth_000
 */
public class AssetContentStream implements ContentStream {
    
    private String name;
    
    private String contentType;
    
    private String sourceInfo;
    
    private Long size;
    
    private InputStream stream;
    
    private Reader reader;
    
    public AssetContentStream(ResourceBinary binary) {
        
        this.name = binary.getName();
        this.contentType = binary.getContentType();
        this.sourceInfo = binary.getPath();
        
        // access file
        this.size = binary.getSize();
        this.stream = binary.getStream();
        this.reader = new InputStreamReader(stream);
    }

    @Override
    public String getName() {
        
        return this.name;
    }

    @Override
    public String getSourceInfo() {
        
        return this.sourceInfo;
    }

    @Override
    public String getContentType() {
        
        return this.contentType;
    }

    @Override
    public Long getSize() {
        
        return this.size;
    }

    @Override
    public InputStream getStream() throws IOException {
        
        return this.stream;
    }

    @Override
    public Reader getReader() throws IOException {
        
        return this.reader;
    }
    
}
