package org.iplantc.de.admin.desktop.client.toolRequest.service.impl;

import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestAdminAutoBeanFactory;
import org.iplantc.de.client.models.toolRequest.ToolRequestList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class ToolRequestListCallbackConverter extends AsyncCallbackConverter<String, List<ToolRequest>> {

    private final ToolRequestAdminAutoBeanFactory factory;

    public ToolRequestListCallbackConverter(AsyncCallback<List<ToolRequest>> callback, ToolRequestAdminAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<ToolRequest> convertFrom(String object) {
        final AutoBean<ToolRequestList> decode = AutoBeanCodex.decode(factory, ToolRequestList.class, object);
        return decode.as().getToolRequests();
    }

}
