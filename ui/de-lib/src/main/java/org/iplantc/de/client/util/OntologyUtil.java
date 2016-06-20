package org.iplantc.de.client.util;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyUtil {

    private static OntologyUtil INSTANCE;
    OntologyAutoBeanFactory factory;
    AvuAutoBeanFactory avuFactory;

    final String OPERATION_ATTR = "rdf:type";
    final String TOPIC_ATTR = "http://edamontology.org/has_topic";
    final String LABEL_ATTR = "rdfs:label";
    final String LABEL_UNIT = "value";

    final String UNCLASSIFIED_LABEL = "Unclassified";
    final String UNCLASSIFIED_IRI_APPEND = "_unclassified";

    private OntologyUtil() {
        factory = GWT.create(OntologyAutoBeanFactory.class);
        avuFactory = GWT.create(AvuAutoBeanFactory.class);
    }

    public static OntologyUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OntologyUtil();
        }
        return INSTANCE;
    }

    public void addUnclassifiedChild(List<OntologyHierarchy> children) {
        for (OntologyHierarchy child : children){
            OntologyHierarchy unclassified = factory.getHierarchy().as();
            unclassified.setLabel(UNCLASSIFIED_LABEL);
            unclassified.setIri(child.getIri() + UNCLASSIFIED_IRI_APPEND);
            child.getSubclasses().add(unclassified);
        }
    }

    public boolean isUnclassified(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().matches(".*" + UNCLASSIFIED_IRI_APPEND + "$");
    }

    public String getUnclassifiedParentIri(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().replace(UNCLASSIFIED_IRI_APPEND,"");
    }


    public String getAttr(OntologyHierarchy hierarchy) {
        if (hierarchy.getIri().contains("operation")){
            return OPERATION_ATTR;
        }
        else{
            return TOPIC_ATTR;
        }
    }

    public Avu convertHierarchyToAvu(OntologyHierarchy hierarchy) {
        Avu avu = avuFactory.getAvu().as();
        avu.setAttribute(getAttr(hierarchy));
        avu.setValue(hierarchy.getIri());
        avu.setUnit("");

        List<Avu> avus = Lists.newArrayList();
        Avu subAvu = avuFactory.getAvu().as();
        subAvu.setAttribute(LABEL_ATTR);
        subAvu.setValue(hierarchy.getLabel());
        subAvu.setUnit(LABEL_UNIT);
        avus.add(subAvu);

        avu.setAvus(avus);
        return avu;
    }

    public AvuList convertHierarchiesToAvus(OntologyHierarchy hierarchy) {
        List<OntologyHierarchy> hierarchyList = Lists.newArrayList();
        hierarchyList.add(hierarchy);
        return convertHierarchiesToAvus(hierarchyList);
    }

    public AvuList convertHierarchiesToAvus(List<OntologyHierarchy> selectedHierarchies) {
        AvuList avuListBean = avuFactory.getAvuList().as();
        List<Avu> avuList = Lists.newArrayList();
        for (OntologyHierarchy hierarchy : selectedHierarchies) {
            Avu avu = convertHierarchyToAvu(hierarchy);
            avuList.add(avu);
        }
        avuListBean.setAvus(avuList);
        return avuListBean;
    }

}
