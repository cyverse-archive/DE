package org.iplantc.de.admin.desktop.client.toolAdmin.service.impl;

import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequest;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Created by aramsey on 10/28/15.
 */


public class ToolImportUpdateRequestCallbackConverter extends AsyncCallbackConverter<String, ToolImportUpdateRequest>{

    private final ToolAutoBeanFactory factory;

    public ToolImportUpdateRequestCallbackConverter(AsyncCallback<ToolImportUpdateRequest> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected ToolImportUpdateRequest convertFrom(String object) {
        final AutoBean<ToolImportUpdateRequest> decode = AutoBeanCodex.decode(factory, ToolImportUpdateRequest.class, object);
        return decode.as();
    }
}
