package org.iplantc.de.client.events;

import org.iplantc.de.client.events.AnalysisPayloadEvent.AnalysisPayloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

/**
 * Analysis payload event.
 * 
 * @author amuir
 * 
 */
public class AnalysisPayloadEvent extends MessagePayloadEvent<AnalysisPayloadEventHandler> {

    public interface AnalysisPayloadEventHandler extends EventHandler {
        /**
         * Called when an analysis payload event has fired.
         * 
         * @param event fired event.
         */
        void onFire(AnalysisPayloadEvent event);
    }
    /**
     * Defines the GWT Event Type.
     */
    public static final GwtEvent.Type<AnalysisPayloadEventHandler> TYPE = new GwtEvent.Type<AnalysisPayloadEventHandler>();

    /**
     * Instant
     * 
     * @param message
     * @param payload
     */
    public AnalysisPayloadEvent(JSONObject message, JSONObject payload) {
        super(message, payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void dispatch(AnalysisPayloadEventHandler handler) {
        handler.onFire(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GwtEvent.Type<AnalysisPayloadEventHandler> getAssociatedType() {
        return TYPE;
    }
}
