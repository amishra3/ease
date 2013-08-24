/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import java.io.InputStream;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author matth_000
 */
@Getter @Setter
public class ResourceBinary {
    
    private String name;
    
    private String contentType;
    
    private String path;
    
    private Long size;
    
    private InputStream stream;
    
}
