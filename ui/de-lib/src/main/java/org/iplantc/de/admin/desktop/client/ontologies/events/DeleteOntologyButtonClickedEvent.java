package org.iplantc.de.admin.desktop.client.ontologies.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class DeleteOntologyButtonClickedEvent
        extends GwtEvent<DeleteOntologyButtonClickedEvent.DeleteOntologyButtonClickedEventHandler> {
    public static interface DeleteOntologyButtonClickedEventHandler extends EventHandler {
        void onDeleteOntologyButtonClicked(DeleteOntologyButtonClickedEvent event);
    }

    public interface HasDeleteOntologyButtonClickedEventHandlers {
        HandlerRegistration addDeleteOntologyButtonClickedEventHandler(DeleteOntologyButtonClickedEventHandler handler);
    }
    public static Type<DeleteOntologyButtonClickedEventHandler> TYPE =
            new Type<DeleteOntologyButtonClickedEventHandler>();

    private String ontologyVersion;

    public DeleteOntologyButtonClickedEvent(String ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    public Type<DeleteOntologyButtonClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteOntologyButtonClickedEventHandler handler) {
        handler.onDeleteOntologyButtonClicked(this);
    }

    public String getOntologyVersion() {
        return ontologyVersion;
    }
}
