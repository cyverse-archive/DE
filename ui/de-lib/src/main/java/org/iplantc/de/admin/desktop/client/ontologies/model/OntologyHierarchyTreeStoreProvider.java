package org.iplantc.de.admin.desktop.client.ontologies.model;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.common.base.Strings;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public class OntologyHierarchyTreeStoreProvider implements Provider<TreeStore<OntologyHierarchy>> {

    private static final String HIERARCHY_PARENT_MODEL_KEY = "parent_key";
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    @Override
    public TreeStore<OntologyHierarchy> get() {
        return new TreeStore<>(new ModelKeyProvider<OntologyHierarchy>() {
            @Override
            public String getKey(OntologyHierarchy item) {
                String key = getHierarchyPathTag(item);
                setChildrenParentTag(key, item);
                return key;
            }
        });
    }

    void setChildrenParentTag(String key, OntologyHierarchy hierarchy) {
        if (!Strings.isNullOrEmpty(key) && hierarchy != null && hierarchy.getSubclasses() != null) {

            for (OntologyHierarchy sub : hierarchy.getSubclasses()) {
                final AutoBean<OntologyHierarchy> subAutoBean = getHierarchyAutoBean(sub);
                subAutoBean.setTag(HIERARCHY_PARENT_MODEL_KEY, key);
            }
        }
    }

    String getHierarchyPathTag(OntologyHierarchy hierarchy){
        if (hierarchy != null){
            AutoBean<OntologyHierarchy> hierarchyAutoBean = getHierarchyAutoBean(hierarchy);
            String parentTag = hierarchyAutoBean.getTag(HIERARCHY_PARENT_MODEL_KEY);
            String childTag = hierarchyAutoBean.getTag(HIERARCHY_MODEL_KEY);
            if (parentTag == null){
                parentTag = hierarchy.getLabel();
                childTag = parentTag;
                hierarchyAutoBean.setTag(HIERARCHY_PARENT_MODEL_KEY, parentTag);
                hierarchyAutoBean.setTag(HIERARCHY_MODEL_KEY, parentTag);
            }
            if (childTag == null) {
                childTag = parentTag + "/" + hierarchy.getLabel();
                hierarchyAutoBean.setTag(HIERARCHY_MODEL_KEY, childTag);
            }
            return childTag;
        }
        return "";
    }

    AutoBean<OntologyHierarchy> getHierarchyAutoBean(OntologyHierarchy hierarchy) {
        return AutoBeanUtils.getAutoBean(hierarchy);
    }
}
