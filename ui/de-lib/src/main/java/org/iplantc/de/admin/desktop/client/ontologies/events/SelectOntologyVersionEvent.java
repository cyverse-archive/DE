package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.ontologies.Ontology;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class SelectOntologyVersionEvent extends GwtEvent<SelectOntologyVersionEvent.SelectOntologyVersionEventHandler> {

    public static interface SelectOntologyVersionEventHandler extends EventHandler {
        void onSelectOntologyVersion(SelectOntologyVersionEvent event);
    }

    public interface HasSelectOntologyVersionEventHandlers {
        HandlerRegistration addSelectOntologyVersionEventHandler(SelectOntologyVersionEventHandler handler);
    }

    public static Type<SelectOntologyVersionEventHandler> TYPE =
            new Type<SelectOntologyVersionEventHandler>();

    public Type<SelectOntologyVersionEventHandler> getAssociatedType() {
        return TYPE;
    }
    private Ontology selectedOntology;

    public SelectOntologyVersionEvent(Ontology ontology){
        this.selectedOntology = ontology;
    }

    public Ontology getSelectedOntology() {
        return selectedOntology;
    }

    protected void dispatch(SelectOntologyVersionEventHandler handler) {
        handler.onSelectOntologyVersion(this);
    }
}
