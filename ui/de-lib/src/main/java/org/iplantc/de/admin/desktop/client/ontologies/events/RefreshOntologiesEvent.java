package org.iplantc.de.admin.desktop.client.ontologies.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class RefreshOntologiesEvent
        extends GwtEvent<RefreshOntologiesEvent.RefreshOntologiesEventHandler> {

    public static interface RefreshOntologiesEventHandler extends EventHandler {
        void onRefreshOntologies(RefreshOntologiesEvent event);
    }

    public interface HasViewOntologyVersionEventHandlers {
        HandlerRegistration addRefreshOntologiesEventHandler(RefreshOntologiesEventHandler handler);
    }

    public static Type<RefreshOntologiesEventHandler> TYPE =
            new Type<RefreshOntologiesEventHandler>();

    public Type<RefreshOntologiesEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RefreshOntologiesEventHandler handler) {
        handler.onRefreshOntologies(this);
    }


}
