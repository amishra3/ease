/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author matth_000
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexer {
    
    String[] resourceTypes();
    
}
