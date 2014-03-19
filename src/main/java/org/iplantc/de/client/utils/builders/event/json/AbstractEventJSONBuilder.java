package org.iplantc.de.client.utils.builders.event.json;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

/**
 * Abstract class to provide basic functionality for event JSON builders.
 * 
 * @author amuir
 * 
 */
public abstract class AbstractEventJSONBuilder implements EventJSONBuilder {
    protected final String action;

    /**
     * Instantiate from an action.
     * 
     * @param action action tag to be added to our payload.
     */
    protected AbstractEventJSONBuilder(final String action) {
        this.action = action;
    }

    /**
     * Build payload string. This wraps the current data returned from an RPC call.
     * 
     * @param action action associated with this payload.
     * @param objJson JSONObj returned from an RPC call.
     * @return JSON object for the payload.
     */
    protected JSONObject buildPayload(final JSONObject objJson) {
        // StringBuffer ret = new StringBuffer();
        //
        //        ret.append("{\"action\": " + JsonUtil.quoteString(action)); //$NON-NLS-1$
        //
        // if (objJson != null) {
        //            ret.append(", \"data\": "); //$NON-NLS-1$
        // ret.append(objJson.toString());
        // }
        //
        //        ret.append("}"); //$NON-NLS-1$
        //
        // return ret.toString();

        JSONObject ret = new JSONObject();
        ret.put("action", new JSONString(action));
        if (objJson != null) {
            ret.put("data", objJson);
        }
        return ret;
    }
}
