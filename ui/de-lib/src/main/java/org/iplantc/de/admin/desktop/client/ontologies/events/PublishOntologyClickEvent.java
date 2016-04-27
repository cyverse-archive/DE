package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.ontologies.Ontology;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class PublishOntologyClickEvent extends GwtEvent<PublishOntologyClickEvent.PublishOntologyClickEventHandler> {

    public static interface PublishOntologyClickEventHandler extends EventHandler {
        void onPublishOntologyClick(PublishOntologyClickEvent event);
    }

    public interface HasPublishOntologyClickEventHandlers {
        HandlerRegistration addPublishOntologyClickEventHandler(PublishOntologyClickEventHandler handler);
    }

    public static Type<PublishOntologyClickEventHandler> TYPE =
            new Type<PublishOntologyClickEventHandler>();

    public Type<PublishOntologyClickEventHandler> getAssociatedType() {
        return TYPE;
    }

    private Ontology ontology;

    public PublishOntologyClickEvent(Ontology newActiveOntology) {
        this.ontology = newActiveOntology;
    }

    public Ontology getNewActiveOntology() {
        return ontology;
    }

    protected void dispatch(PublishOntologyClickEventHandler handler) {
        handler.onPublishOntologyClick(this);
    }

}
