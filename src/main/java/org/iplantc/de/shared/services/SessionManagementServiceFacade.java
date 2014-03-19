package org.iplantc.de.shared.services;

import org.iplantc.de.shared.AsyncCallbackWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.Map;

/**
 * Provides a singleton service facade for Session Management.
 * 
 * @author sriram
 */
public class SessionManagementServiceFacade {
    private static final String SESSION_SERVICE = "sessionmanagement";

    private static SessionManagementServiceFacade sessionMgmt = null;

    private SessionManagementServiceAsync proxy;

    private SessionManagementServiceFacade() {
        proxy = (SessionManagementServiceAsync)GWT.create(SessionManagementService.class);
        ((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + SESSION_SERVICE);
    }

    /**
     * Retrieve singleton instance.
     * 
     * @return singleton instance.
     */
    public static SessionManagementServiceFacade getInstance() {
        if (sessionMgmt == null) {
            sessionMgmt = new SessionManagementServiceFacade();
        }

        return sessionMgmt;
    }

    /**
     * 
     * @param callback executed when RPC call completes.
     */
    public void getAttributes(AsyncCallback<Map<String, String>> callback) {
        proxy.getAttributes(new AsyncCallbackWrapper<Map<String, String>>(callback));
    }

    /**
     * Retrieve individual attribute.
     * 
     * @param key attribute key.
     * @param callback executed when RPC call completes.
     */
    public void getAttribute(String key, AsyncCallback<String> callback) {
        proxy.getAttribute(key, new AsyncCallbackWrapper<String>(callback));
    }

    /**
     * Set individual attribute.
     * 
     * @param key attribute key.
     * @param value new attribute value.
     * @param callback executed when RPC call completes.
     */
    public void setAttribute(String key, String value, AsyncCallback<Void> callback) {
        proxy.setAttribute(key, value, new AsyncCallbackWrapper<Void>(callback));
    }

    /**
     * Remove an attribute.
     * 
     * @param key attribute key.
     * @param callback executed when RPC call completes.
     */
    public void removeAttribute(String key, AsyncCallback<Void> callback) {
        proxy.removeAttribute(key, new AsyncCallbackWrapper<Void>(callback));
    }

    /**
     * Invalidate session.
     * 
     * @param callback executed when RPC call completes.
     */
    public void invalidate(AsyncCallback<Void> callback) {
        proxy.invalidate(new AsyncCallbackWrapper<Void>(callback));
    }
}
