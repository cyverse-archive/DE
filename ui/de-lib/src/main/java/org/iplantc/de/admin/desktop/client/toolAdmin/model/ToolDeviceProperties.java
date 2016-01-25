package org.iplantc.de.admin.desktop.client.toolAdmin.model;

import org.iplantc.de.client.models.tool.ToolDevice;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 * @author aramsey
 */
public interface ToolDeviceProperties extends PropertyAccess<ToolDevice> {

    ValueProvider<ToolDevice, String> hostPath();

    ValueProvider<ToolDevice, String> containerPath();
}
