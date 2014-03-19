package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.CreateNewAppEvent.CreateNewAppEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CreateNewAppEvent extends GwtEvent<CreateNewAppEventHandler> {

    public interface CreateNewAppEventHandler extends EventHandler {

        /**
         * Fired when a user wants to create a new app.
         * 
         * @param event
         */
        void createNewApp(CreateNewAppEvent event);

    }

    public static final GwtEvent.Type<CreateNewAppEventHandler> TYPE = new GwtEvent.Type<CreateNewAppEventHandler>();

    @Override
    public GwtEvent.Type<CreateNewAppEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateNewAppEventHandler handler) {
        handler.createNewApp(this);
    }

}
