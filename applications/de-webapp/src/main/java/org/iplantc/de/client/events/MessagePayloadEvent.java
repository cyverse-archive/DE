package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.json.client.JSONObject;

/**
 * Abstract message/payload event.
 * 
 * @author amuir
 * 
 * @param <H> A subclass of EventHandler.
 */
public abstract class MessagePayloadEvent<H extends EventHandler> extends PayloadEvent<H> {
    private final JSONObject message;

    /**
     * Instantiate from a message and payload.
     * 
     * @param message message to be displayed as a notification.
     * @param payload additional data needed to react to this event.
     */
    public MessagePayloadEvent(JSONObject message, JSONObject payload) {
        super(payload);

        this.message = message;
    }

    /**
     * Retrieve event message.
     * 
     * @return event's message.
     */
    public JSONObject getMessage() {
        return message;
    }
}
