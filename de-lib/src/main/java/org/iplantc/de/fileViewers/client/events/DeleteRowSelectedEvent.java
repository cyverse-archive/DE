package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class DeleteRowSelectedEvent extends GwtEvent<DeleteRowSelectedEvent.DeleteRowSelectedEventHandler> {
    public static interface DeleteRowSelectedEventHandler extends EventHandler {
        void onDeleteRowSelected(DeleteRowSelectedEvent event);
    }

    public static Type<DeleteRowSelectedEventHandler> TYPE = new Type<>();

    public Type<DeleteRowSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteRowSelectedEventHandler handler) {
        handler.onDeleteRowSelected(this);
    }
}
