package org.iplantc.de.apps.integration.client.view.tools;

import org.iplantc.de.client.models.tool.Tool;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 *
 */
public interface DeployedComponentProperties extends PropertyAccess<Tool> {

    ValueProvider<Tool, String> name();

    ValueProvider<Tool, String> version();

    ValueProvider<Tool, String> location();

}
