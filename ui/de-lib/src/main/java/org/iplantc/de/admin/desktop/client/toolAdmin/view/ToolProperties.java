package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import org.iplantc.de.client.models.tool.Tool;

/**
 * Created by aramsey on 10/27/15.
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
