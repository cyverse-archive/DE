package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class RefreshSelectedEvent extends GwtEvent<RefreshSelectedEvent.RefreshSelectedEventHandler> {
    public static interface RefreshSelectedEventHandler extends EventHandler {
        void onRefreshSelected(RefreshSelectedEvent event);
    }

    public static Type<RefreshSelectedEventHandler> TYPE = new Type<>();

    public Type<RefreshSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RefreshSelectedEventHandler handler) {
        handler.onRefreshSelected(this);
    }
}
