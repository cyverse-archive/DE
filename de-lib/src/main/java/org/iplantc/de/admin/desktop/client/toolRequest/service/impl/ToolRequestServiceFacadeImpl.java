package org.iplantc.de.admin.desktop.client.toolRequest.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.POST;

import org.iplantc.de.admin.desktop.client.toolRequest.service.ToolRequestServiceFacade;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestAdminAutoBeanFactory;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.SortInfo;

import java.util.List;

/**
 * @author jstroot
 */
public class ToolRequestServiceFacadeImpl implements ToolRequestServiceFacade {

    @Inject private ToolRequestAdminAutoBeanFactory factory;
    @Inject private DiscEnvApiService deService;

    @Inject
    public ToolRequestServiceFacadeImpl() { }

    @Override
    public void getToolRequestDetails(HasId toolRequest, AsyncCallback<ToolRequestDetails> callback) {
        String address = ADMIN_TOOL_REQUESTS + "/" + toolRequest.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void updateToolRequest(String id,
                                  ToolRequestUpdate trUpdate,
                                  AsyncCallback<ToolRequestDetails> callback) {
        String address = ADMIN_TOOL_REQUESTS + "/" + id + "/status";
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(trUpdate));
        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode.getPayload());
        deService.getServiceData(wrapper, new ToolRequestDetailsCallbackConverter(callback, factory));
    }

    @Override
    public void getToolRequests(SortInfo sortInfo, String userName, AsyncCallback<List<ToolRequest>> callback) {
        String address = ADMIN_TOOL_REQUESTS;
        // TODO Pending rest of endpoint setup. Waiting for generic get all tool requests endpoint.

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new ToolRequestListCallbackConverter(callback, factory));
    }

}
