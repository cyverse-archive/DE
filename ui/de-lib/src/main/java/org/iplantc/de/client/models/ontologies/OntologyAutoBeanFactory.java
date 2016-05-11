package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author aramsey
 */
public interface OntologyAutoBeanFactory extends AutoBeanFactory {

    AutoBean<Ontology> getOntology();

    AutoBean<OntologyHierarchy> getHierarchy();

    AutoBean<OntologyHierarchyFilterReq> getHierarchyReq();

    AutoBean<OntologyHierarchyList> getHierarchyList();

    AutoBean<OntologyList> getOntologyList();

    AutoBean<TargetIDList> getTargetIdList();

    AutoBean<OntologyVersionDetail> getVersionDetail();

}
