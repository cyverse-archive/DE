package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class DeleteSelectedPathsSelectedEvent extends GwtEvent<DeleteSelectedPathsSelectedEvent.DeleteSelectedPathsSelectedEventHandler> {
    public static interface DeleteSelectedPathsSelectedEventHandler extends EventHandler {
        void onDeleteSelectedPathsSelected(DeleteSelectedPathsSelectedEvent event);
    }

    public static Type<DeleteSelectedPathsSelectedEventHandler> TYPE = new Type<>();

    public Type<DeleteSelectedPathsSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteSelectedPathsSelectedEventHandler handler) {
        handler.onDeleteSelectedPathsSelected(this);
    }
}
