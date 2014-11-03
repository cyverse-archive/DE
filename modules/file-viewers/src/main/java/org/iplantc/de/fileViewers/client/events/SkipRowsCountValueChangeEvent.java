package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class SkipRowsCountValueChangeEvent extends GwtEvent<SkipRowsCountValueChangeEvent.SkipRowsCountValueChangeEventHandler> {
    public static interface SkipRowsCountValueChangeEventHandler extends EventHandler {
        void onSkipRowsCountValueChange(SkipRowsCountValueChangeEvent event);
    }

    public static Type<SkipRowsCountValueChangeEventHandler> TYPE = new Type<>();
    private final int value;

    public SkipRowsCountValueChangeEvent(int value) {
        this.value = value;
    }

    public Type<SkipRowsCountValueChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getValue() {
        return value;
    }

    protected void dispatch(SkipRowsCountValueChangeEventHandler handler) {
        handler.onSkipRowsCountValueChange(this);
    }
}
