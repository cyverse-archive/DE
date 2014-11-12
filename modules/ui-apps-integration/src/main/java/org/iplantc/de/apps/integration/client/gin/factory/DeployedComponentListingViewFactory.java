package org.iplantc.de.apps.integration.client.gin.factory;

import org.iplantc.de.apps.integration.client.view.tools.DeployedComponentsListingView;
import org.iplantc.de.client.models.tool.Tool;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;

/**
 * @author jstroot
 */
public interface DeployedComponentListingViewFactory {
    DeployedComponentsListingView createDcListingView(ListStore<Tool> listStore,
                                                      SelectionChangedEvent.SelectionChangedHandler<Tool> handler);
}
