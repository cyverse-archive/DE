package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.toolRequests.NewToolRequest;
import org.iplantc.de.client.models.toolRequests.RequestedToolDetails;
import org.iplantc.de.client.models.toolRequests.ToolRequestFactory;
import org.iplantc.de.client.services.ToolRequestServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * Uses the backend services to provide the tool request services.
 * 
 */
public final class ToolRequestServiceFacadeImpl implements ToolRequestServiceFacade {

    @Inject private ToolRequestFactory factory;
    @Inject private DiscEnvApiService deServiceFacade;

    @Inject
    public ToolRequestServiceFacadeImpl(){ }

    @Override
    public void requestInstallation(final NewToolRequest request, final AsyncCallback<RequestedToolDetails> callback) {
        final String address = TOOL_REQUESTS;
        final String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, body);
        final AsyncCallback<String> convCB = new AsyncCallbackConverter<String, RequestedToolDetails>(callback) {
            @Override
            protected RequestedToolDetails convertFrom(final String json) {
                return AutoBeanCodex.decode(factory, RequestedToolDetails.class, json).as();
            }};
        deServiceFacade.getServiceData(wrapper, convCB);
    }

}
