/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import java.util.Map;

/**
 *
 * @author matth_000
 */
public interface IndexServer {
    
    void add(String path, Map<String,Object> data) throws IndexException;
    
    void add(String path, Map<String,Object> data, ResourceBinary binary) throws IndexException;
    
    void remove(String path) throws IndexException;
    
    void commit() throws IndexException;
    
    void rollback() throws IndexException;
    
    void clear() throws IndexException;
    
}
