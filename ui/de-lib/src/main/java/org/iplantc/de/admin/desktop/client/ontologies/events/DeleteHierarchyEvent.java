package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author aramsey
 */
public class DeleteHierarchyEvent extends GwtEvent<DeleteHierarchyEvent.DeleteHierarchyEventHandler> {
    public static interface DeleteHierarchyEventHandler extends EventHandler {
        void onDeleteHierarchy(DeleteHierarchyEvent event);
    }

    public interface HasDeleteHierarchyEventHandlers {
        HandlerRegistration addDeleteHierarchyEventHandler(DeleteHierarchyEventHandler handler);
    }

    public static Type<DeleteHierarchyEventHandler> TYPE = new Type<DeleteHierarchyEventHandler>();

    private Ontology editedOntology;
    private List<OntologyHierarchy> deletedHierarchies;

    public DeleteHierarchyEvent(Ontology editedOntology, List<OntologyHierarchy> deletedHierarchies) {
        this.editedOntology = editedOntology;
        this.deletedHierarchies = deletedHierarchies;
    }

    public Type<DeleteHierarchyEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteHierarchyEventHandler handler) {
        handler.onDeleteHierarchy(this);
    }

    public Ontology getEditedOntology() {
        return editedOntology;
    }

    public List<OntologyHierarchy> getDeletedHierarchies() {
        return deletedHierarchies;
    }
}
