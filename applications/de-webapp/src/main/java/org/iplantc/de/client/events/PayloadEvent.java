package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

/**
 * Abstract payload event.
 * 
 * @author amuir
 * 
 * @param <H> A subclass of EventHandler.
 */
public abstract class PayloadEvent<H extends EventHandler> extends GwtEvent<H> {
    private final JSONObject payload;

    /**
     * Instantiate from payload.
     * 
     * @param payload additional data needed to react to this event.
     */
    public PayloadEvent(final JSONObject payload) {
        this.payload = payload;
    }

    /**
     * Retrieve event payload.
     * 
     * @return event's payload.
     */
    public JSONObject getPayload() {
        return payload;
    }
}
