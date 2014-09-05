package org.iplantc.admin.belphegor.client.systemMessage.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.admin.belphegor.client.models.BelphegorAdminProperties;
import org.iplantc.admin.belphegor.client.systemMessage.service.SystemMessageServiceFacade;
import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.client.models.systemMessages.SystemMessageFactory;
import org.iplantc.de.client.services.converters.StringToVoidCallbackConverter;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import java.util.List;

public class SystemMessageServiceFacadeImpl implements SystemMessageServiceFacade {

    private final SystemMessageFactory factory;
    private final DiscEnvApiService deService;
    private final BelphegorAdminProperties properties;

    @Inject
    public SystemMessageServiceFacadeImpl(SystemMessageFactory factory,
                                          final DiscEnvApiService deService,
                                          final BelphegorAdminProperties properties) {
        this.factory = factory;
        this.deService = deService;
        this.properties = properties;
    }

    @Override
    public void getSystemMessages(AsyncCallback<List<SystemMessage>> callback) {
        String address = properties.getAdminSystemMessageServiceUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new SystemMessageListCallbackConverter(callback, factory));
    }

    @Override
    public void addSystemMessage(SystemMessage msgToAdd, AsyncCallback<SystemMessage> callback) {
        String address = properties.getAdminSystemMessageServiceUrl();
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgToAdd));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, encode.getPayload());
        deService.getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void updateSystemMessage(SystemMessage updatedMsg, AsyncCallback<SystemMessage> callback) {
        String address = properties.getAdminSystemMessageServiceUrl() + "/" + updatedMsg.getId();
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(updatedMsg));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode.getPayload());
        deService.getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void deleteSystemMessage(SystemMessage msgToDelete, AsyncCallback<Void> callback) {
        String address = properties.getAdminSystemMessageServiceUrl() + "/" + msgToDelete.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getSystemMessageTypes(AsyncCallback<List<String>> callback) {
        String address = properties.getAdminSystemMessageTypesUrl();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new SystemMessageTypeListCallbackConverter(callback, factory));
    }

}
