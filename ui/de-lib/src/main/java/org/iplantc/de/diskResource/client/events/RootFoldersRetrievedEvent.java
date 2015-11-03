package org.iplantc.de.diskResource.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class RootFoldersRetrievedEvent extends GwtEvent<RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler> {
    public static interface RootFoldersRetrievedEventHandler extends EventHandler {
        void onRootFoldersRetrieved(RootFoldersRetrievedEvent event);
    }

    public static interface HasRootFoldersRetrievedEventHandlers{
        HandlerRegistration addRootFoldersRetrievedEventHandler(RootFoldersRetrievedEventHandler handler);
    }

    public static final Type<RootFoldersRetrievedEventHandler> TYPE = new Type<>();

    public Type<RootFoldersRetrievedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RootFoldersRetrievedEventHandler handler) {
        handler.onRootFoldersRetrieved(this);
    }
}
