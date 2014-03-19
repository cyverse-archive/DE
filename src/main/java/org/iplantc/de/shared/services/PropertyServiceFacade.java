package org.iplantc.de.shared.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.Map;

/**
 * Provides a singleton service facade for obtaining discovery environment configuration properties.
 * 
 * @author Dennis Roberts
 */
public class PropertyServiceFacade {
    /**
     * The relative URL path used to get the property information.
     */
    private final String PROPERTY_SERVICE = "properties"; //$NON-NLS-1$

    /**
     * The single instance of the property service facade.
     */
    private static PropertyServiceFacade instance = null;

    /**
     * The service proxy.
     */
    private PropertyServiceAsync proxy;

    /**
     * Initializes the service facade with a new service proxy.
     */
    private PropertyServiceFacade() {
        proxy = (PropertyServiceAsync)GWT.create(PropertyService.class);
        ((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + PROPERTY_SERVICE);
    }

    /**
     * Gets this single instance of this service facade.
     * 
     * @return the service facade instance.
     */
    public static PropertyServiceFacade getInstance() {
        if (instance == null) {
            instance = new PropertyServiceFacade();
        }
        return instance;
    }

    /**
     * Retrieves the discovery environment configuration properties from the server.
     * 
     * @param callback called when the service call completes.
     */
    public void getProperties(AsyncCallback<Map<String, String>> callback) {
        proxy.getProperties(callback);
    }
}
