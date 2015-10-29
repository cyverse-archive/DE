package org.iplantc.de.admin.desktop.client.toolAdmin.service.impl;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * Created by aramsey on 10/28/15.
 */


public class ToolCallbackConverter extends AsyncCallbackConverter<String, Tool>{

    private final ToolAutoBeanFactory factory;

    public ToolCallbackConverter(AsyncCallback<Tool> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected Tool convertFrom(String object) {
        final AutoBean<Tool> decode = AutoBeanCodex.decode(factory, Tool.class, object);
        return decode.as();
    }
}
