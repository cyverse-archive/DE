package org.iplantc.de.admin.desktop.client.toolAdmin.model;

import org.iplantc.de.client.models.tool.Tool;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author jstroot
 * @author aramsey
 */
public interface ToolProperties extends PropertyAccess<Tool> {

    ModelKeyProvider<Tool> id();

    ValueProvider<Tool, String> name();

    ValueProvider<Tool, String> description();

    ValueProvider<Tool, String> location();

    ValueProvider<Tool, String> type();

    ValueProvider<Tool, String> attribution();

    ValueProvider<Tool, String> version();

}
