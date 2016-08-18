package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import java.util.List;

/**
 * @author aramsey
 */
public class PreviewHierarchySelectedEvent extends GwtEvent<PreviewHierarchySelectedEvent.PreviewHierarchySelectedEventHandler> {
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    public static interface PreviewHierarchySelectedEventHandler extends EventHandler {
        void onPreviewHierarchySelected(PreviewHierarchySelectedEvent event);
    }

    public interface HasPreviewHierarchySelectedEventHandlers {
        HandlerRegistration addPreviewHierarchySelectedEventHandler(PreviewHierarchySelectedEventHandler handler);
    }

    public static Type<PreviewHierarchySelectedEventHandler> TYPE = new Type<PreviewHierarchySelectedEventHandler>();

    private OntologyHierarchy hierarchy;
    private Ontology editedOntology;
    OntologiesView.TreeType treeType;

    public PreviewHierarchySelectedEvent(OntologyHierarchy hierarchy,
                                         Ontology editedOntology, OntologiesView.TreeType treeType){
        this.hierarchy = hierarchy;
        this.editedOntology = editedOntology;
        this.treeType = treeType;
    }

    public OntologyHierarchy getHierarchy() {
        return hierarchy;
    }
    public Ontology getEditedOntology() {
        return editedOntology;
    }

    public List<String> getPath() {
        AutoBean<OntologyHierarchy> hierarchyAutoBean = AutoBeanUtils.getAutoBean(hierarchy);
        String tag = hierarchyAutoBean.getTag(HIERARCHY_MODEL_KEY);
        return Lists.newArrayList(tag.split("/"));
    }

    public OntologiesView.TreeType getTreeType() {
        return treeType;
    }

    public Type<PreviewHierarchySelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(PreviewHierarchySelectedEventHandler handler) {
        handler.onPreviewHierarchySelected(this);
    }

}
