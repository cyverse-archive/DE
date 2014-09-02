package org.iplantc.admin.belphegor.client.toolRequest.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.toolRequest.service.ToolRequestServiceFacade;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestAdminAutoBeanFactory;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;
import org.iplantc.de.shared.services.DEServiceAsync;
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
    private final DEServiceAsync deService;
    private final BelphegorAdminProperties properties;

    @Inject
    public ToolRequestServiceFacadeImpl(ToolRequestAdminAutoBeanFactory factory,
                                        final DEServiceAsync deService,
                                        final BelphegorAdminProperties properties) {
        this.factory = factory;
        this.deService = deService;
        this.properties = properties;
    }

    @Override
    public void getToolRequestDetails(HasId toolRequest, AsyncCallback<ToolRequestDetails> callback) {
        String address = properties.getToolRequestServiceUrl() + "/" + toolRequest.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void updateToolRequest(ToolRequestUpdate trUpdate, AsyncCallback<ToolRequestDetails> callback) {
        String address = properties.getToolRequestServiceUrl();
        trUpdate.setUserName(UserInfo.getInstance().getUsername());

        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(trUpdate));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode.getPayload());
        deService.getServiceData(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void getToolRequests(SortInfo sortInfo, String userName, AsyncCallback<List<ToolRequest>> callback) {
        String address = properties.getListToolRequestsServiceUrl();
        // TODO Pending rest of endpoint setup. Waiting for generic get all tool requests endpoint.

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ToolRequestListCallbackConverter(callback, factory));
    }

}
