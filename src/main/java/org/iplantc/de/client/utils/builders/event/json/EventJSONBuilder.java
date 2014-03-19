package org.iplantc.de.client.utils.builders.event.json;

import com.google.gwt.json.client.JSONObject;

/**
 * Interface for event JSON builders.
 * 
 * @author amuir
 * 
 */
public interface EventJSONBuilder {
    /**
     * Interface to build a payload event from RPC success JSON.
     * 
     * @param json JSON returned from an RPC.
     * @return JSON for a new payload event.
     */
    JSONObject build(JSONObject json);
}
