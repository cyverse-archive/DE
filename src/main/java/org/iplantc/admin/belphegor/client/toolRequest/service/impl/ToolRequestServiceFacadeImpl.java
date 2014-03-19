package org.iplantc.admin.belphegor.client.toolRequest.service.impl;

import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.admin.belphegor.client.services.ToolIntegrationAdminServiceFacade;
import org.iplantc.admin.belphegor.client.toolRequest.service.ToolRequestServiceFacade;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestAdminAutoBeanFactory;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.SortInfo;

import java.util.List;

public class ToolRequestServiceFacadeImpl implements ToolRequestServiceFacade {

    private final ToolRequestAdminAutoBeanFactory factory;

    @Inject
    public ToolRequestServiceFacadeImpl(ToolRequestAdminAutoBeanFactory factory) {
        this.factory = factory;
    }

    @Override
    public void getToolRequestDetails(HasId toolRequest, AsyncCallback<ToolRequestDetails> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getToolRequestServiceUrl() + "/" + toolRequest.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        callService(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void updateToolRequest(ToolRequestUpdate trUpdate, AsyncCallback<ToolRequestDetails> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getToolRequestServiceUrl();
        trUpdate.setUserName(UserInfo.getInstance().getUsername());

        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(trUpdate));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, encode.getPayload());
        callService(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void getToolRequests(SortInfo sortInfo, String userName, AsyncCallback<List<ToolRequest>> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getListToolRequestsServiceUrl();
        // TODO Pending rest of endpoint setup. Waiting for generic get all tool requests endpoint.

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        callService(wrapper, new ToolRequestListCallbackConverter(callback, factory));
    }

    /**
     * Performs the actual service call, masking any calling component.
     * 
     * @param callback executed when RPC call completes.
     * @param wrapper the wrapper used to get to the actual service via the service proxy.
     */
    private void callService(ServiceCallWrapper wrapper, AsyncCallback<String> callback) {
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, callback);
    }

}
