package org.iplantc.de.client.util;

import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.shared.DEProperties;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONObject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.shared.FastMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
public class OntologyUtil {

    public class OntologyHierarchyNameComparator implements Comparator<OntologyHierarchy> {
        @Override
        public int compare(OntologyHierarchy o1, OntologyHierarchy o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    }

    JsonUtil jsonUtil;
    private static OntologyUtil INSTANCE;
    OntologyAutoBeanFactory factory;
    AvuAutoBeanFactory avuFactory;
    DEProperties properties;

    final String LABEL_ATTR = "rdfs:label";
    final String LABEL_UNIT = "value";

    final String UNCLASSIFIED_LABEL = "Unclassified";
    final String UNCLASSIFIED_IRI_APPEND = "_unclassified";
    Map<String, String> iriToAttrMap = new FastMap<>();

    final String BETA_ATTR;
    final String BETA_VALUE;
    final String BETA_SUB_VALUE;
    final String BETA_SUB_UNIT;

    private static final String HIERARCHY_PARENT_MODEL_KEY = "parent_key";
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    private OntologyUtil() {
        factory = GWT.create(OntologyAutoBeanFactory.class);
        avuFactory = GWT.create(AvuAutoBeanFactory.class);
        properties = DEProperties.getInstance();
        jsonUtil = JsonUtil.getInstance();

        BETA_ATTR = properties.getBetaAvuIri();
        BETA_VALUE = properties.getBetaAvuValue();
        BETA_SUB_VALUE = properties.getBetaAvuLabel();
        BETA_SUB_UNIT = properties.getBetaAvuUnit();
    }

