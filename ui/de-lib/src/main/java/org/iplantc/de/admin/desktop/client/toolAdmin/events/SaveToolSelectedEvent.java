package org.iplantc.de.admin.desktop.client.toolAdmin.events;

import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class SaveToolSelectedEvent extends GwtEvent<SaveToolSelectedEvent.SaveToolSelectedEventHandler> {
    public static Type<SaveToolSelectedEventHandler> TYPE = new Type<>();
    private Tool tool;

    public SaveToolSelectedEvent(Tool tool) {
        this.tool = tool;
    }

    public Type<SaveToolSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SaveToolSelectedEventHandler handler) {
        handler.onSaveToolSelected(this);
    }

    public Tool getTool() {
        return tool;
    }

    public interface SaveToolSelectedEventHandler extends EventHandler {
        void onSaveToolSelected(SaveToolSelectedEvent event);
    }

    public interface HasSaveToolSelectedEventHandlers {
        HandlerRegistration addSaveToolSelectedEventHandler(SaveToolSelectedEventHandler handler);
    }
}
