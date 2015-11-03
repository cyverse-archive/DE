package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class WrapTextCheckboxChangeEvent extends GwtEvent<WrapTextCheckboxChangeEvent.WrapTextCheckboxChangeEventHandler> {
    public static interface WrapTextCheckboxChangeEventHandler extends EventHandler {
        void onWrapTextCheckboxChange(WrapTextCheckboxChangeEvent event);
    }

    public static Type<WrapTextCheckboxChangeEventHandler> TYPE = new Type<>();
    private final boolean value;

    public WrapTextCheckboxChangeEvent(boolean value) {
        this.value = value;
    }

    public Type<WrapTextCheckboxChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean getValue() {
        return value;
    }

    protected void dispatch(WrapTextCheckboxChangeEventHandler handler) {
        handler.onWrapTextCheckboxChange(this);
    }
}