    public static OntologyUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OntologyUtil();
        }
        return INSTANCE;
    }

    /**
     * Creates a mapping from a JSON object in the configs
     * The mapping is from a regex which should match a set of hierarchy IRIs
     * to the corresponding attribute needed for metadata for those hierarchies
     * @param result
     * @return
     */
    public boolean createIriToAttrMap(List<OntologyHierarchy> result) {
        String attrs = properties.getOntologyAttrs();
        if (attrs == null) {
            return false;
        }

        iriToAttrMap.clear();
        buildIriToAttrMap(attrs);

        return isValidIriMap(result);
    }

    public boolean isValidIriMap(List<OntologyHierarchy> hierarchies) {
        if (iriToAttrMap == null || iriToAttrMap.size() == 0) {
            return false;
        }

        for (OntologyHierarchy hierarchy : hierarchies) {
            if (Strings.isNullOrEmpty(getAttr(hierarchy))) {
                return false;
            }
        }

        return true;
    }

    public void buildIriToAttrMap(String ontologyAttr) {
        JSONObject map = jsonUtil.getObject(ontologyAttr);

        for (String key : map.keySet()) {
            String value = jsonUtil.getString(map, key);
            iriToAttrMap.put(key, value);
        }
    }

    public OntologyHierarchyNameComparator getOntologyNameComparator() {
        return new OntologyHierarchyNameComparator();
    }

    public void addUnclassifiedChild(List<OntologyHierarchy> children) {
        for (OntologyHierarchy child : children){
            addUnclassifiedChild(child);
        }
    }

    public OntologyHierarchy addUnclassifiedChild(OntologyHierarchy child) {
        OntologyHierarchy unclassified = factory.getHierarchy().as();
        unclassified.setLabel(UNCLASSIFIED_LABEL);
        unclassified.setIri(child.getIri() + UNCLASSIFIED_IRI_APPEND);
        child.getSubclasses().add(unclassified);
        return unclassified;
    }

    public boolean isUnclassified(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().matches(".*" + UNCLASSIFIED_IRI_APPEND + "$");
    }

    public String getUnclassifiedParentIri(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().replace(UNCLASSIFIED_IRI_APPEND,"");
    }


    public String getAttr(OntologyHierarchy hierarchy) {
        for (String regex : iriToAttrMap.keySet()) {
            if (hierarchy.getIri().matches(regex)){
                return iriToAttrMap.get(regex);
            }
        }
        return "";
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

    public List<String> getPathList(OntologyHierarchy hierarchy) {
        List<String> pathList = Lists.newArrayList();
        if (hierarchy != null) {
            String tag = getOrCreateHierarchyPathTag(hierarchy);
            pathList = Arrays.asList(tag.split("/"));
        }
        return pathList;
    }

    public List<List<String>> getAllPathsList(List<OntologyHierarchy> hierarchies) {
        List<List<String>> pathList = Lists.newArrayList();
        if (hierarchies == null || hierarchies.size() == 0){
            return pathList;
        }

        Collections.sort(hierarchies, new OntologyHierarchyNameComparator());
        for (OntologyHierarchy hierarchy : hierarchies) {
            pathList.add(getPathList(hierarchy));
        }
        return pathList;
    }

    /**
     * Given a hierarchy it will return that hierarchy's key.
     * The key is actually a tag which is a "/" separated string of the path from the root to this node.
     * In order to achieve this, any time a key is defined for a node, its children must get updated with
     * the parents key in a separate parent tag.
     * @param hierarchy
     * @return
     */
    public String getOrCreateHierarchyPathTag(OntologyHierarchy hierarchy) {
        if (hierarchy != null){
            final AutoBean<OntologyHierarchy> hierarchyAutoBean = getHierarchyAutoBean(hierarchy);
            String parentTag = hierarchyAutoBean.getTag(HIERARCHY_PARENT_MODEL_KEY);
            String modelTag = hierarchyAutoBean.getTag(HIERARCHY_MODEL_KEY);
            if (parentTag == null || modelTag == null) {
                modelTag = createHierarchyPathTag(hierarchy, hierarchyAutoBean, parentTag, modelTag);
                setParentTagOnChildren(modelTag, hierarchy);
            }
            return modelTag;
        }
        return "";
    }

    void setParentTagOnChildren(String parentKey, OntologyHierarchy hierarchy) {
        if (!Strings.isNullOrEmpty(parentKey) && hierarchy != null && hierarchy.getSubclasses() != null) {

            for (OntologyHierarchy sub : hierarchy.getSubclasses()) {
                final AutoBean<OntologyHierarchy> subAutoBean = getHierarchyAutoBean(sub);
                subAutoBean.setTag(HIERARCHY_PARENT_MODEL_KEY, parentKey);
            }
        }
    }

    String createHierarchyPathTag(OntologyHierarchy hierarchy,
                                          AutoBean<OntologyHierarchy> hierarchyAutoBean,
                                          String parentTag,
                                          String modelTag) {
        if (parentTag == null){
            parentTag = hierarchy.getLabel();
            modelTag = parentTag;
            hierarchyAutoBean.setTag(HIERARCHY_PARENT_MODEL_KEY, parentTag);
            hierarchyAutoBean.setTag(HIERARCHY_MODEL_KEY, parentTag);
        }
        if (modelTag == null) {
            modelTag = parentTag + "/" + hierarchy.getLabel();
            hierarchyAutoBean.setTag(HIERARCHY_MODEL_KEY, modelTag);
        }
        return modelTag;
    }

    AutoBean<OntologyHierarchy> getHierarchyAutoBean(OntologyHierarchy hierarchy) {
        return AutoBeanUtils.getAutoBean(hierarchy);
    }

    public AvuList getBetaAvuList() {
        AvuList avuListBean = avuFactory.getAvuList().as();
        List<Avu> betaAvuList = Lists.newArrayList();

        Avu betaAvu = getBetaAvu();

        betaAvuList.add(betaAvu);
        avuListBean.setAvus(betaAvuList);

        return avuListBean;
    }

    public Avu getBetaAvu() {
        Avu avu = avuFactory.getAvu().as();
        avu.setAttribute(BETA_ATTR);
        avu.setValue(BETA_VALUE);
        avu.setUnit("");

        List<Avu> subAvus = Lists.newArrayList();
        Avu subAvu = avuFactory.getAvu().as();
        subAvu.setAttribute(LABEL_ATTR);
        subAvu.setValue(BETA_SUB_VALUE);
        subAvu.setUnit(BETA_SUB_UNIT);
        subAvus.add(subAvu);

        avu.setAvus(subAvus);

        return avu;
    }

    public AvuList removeBetaAvu(List<Avu> result) {
        AvuList avuListBean = avuFactory.getAvuList().as();

        for (Avu avu : result) {
            if (avu.getAttribute().equalsIgnoreCase(BETA_ATTR)) {
                result.remove(avu);
            }
        }
        avuListBean.setAvus(result);
        return avuListBean;
    }

    public OntologyHierarchy getHierarchyObject() {
        return factory.getHierarchy().as();
    }
}
