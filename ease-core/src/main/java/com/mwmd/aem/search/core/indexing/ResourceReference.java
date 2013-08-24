/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.core.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;

/**
 *
 * @author matth_000
 */
@AllArgsConstructor @Getter @Setter
public class ResourceReference {
    
    private Resource resource;
    
    private String forceResourceType;

    public ResourceReference(Resource resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "ResourceReference{" + "resource=" + (resource == null ? null : resource.getPath()) + ", forceResourceType=" + forceResourceType + '}';
    }
    
    
    
}
