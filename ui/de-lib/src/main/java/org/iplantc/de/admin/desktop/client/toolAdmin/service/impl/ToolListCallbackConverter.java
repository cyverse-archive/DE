package org.iplantc.de.admin.desktop.client.toolAdmin.service.impl;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolList;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

/**
 * @author aramsey
 */
public class ToolListCallbackConverter extends AsyncCallbackConverter<String, List<Tool>> {

    private final ToolAutoBeanFactory factory;

    public ToolListCallbackConverter(AsyncCallback<List<Tool>> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<Tool> convertFrom(String object) {
        final AutoBean<ToolList> decode = AutoBeanCodex.decode(factory, ToolList.class, object);
        return decode.as().getToolList();
    }
}
