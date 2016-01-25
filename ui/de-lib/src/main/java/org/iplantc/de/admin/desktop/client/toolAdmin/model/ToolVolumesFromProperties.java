package org.iplantc.de.admin.desktop.client.toolAdmin.model;

import org.iplantc.de.client.models.tool.ToolVolumesFrom;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 * @author aramsey
 */
public interface ToolVolumesFromProperties extends PropertyAccess<ToolVolumesFrom> {

    ValueProvider<ToolVolumesFrom, String> name();

    ValueProvider<ToolVolumesFrom, String> namePrefix();

    ValueProvider<ToolVolumesFrom, String> tag();

    ValueProvider<ToolVolumesFrom, String> url();

    ValueProvider<ToolVolumesFrom, Boolean> readOnly();
}
