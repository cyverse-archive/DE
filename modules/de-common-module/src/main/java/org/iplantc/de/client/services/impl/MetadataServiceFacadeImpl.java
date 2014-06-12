package org.iplantc.de.client.services.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.services.DEServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class MetadataServiceFacadeImpl implements MetadataServiceFacade {
    
    private final DEProperties deProps;
    private final DEServiceFacade deServiceFacade;

    @Inject
    public MetadataServiceFacadeImpl(final DEServiceFacade deServiceFacade, final DEProperties deProps) {
        this.deServiceFacade = deServiceFacade;
        this.deProps = deProps;
    }

    @Override
    public void createTag(String value, String description, AsyncCallback<String> callback) {
       String address = deProps.getMuleServiceBaseUrl() + "tags/user";
       JSONObject obj = new JSONObject();
       obj.put("value", new  JSONString(value));
        if (!Strings.isNullOrEmpty(description)) {
            obj.put("description", new JSONString(description));
        }
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, obj.toString());
        callService(wrapper, callback);
    }

    @Override
    public void suggestTag(String text, int limit, AsyncCallback<String> callback) {
        String address = deProps.getMuleServiceBaseUrl() + "tags/suggestions?contains=" + URL.encode(text) + "&limit=" + limit;
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        callService(wrapper, callback);
    }

    /**
     * Performs the actual service call.
     * 
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     * @param callback executed when RPC call completes.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        deServiceFacade.getServiceData(wrapper, callback);
    }

}
