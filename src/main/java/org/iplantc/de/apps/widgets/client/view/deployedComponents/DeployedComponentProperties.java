package org.iplantc.de.apps.widgets.client.view.deployedComponents;

import org.iplantc.de.client.models.deployedComps.DeployedComponent;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 *
 */
public interface DeployedComponentProperties extends PropertyAccess<DeployedComponent> {

    ValueProvider<DeployedComponent, String> name();

    ValueProvider<DeployedComponent, String> version();

    ValueProvider<DeployedComponent, String> location();

}
