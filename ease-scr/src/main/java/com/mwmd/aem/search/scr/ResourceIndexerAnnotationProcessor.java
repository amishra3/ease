/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mwmd.aem.search.scr;

import com.mwmd.aem.search.core.annotation.Indexer;
import com.mwmd.aem.search.core.indexing.ResourceIndexer;
import java.util.List;
import org.apache.felix.scrplugin.SCRDescriptorException;
import org.apache.felix.scrplugin.SCRDescriptorFailureException;
import org.apache.felix.scrplugin.annotations.AnnotationProcessor;
import org.apache.felix.scrplugin.annotations.ClassAnnotation;
import org.apache.felix.scrplugin.annotations.ScannedClass;
import org.apache.felix.scrplugin.description.ClassDescription;
import org.apache.felix.scrplugin.description.ComponentConfigurationPolicy;
import org.apache.felix.scrplugin.description.ComponentDescription;
import org.apache.felix.scrplugin.description.ServiceDescription;

/**
 *
 * @author matth_000
 */
public class ResourceIndexerAnnotationProcessor implements AnnotationProcessor {

    @Override
    public void process(ScannedClass scannedClass, ClassDescription classDesc) throws SCRDescriptorException, SCRDescriptorFailureException {
        
        List<ClassAnnotation> indexers = scannedClass.getClassAnnotations(Indexer.class.getName());
        scannedClass.processed(indexers);

        for(ClassAnnotation cad: indexers) {
            
            // generate component and service
            ComponentDescription cd = new ComponentDescription(cad);
            cd.setName(classDesc.getDescribedClass().getName());
            cd.setConfigurationPolicy(ComponentConfigurationPolicy.OPTIONAL);
            cd.setLabel(null);
            cd.setDescription(null);
            cd.setCreateMetatype(false);
            classDesc.add(cd);
            ServiceDescription sd = new ServiceDescription(cad);
            sd.addInterface(ResourceIndexer.class.getName());
            classDesc.add(sd);
            
        }
    }

    @Override
    public String getName() {
        
        return "Search Indexer processor";
    }

    @Override
    public int getRanking() {
        
        return 1000;
    }
    
    
    
}
