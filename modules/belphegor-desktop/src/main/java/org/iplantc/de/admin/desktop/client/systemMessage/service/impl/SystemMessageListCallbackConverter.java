package org.iplantc.de.admin.desktop.client.systemMessage.service.impl;

import org.iplantc.de.client.models.systemMessages.SystemMessage;
import org.iplantc.de.client.models.systemMessages.SystemMessageFactory;
import org.iplantc.de.client.models.systemMessages.SystemMessageList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class SystemMessageListCallbackConverter extends AsyncCallbackConverter<String, List<SystemMessage>> {

    private final SystemMessageFactory factory;

    public SystemMessageListCallbackConverter(AsyncCallback<List<SystemMessage>> callback, SystemMessageFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<SystemMessage> convertFrom(String object) {
        final AutoBean<SystemMessageList> decode = AutoBeanCodex.decode(factory, SystemMessageList.class, object);
        return decode.as().getSystemMessages();
    }

}
