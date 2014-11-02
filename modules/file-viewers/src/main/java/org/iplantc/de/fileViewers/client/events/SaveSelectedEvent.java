package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class SaveSelectedEvent extends GwtEvent<SaveSelectedEvent.SaveSelectedEventHandler> {
    public static interface SaveSelectedEventHandler extends EventHandler {
        void onSaveSelected(SaveSelectedEvent event);
    }

    public static Type<SaveSelectedEventHandler> TYPE = new Type<>();

    public Type<SaveSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SaveSelectedEventHandler handler) {
        handler.onSaveSelected(this);
    }
}
