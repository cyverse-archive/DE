package org.iplantc.de.admin.desktop.client.toolAdmin.events;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class DeleteToolSelectedEvent extends GwtEvent<DeleteToolSelectedEvent.DeleteToolSelectedEventHandler> {
    public static Type<DeleteToolSelectedEventHandler> TYPE = new Type<>();
    private final Tool tool;

    public DeleteToolSelectedEvent(Tool tool) {
        this.tool = tool;
    }

    public Type<DeleteToolSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteToolSelectedEventHandler handler) {
        handler.onDeleteToolSelected(this);
    }

    public Tool getTool() {
        return tool;
    }

    public interface DeleteToolSelectedEventHandler extends EventHandler {
        void onDeleteToolSelected(DeleteToolSelectedEvent event);
    }

    public interface HasDeleteToolSelectedEventHandlers {
        HandlerRegistration addDeleteToolSelectedEventHandler(DeleteToolSelectedEventHandler handler);
    }
}
