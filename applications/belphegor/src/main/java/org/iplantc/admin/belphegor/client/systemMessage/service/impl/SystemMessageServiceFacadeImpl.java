package org.iplantc.admin.belphegor.client.systemMessage.service.impl;

import org.iplantc.admin.belphegor.client.models.ToolIntegrationAdminProperties;
import org.iplantc.admin.belphegor.client.services.ToolIntegrationAdminServiceFacade;
import org.iplantc.admin.belphegor.client.systemMessage.service.SystemMessageServiceFacade;
import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.client.models.systemMessages.SystemMessageFactory;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class SystemMessageServiceFacadeImpl implements SystemMessageServiceFacade {

    private final SystemMessageFactory factory;

    @Inject
    public SystemMessageServiceFacadeImpl(SystemMessageFactory factory) {
        this.factory = factory;
    }

    @Override
    public void getSystemMessages(AsyncCallback<List<SystemMessage>> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAdminSystemMessageServiceUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new SystemMessageListCallbackConverter(callback, factory));
    }

    @Override
    public void addSystemMessage(SystemMessage msgToAdd, AsyncCallback<SystemMessage> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAdminSystemMessageServiceUrl();
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgToAdd));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.PUT, address, encode.getPayload());
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void updateSystemMessage(SystemMessage updatedMsg, AsyncCallback<SystemMessage> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAdminSystemMessageServiceUrl() + "/" + updatedMsg.getId();
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(updatedMsg));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.POST, address, encode.getPayload());
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void deleteSystemMessage(SystemMessage msgToDelete, AsyncCallback<Void> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAdminSystemMessageServiceUrl() + "/" + msgToDelete.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.DELETE, address);
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getSystemMessageTypes(AsyncCallback<List<String>> callback) {
        String address = ToolIntegrationAdminProperties.getInstance().getAdminSystemMessageTypesUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(ServiceCallWrapper.Type.GET, address);
        ToolIntegrationAdminServiceFacade.getInstance().getServiceData(wrapper, new SystemMessageTypeListCallbackConverter(callback, factory));
    }

}
