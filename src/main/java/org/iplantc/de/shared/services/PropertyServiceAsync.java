package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Map;

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
    void getProperties(AsyncCallback<Map<String, String>> callback);
}
