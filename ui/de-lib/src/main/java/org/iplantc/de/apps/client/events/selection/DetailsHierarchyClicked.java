package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class DetailsHierarchyClicked
        extends GwtEvent<DetailsHierarchyClicked.DetailsHierarchyClickedHandler> {
    public static interface DetailsHierarchyClickedHandler extends EventHandler {
        void onDetailsHierarchyClicked(DetailsHierarchyClicked event);
    }

    public interface HasDetailsHierarchyClickedHandlers {
        HandlerRegistration addDetailsHierarchyClickedHandler(DetailsHierarchyClickedHandler handler);
    }
    public static Type<DetailsHierarchyClickedHandler> TYPE = new Type<DetailsHierarchyClickedHandler>();

    private OntologyHierarchy hierarchy;

    public DetailsHierarchyClicked(OntologyHierarchy hierarchy) {
        this.hierarchy = hierarchy;
    }

    public Type<DetailsHierarchyClickedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DetailsHierarchyClickedHandler handler) {
        handler.onDetailsHierarchyClicked(this);
    }

    public OntologyHierarchy getHierarchy() {
        return hierarchy;
    }
}
