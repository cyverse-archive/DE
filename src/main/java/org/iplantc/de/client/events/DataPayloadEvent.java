package org.iplantc.de.client.events;

import org.iplantc.de.client.events.DataPayloadEvent.DataPayloadEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.json.client.JSONObject;

public class DataPayloadEvent extends MessagePayloadEvent<DataPayloadEventHandler> {

    public interface DataPayloadEventHandler extends EventHandler {
        void onFire(DataPayloadEvent event);
    }

    /**
     * Defines the GWT Event Type.
     * 
     */
    public static final GwtEvent.Type<DataPayloadEventHandler> TYPE = new GwtEvent.Type<DataPayloadEventHandler>();

    public DataPayloadEvent(JSONObject message, JSONObject payload) {
        super(message, payload);
    }

    @Override
    protected void dispatch(DataPayloadEventHandler handler) {
        handler.onFire(this);
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<DataPayloadEventHandler> getAssociatedType() {
        return TYPE;
    }
}
