package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class AddPathsSelectedEvent extends GwtEvent<AddPathsSelectedEvent.AddPathsSelectedEventHandler> {
    public static interface AddPathsSelectedEventHandler extends EventHandler {
        void onAddPathsSelected(AddPathsSelectedEvent event);
    }

    public static Type<AddPathsSelectedEventHandler> TYPE = new Type<>();

    public Type<AddPathsSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddPathsSelectedEventHandler handler) {
        handler.onAddPathsSelected(this);
    }
}
