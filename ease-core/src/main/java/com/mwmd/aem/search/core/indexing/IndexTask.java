/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author matth_000
 */
@Data @AllArgsConstructor
public class IndexTask {
    
    private IndexOperation op;
    
    private String path;
    
    private String revision;
    
}
