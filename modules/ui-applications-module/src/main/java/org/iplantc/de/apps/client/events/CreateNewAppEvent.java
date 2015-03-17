package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.CreateNewAppEvent.CreateNewAppEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * FIXME This is more of a message than an event
 * @author jstroot
 */
public class CreateNewAppEvent extends GwtEvent<CreateNewAppEventHandler> {

    public interface CreateNewAppEventHandler extends EventHandler {
        void createNewApp(CreateNewAppEvent event);
    }

    public static final GwtEvent.Type<CreateNewAppEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public GwtEvent.Type<CreateNewAppEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateNewAppEventHandler handler) {
        handler.createNewApp(this);
    }

}
