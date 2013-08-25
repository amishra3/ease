package com.mwmd.aem.search.core.indexing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;

/**
 * POJO to identify a reference from an indexed resource to a related indexed resource. In some cases the resourceType
 * of a reference is stored on the resource (example: page type), but in other cases it isn't (example: fixed included
 * component on a page). For the latter cases, each indexer can provide information which resource type should be used
 * to find a matching indexer for the returned resource. If present, a forced resource type always overrides the
 * resource type present on the resource.
 *
 * @author Matthias Wermund
 */
@AllArgsConstructor
@Getter
@Setter
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
