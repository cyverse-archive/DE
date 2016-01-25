package org.iplantc.de.admin.desktop.client.toolAdmin.model;

import org.iplantc.de.client.models.tool.ToolVolume;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 * @author aramsey
 */
public interface ToolVolumeProperties extends PropertyAccess<ToolVolume> {
    ValueProvider<ToolVolume, String> hostPath();

    ValueProvider<ToolVolume, String> containerPath();
}
