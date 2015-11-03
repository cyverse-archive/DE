package org.iplantc.de.admin.desktop.client.systemMessage.service.impl;

import org.iplantc.de.client.models.systemMessages.SystemMessageFactory;
import org.iplantc.de.client.models.systemMessages.SystemMessageTypesList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class SystemMessageTypeListCallbackConverter extends AsyncCallbackConverter<String, List<String>> {

    private final SystemMessageFactory factory;

    public SystemMessageTypeListCallbackConverter(AsyncCallback<List<String>> callback, SystemMessageFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<String> convertFrom(String object) {
        final AutoBean<SystemMessageTypesList> decode = AutoBeanCodex.decode(factory, SystemMessageTypesList.class, object);
        return decode.as().getSystemMessageTypes();
    }

}
