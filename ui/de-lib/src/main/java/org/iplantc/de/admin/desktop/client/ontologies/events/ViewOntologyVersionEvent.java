package org.iplantc.de.admin.desktop.client.ontologies.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class ViewOntologyVersionEvent
        extends GwtEvent<ViewOntologyVersionEvent.ViewOntologyVersionEventHandler> {

    public static interface ViewOntologyVersionEventHandler extends EventHandler {
        void onViewOntologyVersion(ViewOntologyVersionEvent event);
    }

    public interface HasViewOntologyVersionEventHandlers {
        HandlerRegistration addViewOntologyVersionEventHandler(ViewOntologyVersionEventHandler handler);
    }

    public static Type<ViewOntologyVersionEventHandler> TYPE =
            new Type<ViewOntologyVersionEventHandler>();

    public Type<ViewOntologyVersionEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ViewOntologyVersionEventHandler handler) {
        handler.onViewOntologyVersion(this);
    }


}
