package org.iplantc.admin.belphegor.client.services;

import org.iplantc.de.shared.AsyncCallbackWrapper;
import org.iplantc.de.shared.services.DEService;
import org.iplantc.de.shared.services.DEServiceAsync;
import org.iplantc.de.shared.services.MultiPartServiceWrapper;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * A singleton service that provides an asynchronous proxy to data services.
 */
public class ToolIntegrationAdminServiceFacade implements DEServiceAsync {
    public static final String DE_SERVICE = "deservice"; //$NON-NLS-1$

    private static ToolIntegrationAdminServiceFacade srvFacade;
    private DEServiceAsync proxy;

    private ToolIntegrationAdminServiceFacade() {
        proxy = GWT.create(DEService.class);

        ((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + DE_SERVICE);
    }

    /**
     * Retrieve singleton instance.
     *
     * @return the singleton instance.
     */
    public static ToolIntegrationAdminServiceFacade getInstance() {
        if (srvFacade == null) {
            srvFacade = new ToolIntegrationAdminServiceFacade();
        }

        return srvFacade;
    }

    /**
     * Perform service call with a populated wrapper.
     *
     * @param wrapper the service call configuration object.
     * @param callback the callback for when the RPC call finishes.
     * @throws SerializationException
     */
    @Override
    public Request getServiceData(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        return proxy.getServiceData(wrapper, new AsyncCallbackWrapper<>(callback));
    }

    /**
     * Perform service call with a populated multi-part wrapper.
     *
     * @param wrapper the service call configuration object
     * @param callback the callback for when the RPC call finishes.
     * @throws SerializationException
     */
    @Override
    public Request getServiceData(MultiPartServiceWrapper wrapper, AsyncCallback<String> callback) {
        return proxy.getServiceData(wrapper, new AsyncCallbackWrapper<>(callback));
    }
}
