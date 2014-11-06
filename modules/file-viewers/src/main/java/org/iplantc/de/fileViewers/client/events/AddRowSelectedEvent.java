package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class AddRowSelectedEvent extends GwtEvent<AddRowSelectedEvent.AddRowSelectedEventHandler> {
    public static interface AddRowSelectedEventHandler extends EventHandler {
        void onAddRowSelected(AddRowSelectedEvent event);
    }

    public static Type<AddRowSelectedEventHandler> TYPE = new Type<>();

    public Type<AddRowSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddRowSelectedEventHandler handler) {
        handler.onAddRowSelected(this);
    }
}
