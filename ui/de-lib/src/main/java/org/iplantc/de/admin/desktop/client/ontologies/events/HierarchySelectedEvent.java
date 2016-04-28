package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class HierarchySelectedEvent extends GwtEvent<HierarchySelectedEvent.HierarchySelectedEventHandler> {

    public static interface HierarchySelectedEventHandler extends EventHandler {
        void onHierarchySelected(HierarchySelectedEvent event);
    }

    public interface HasHierarchySelectedEventHandlers {
        HandlerRegistration addHierarchySelectedEventHandler(HierarchySelectedEventHandler handler);
    }

    public static Type<HierarchySelectedEventHandler> TYPE = new Type<HierarchySelectedEventHandler>();

    private OntologyHierarchy hierarchy;

    public HierarchySelectedEvent(OntologyHierarchy hierarchy){
        this.hierarchy = hierarchy;
    }

    public OntologyHierarchy getHierarchy() {
        return hierarchy;
    }

    public Type<HierarchySelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(HierarchySelectedEventHandler handler) {
        handler.onHierarchySelected(this);
    }

}
