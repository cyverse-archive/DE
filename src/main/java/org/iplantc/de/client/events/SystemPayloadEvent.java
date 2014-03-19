package org.iplantc.de.client.events;

import org.iplantc.de.client.events.SystemPayloadEvent.SystemPayloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

/**
 * System payload event.
 * 
 * @author amuir
 * 
 */
public class SystemPayloadEvent extends MessagePayloadEvent<SystemPayloadEventHandler> {
    /**
     * Handler for system payload events.
     * 
     * @author amuir
     * 
     */
    public interface SystemPayloadEventHandler extends EventHandler {
        /**
         * Called when an system payload event has fired.
         * 
         * @param event fired event.
         */
        void onFire(SystemPayloadEvent event);
    }

    /**
     * Defines the GWT Event Type.
     */
    public static final GwtEvent.Type<SystemPayloadEventHandler> TYPE = new GwtEvent.Type<SystemPayloadEventHandler>();

    /**
     * Instantiate from a message and payload.
     * 
     * @param message message to be displayed as a notification.
     * @param payload additional data needed to react to this event.
     */
    public SystemPayloadEvent(JSONObject message, JSONObject payload) {
        super(message, payload);
    }

    @Override
    protected void dispatch(SystemPayloadEventHandler handler) {
        handler.onFire(this);
    }

    @Override
    public GwtEvent.Type<SystemPayloadEventHandler> getAssociatedType() {
        return TYPE;
    }
}
