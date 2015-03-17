package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.services.ToolServices;
import org.iplantc.de.client.services.converters.GetDeployedComponentsCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import java.util.List;

public class ToolServicesImpl implements ToolServices {

    private final String COMPONENTS = "org.iplantc.services.apps.elements.tools";
    private final String TOOLS = "org.iplantc.services.tools";
    private final ToolAutoBeanFactory factory;
    private final DiscEnvApiService deServiceFacade;

    @Inject
    public ToolServicesImpl(final DiscEnvApiService deServiceFacade,
                                         final ToolAutoBeanFactory factory) {
        this.deServiceFacade = deServiceFacade;
        this.factory = factory;
    }

    @Override
    public void getDeployedComponents(AsyncCallback<List<Tool>> callback) {
        String address = COMPONENTS;
        GetDeployedComponentsCallbackConverter callbackCnvt = new GetDeployedComponentsCallbackConverter(callback, factory);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);

        deServiceFacade.getServiceData(wrapper, callbackCnvt);
    }

    @Override
    public void searchDeployedComponents(String searchTerm, AsyncCallback<List<Tool>> callback) {
        GetDeployedComponentsCallbackConverter callbackCnvt = new GetDeployedComponentsCallbackConverter(callback, factory);

        String address = TOOLS + "?search=" + URL.encodeQueryString(searchTerm);
        ServiceCallWrapper wrapper = new ServiceCallWrapper(address);
        deServiceFacade.getServiceData(wrapper, callbackCnvt);
    }

}
