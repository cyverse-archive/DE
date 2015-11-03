package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class HeaderRowCheckboxChangedEvent extends GwtEvent<HeaderRowCheckboxChangedEvent.HeaderRowCheckboxChangedEventHandler> {
    public static interface HeaderRowCheckboxChangedEventHandler extends EventHandler {
        void onSkipRowsCheckboxChanged(HeaderRowCheckboxChangedEvent event);
    }

    public static Type<HeaderRowCheckboxChangedEventHandler> TYPE = new Type<>();
    private final boolean value;

    public HeaderRowCheckboxChangedEvent(boolean value) {

        this.value = value;
    }

    public Type<HeaderRowCheckboxChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean getValue() {
        return value;
    }

    protected void dispatch(HeaderRowCheckboxChangedEventHandler handler) {
        handler.onSkipRowsCheckboxChanged(this);
    }
}
