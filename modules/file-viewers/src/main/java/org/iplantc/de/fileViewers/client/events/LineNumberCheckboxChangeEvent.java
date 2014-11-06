package org.iplantc.de.fileViewers.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class LineNumberCheckboxChangeEvent extends GwtEvent<LineNumberCheckboxChangeEvent.LineNumberCheckboxChangeEventHandler> {
    public static interface LineNumberCheckboxChangeEventHandler extends EventHandler {
        void onLineNumberCheckboxChange(LineNumberCheckboxChangeEvent event);
    }

    private final boolean value;

    public LineNumberCheckboxChangeEvent(boolean value){
        this.value = value;
    }
    public static Type<LineNumberCheckboxChangeEventHandler> TYPE = new Type<>();

    public Type<LineNumberCheckboxChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean getValue() {
        return value;
    }

    protected void dispatch(LineNumberCheckboxChangeEventHandler handler) {
        handler.onLineNumberCheckboxChange(this);
    }
}
