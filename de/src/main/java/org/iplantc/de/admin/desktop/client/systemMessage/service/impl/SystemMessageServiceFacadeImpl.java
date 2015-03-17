package org.iplantc.de.admin.desktop.client.systemMessage.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.*;
import org.iplantc.de.admin.desktop.client.systemMessage.service.SystemMessageServiceFacade;
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

/**
 * @author jstroot
 */
public class SystemMessageServiceFacadeImpl implements SystemMessageServiceFacade {

    private final String SYSTEM_NOTIFICATION_TYPES = "org.iplantc.services.admin.notifications.system.types";
    private final String SYSTEM_NOTIFICATIONS = "org.iplantc.services.admin.notifications.system";
    @Inject private SystemMessageFactory factory;
    @Inject private DiscEnvApiService deService;

    @Inject
    public SystemMessageServiceFacadeImpl() { }

    @Override
    public void getSystemMessages(AsyncCallback<List<SystemMessage>> callback) {
        String address = SYSTEM_NOTIFICATIONS;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new SystemMessageListCallbackConverter(callback, factory));
    }

    @Override
    public void addSystemMessage(SystemMessage msgToAdd, AsyncCallback<SystemMessage> callback) {
        String address = SYSTEM_NOTIFICATIONS;
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(msgToAdd));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, encode.getPayload());
        deService.getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void updateSystemMessage(SystemMessage updatedMsg, AsyncCallback<SystemMessage> callback) {
        String address = SYSTEM_NOTIFICATIONS + "/" + updatedMsg.getId();
        final Splittable encode = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(updatedMsg));

        ServiceCallWrapper wrapper = new ServiceCallWrapper(POST, address, encode.getPayload());
        deService.getServiceData(wrapper, new SystemMessageCallbackConverter(callback, factory));
    }

    @Override
    public void deleteSystemMessage(SystemMessage msgToDelete, AsyncCallback<Void> callback) {
        String address = SYSTEM_NOTIFICATIONS + "/" + msgToDelete.getId();

        ServiceCallWrapper wrapper = new ServiceCallWrapper(DELETE, address);
        deService.getServiceData(wrapper, new StringToVoidCallbackConverter(callback));
    }

    @Override
    public void getSystemMessageTypes(AsyncCallback<List<String>> callback) {
        String address = SYSTEM_NOTIFICATION_TYPES;

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new SystemMessageTypeListCallbackConverter(callback, factory));
    }

}
