package org.iplantc.de.admin.desktop.client.toolAdmin.events;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AddToolSelectedEvent extends GwtEvent<AddToolSelectedEvent.AddToolSelectedEventHandler> {

    private Tool tool;

    public AddToolSelectedEvent(Tool tool) {
        this.tool = tool;
    }

    public Tool getTool() {
        return tool;
    }

    public interface AddToolSelectedEventHandler extends EventHandler {
        void onAddToolSelected(AddToolSelectedEvent event);
    }

    public interface HasAddToolSelectedEventHandlers {
        HandlerRegistration addAddToolSelectedEventHandler(AddToolSelectedEventHandler handler);
    }

    public static Type<AddToolSelectedEventHandler> TYPE = new Type<>();

    public Type<AddToolSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddToolSelectedEventHandler handler) {
        handler.onAddToolSelected(this);
    }

}
