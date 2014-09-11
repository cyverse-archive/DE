package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.toolRequests.NewToolRequest;
import org.iplantc.de.client.models.toolRequests.RequestedToolDetails;
import org.iplantc.de.client.models.toolRequests.ToolRequestFactory;
import org.iplantc.de.client.services.ToolRequestServiceFacade;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

/**
 * Uses the backend services to provide the tool request services.
 * 
 * <a
 * href=
 * "https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-metadata/tool-requests.md"
 * />
 */
public final class ToolRequestServiceFacadeImpl implements ToolRequestServiceFacade {

    private final ToolRequestFactory factory;
    private final DEProperties props;
    private final DiscEnvApiService deServiceFacade;


    @Inject
    public ToolRequestServiceFacadeImpl(final ToolRequestFactory factory,
                                        final DEProperties props,
                                        final DiscEnvApiService deServiceFacade){
        this.factory = factory;
        this.props = props;
        this.deServiceFacade = deServiceFacade;
    }

    /**
     * {@link ToolRequestServiceFacade#requestInstallation(NewToolRequest, AsyncCallback)}
     */
    @Override
    public void requestInstallation(final NewToolRequest request, final AsyncCallback<RequestedToolDetails> callback) {
        final String address = props.getMuleServiceBaseUrl() + "tool-request";  //$NON-NLS-1$
        final String body = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(request)).getPayload();
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.PUT, address, body);
        final AsyncCallback<String> convCB = new AsyncCallbackConverter<String, RequestedToolDetails>(callback) {
            @Override
            protected RequestedToolDetails convertFrom(final String json) {
                return AutoBeanCodex.decode(factory, RequestedToolDetails.class, json).as();
            }};
        deServiceFacade.getServiceData(wrapper, convCB);
    }

}
