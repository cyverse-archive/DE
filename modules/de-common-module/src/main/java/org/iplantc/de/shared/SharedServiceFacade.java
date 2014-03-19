package org.iplantc.de.shared;

import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A singleton service that provides an asynchronous proxy to data services.
 */
public class SharedServiceFacade extends BaseSharedServiceFacade {
    public static final String DE_SERVICE = "deservice";

    /**
     * The single instance of this class.
     */
    private static SharedServiceFacade srvFacade;

    /**
     * Creates a new secured service facade.
     */
    private SharedServiceFacade() {
        this(DE_SERVICE);
    }

    /**
     * Creates a new secured service facade.
     * 
     * @param serviceName the name of the service.
     */
    protected SharedServiceFacade(String serviceName) {
        super(serviceName);
    }

    /**
     * Gets the single instance of the unsecured shared service facade.
     * 
     * @return the instance.
     */
    public static SharedServiceFacade getInstance() {
        if (srvFacade == null) {
            srvFacade = new SharedServiceFacade();
        }

        return srvFacade;
    }

    /**
     * Gets the response to the HTTP request represented by the given service call wrapper.
     * 
     * @param wrapper the service call wrapper.
     * @param callback the callback to use to notify the caller of the results.
     */
    @Override
    public void getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        super.getServiceData(wrapper, new AsyncCallbackWrapper<String>(callback));
    }

    /**
     * Gets the response to the multipart HTTP request represented by the given service call wrapper.
     * 
     * @param wrapper the service call wrapper.
     * @param callback the callback to use to notify the caller of the results.
     */
    @Override
    public void getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback) {
        super.getServiceData(wrapper, new AsyncCallbackWrapper<String>(callback));
    }
}
