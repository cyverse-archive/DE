package org.iplantc.de.shared.services;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;

/**
 * Provides an asynchronous service contract that can be used to retrieve the set of discovery
 * environment configuration properties.
 * 
 * @author Dennis Roberts
 */
public interface PropertyServiceAsync {
    /**
     * Retrieves the entire set of configuration properties.
     * 
     * @param callback executed when RPC call completes.
     */
    Request getProperties(AsyncCallback<HashMap<String, String>> callback);
}
