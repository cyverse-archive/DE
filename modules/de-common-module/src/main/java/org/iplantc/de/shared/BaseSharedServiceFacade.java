package org.iplantc.de.shared;

import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Base class for a singleton service that provides an asynchronous proxy to data services.
 */
public class BaseSharedServiceFacade {
    private DEServiceAsync proxy;

    /**
     * Creates a new unsecured service facade.
     */
    protected BaseSharedServiceFacade(String deService) {
        proxy = (DEServiceAsync)GWT.create(DEService.class);

        String baseUrl = GWT.getModuleBaseURL();
        ((ServiceDefTarget)proxy).setServiceEntryPoint(baseUrl + deService);
    }

    /**
     * Gets the response to the HTTP request represented by the given service call wrapper.
     * 
     * @param wrapper the service call wrapper.
     * @param callback the callback to use to notify the caller of the results.
     */
    public void getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        proxy.getServiceData(wrapper, callback);
    }

    /**
     * Gets the response to the multipart HTTP request represented by the given service call wrapper.
     * 
     * @param wrapper the service call wrapper.
     * @param callback the callback to use to notify the caller of the results.
     */
    public void getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback) {
        proxy.getServiceData(wrapper, callback);
    }
}
