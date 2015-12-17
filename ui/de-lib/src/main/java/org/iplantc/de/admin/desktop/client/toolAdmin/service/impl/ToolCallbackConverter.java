package org.iplantc.de.admin.desktop.client.toolAdmin.service.impl;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolDevice;
import org.iplantc.de.client.models.tool.ToolVolume;
import org.iplantc.de.client.models.tool.ToolVolumesFrom;
import org.iplantc.de.client.services.converters.AsyncCallbackConverter;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author aramsey
 */
public class ToolCallbackConverter extends AsyncCallbackConverter<String, Tool> {

    private final ToolAutoBeanFactory factory;

    public ToolCallbackConverter(AsyncCallback<Tool> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected Tool convertFrom(String object) {
        final AutoBean<Tool> decode = AutoBeanCodex.decode(factory, Tool.class, object);
        /**
         * If you try to edit a Tool that was created without a Device/Volume/VolumeFrom list,
         * for some reason, you cannot add a list to the outgoing JSON on save.
         * Adding empty lists here as a temporary fix.
         */
        Tool tool = decode.as();
        if (tool.getContainer().getContainerVolumes() == null) {
            tool.getContainer().setContainerVolumes(Lists.<ToolVolume>newArrayList());
        }
        if (tool.getContainer().getContainerVolumesFrom() == null) {
            tool.getContainer().setContainerVolumesFrom(Lists.<ToolVolumesFrom>newArrayList());
        }
        if (tool.getContainer().getDeviceList() == null) {
            tool.getContainer().setDeviceList(Lists.<ToolDevice>newArrayList());
        }
        return tool;
    }
}
