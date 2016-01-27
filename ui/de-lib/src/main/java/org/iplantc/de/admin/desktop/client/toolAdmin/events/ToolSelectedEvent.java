package org.iplantc.de.admin.desktop.client.toolAdmin.events;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ToolSelectedEvent extends GwtEvent<ToolSelectedEvent.ToolSelectedEventHandler> {
    public static Type<ToolSelectedEventHandler> TYPE = new Type<>();
    private Tool tool;

    public ToolSelectedEvent(Tool tool) {
        this.tool = tool;
    }

    public Type<ToolSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ToolSelectedEventHandler handler) {
        handler.onToolSelected(this);
    }

    public Tool getTool() {
        return tool;
    }

    public interface ToolSelectedEventHandler extends EventHandler {
        void onToolSelected(ToolSelectedEvent event);
    }

    public interface HasToolSelectedEventHandlers {
        HandlerRegistration addToolSelectedEventHandler(ToolSelectedEventHandler handler);
    }
}
