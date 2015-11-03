package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class DirtyStateChangedEvent extends GwtEvent<DirtyStateChangedEvent.DirtyStateChangedEventHandler> {
    public static interface DirtyStateChangedEventHandler extends EventHandler {
        void onEditorDirtyStateChanged(DirtyStateChangedEvent event);
    }

    public static interface HasDirtyStateChangedEventHandlers {
        HandlerRegistration addDirtyStateChangedEventHandler(DirtyStateChangedEventHandler handler);
    }

    public static Type<DirtyStateChangedEventHandler> TYPE = new Type<>();
    private final boolean dirty;

    public DirtyStateChangedEvent(final boolean dirty) {
        this.dirty = dirty;
    }

    public Type<DirtyStateChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isDirty() {
        return dirty;
    }

    protected void dispatch(DirtyStateChangedEventHandler handler) {
        handler.onEditorDirtyStateChanged(this);
    }
}
