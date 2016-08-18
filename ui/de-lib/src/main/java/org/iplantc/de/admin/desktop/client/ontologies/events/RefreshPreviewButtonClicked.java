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
public class RefreshPreviewButtonClicked
        extends GwtEvent<RefreshPreviewButtonClicked.RefreshPreviewButtonClickedHandler> {

    public static interface RefreshPreviewButtonClickedHandler extends EventHandler {
        void onRefreshPreviewButtonClicked(RefreshPreviewButtonClicked event);
    }

    public interface HasRefreshPreviewButtonClickedHandlers {
        HandlerRegistration addRefreshPreviewButtonClickedHandler(RefreshPreviewButtonClickedHandler handler);
    }
    public static Type<RefreshPreviewButtonClickedHandler> TYPE =
            new Type<RefreshPreviewButtonClickedHandler>();

    private Ontology editedOntology;
    private List<OntologyHierarchy> roots;

    public RefreshPreviewButtonClicked(Ontology editedOntology, List<OntologyHierarchy> roots) {
        this.editedOntology = editedOntology;
        this.roots = roots;
    }

    public Type<RefreshPreviewButtonClickedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RefreshPreviewButtonClickedHandler handler) {
        handler.onRefreshPreviewButtonClicked(this);
    }

    public Ontology getEditedOntology() {
        return editedOntology;
    }

    public List<OntologyHierarchy> getRoots() {
        return roots;
    }
}
