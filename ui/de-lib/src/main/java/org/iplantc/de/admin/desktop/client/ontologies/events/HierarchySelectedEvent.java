package org.iplantc.de.admin.desktop.client.ontologies.events;

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
public class HierarchySelectedEvent extends GwtEvent<HierarchySelectedEvent.HierarchySelectedEventHandler> {
    private static final String HIERARCHY_MODEL_KEY = "model_key";

    public static interface HierarchySelectedEventHandler extends EventHandler {
        void onHierarchySelected(HierarchySelectedEvent event);
    }

    public interface HasHierarchySelectedEventHandlers {
        HandlerRegistration addHierarchySelectedEventHandler(HierarchySelectedEventHandler handler);
    }

    public static Type<HierarchySelectedEventHandler> TYPE = new Type<HierarchySelectedEventHandler>();

    private OntologyHierarchy hierarchy;
    private Ontology editedOntology;

    public HierarchySelectedEvent(OntologyHierarchy hierarchy, Ontology editedOntology){
        this.hierarchy = hierarchy;
        this.editedOntology = editedOntology;
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

    public Type<HierarchySelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(HierarchySelectedEventHandler handler) {
        handler.onHierarchySelected(this);
    }

}
