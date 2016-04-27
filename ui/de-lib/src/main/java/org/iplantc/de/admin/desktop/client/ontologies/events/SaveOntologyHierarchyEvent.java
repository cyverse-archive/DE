package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.ontologies.Ontology;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class SaveOntologyHierarchyEvent extends GwtEvent<SaveOntologyHierarchyEvent.SaveOntologyHierarchyEventHandler> {

    public static interface SaveOntologyHierarchyEventHandler extends EventHandler {
        void onSaveOntologyHierarchy(SaveOntologyHierarchyEvent event);
    }

    public interface HasSaveOntologyHierarchyEventHandlers {
        HandlerRegistration addSaveOntologyHierarchyEventHandler(SaveOntologyHierarchyEventHandler handler);
    }

    public static Type<SaveOntologyHierarchyEventHandler> TYPE =
            new Type<SaveOntologyHierarchyEventHandler>();

    public Type<SaveOntologyHierarchyEventHandler> getAssociatedType() {
        return TYPE;
    }

    private Ontology ontology;
    public SaveOntologyHierarchyEvent(Ontology ontology) {
        this.ontology = ontology;
    }

    protected void dispatch(SaveOntologyHierarchyEventHandler handler) {
        handler.onSaveOntologyHierarchy(this);
    }

    public Ontology getOntology() {
        return ontology;
    }

}
