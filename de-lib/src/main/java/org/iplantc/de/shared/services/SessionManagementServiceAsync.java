package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.Map;

/**
 * Provides an asynchronous service contract for the SessionManagementService.
 * 
 * @see org.iplantc.de.client.services.SessionManagementService
 * @author sriram
 */
public interface SessionManagementServiceAsync {
    /**
     * Retrieve attributes.
     * 
     * @param callback executed when RPC call completes.
     */
    void getAttributes(AsyncCallback<Map<String, String>> callback);

    /**
     * Retrieve individual attribute.
     * 
     * @param key attribute key.
     * @param callback executed when RPC call completes.
     */
    void getAttribute(String key, AsyncCallback<String> callback);

    /**
     * Set an individual attribute.
     * 
     * @param key attribute key.
     * @param value value
     * @param callback executed when RPC call completes.
     */
    void setAttribute(String key, String value, AsyncCallback<Void> callback);

    /**
     * Remove an attribute.
     * 
     * @param key attribute key.
     * @param callback executed when RPC call completes.
     */
    void removeAttribute(String key, AsyncCallback<Void> callback);

    /**
     * Invalidate session.
     * 
     * @param callback executed when RPC call completes.
     */
    void invalidate(AsyncCallback<Void> callback);
}
