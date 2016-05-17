package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologyHierarchySelectionChangedEvent
        extends GwtEvent<OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler> {
    public static interface OntologyHierarchySelectionChangedEventHandler extends EventHandler {
        void onOntologyHierarchySelectionChanged(OntologyHierarchySelectionChangedEvent event);
    }

    public interface HasOntologyHierarchySelectionChangedEventHandlers {
        HandlerRegistration addOntologyHierarchySelectionChangedEventHandler(
                OntologyHierarchySelectionChangedEventHandler handler);
    }
    public static Type<OntologyHierarchySelectionChangedEventHandler> TYPE =
            new Type<OntologyHierarchySelectionChangedEventHandler>();
    private OntologyHierarchy selectedHierarchy;
    private List<String> path;

    public OntologyHierarchySelectionChangedEvent(OntologyHierarchy selectedHierarchy,
                                                  List<String> path) {
        this.selectedHierarchy = selectedHierarchy;
        this.path = path;
    }

    public Type<OntologyHierarchySelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(OntologyHierarchySelectionChangedEventHandler handler) {
        handler.onOntologyHierarchySelectionChanged(this);
    }

    public OntologyHierarchy getSelectedHierarchy() {
        return selectedHierarchy;
    }

    public List<String> getPath() {
        return path;
    }
}
